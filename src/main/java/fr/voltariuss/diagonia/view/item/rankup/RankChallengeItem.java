package fr.voltariuss.diagonia.view.item.rankup;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.RankUpController;
import fr.voltariuss.diagonia.model.config.RankConfig;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import java.util.*;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class RankChallengeItem {

  private final Logger logger;
  private final MiniMessage miniMessage;
  private final RankUpController rankUpController;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankChallengeItem(
      @NotNull Logger logger,
      @NotNull MiniMessage miniMessage,
      @NotNull RankUpController rankUpController,
      @NotNull ResourceBundle resourceBundle) {
    this.logger = logger;
    this.miniMessage = miniMessage;
    this.rankUpController = rankUpController;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem(
      @NotNull UUID playerUuid,
      @NotNull RankConfig.RankInfo rankInfo,
      @NotNull RankConfig.RankChallenge rankChallenge) {
    RankChallengeProgression rcp =
        rankUpController
            .findChallenge(playerUuid, rankInfo.getId(), rankChallenge.getChallengeItemMaterial())
            .orElse(null);

    boolean isChallengeCompleted =
        rcp != null && rankChallenge.getChallengeItemAmount() == rcp.getChallengeAmountGiven();
    if (!isChallengeCompleted) {
      return ItemBuilder.from(rankChallenge.getChallengeItemMaterial())
          .name(
              miniMessage
                  .deserialize(
                      String.format(
                          resourceBundle.getString("diagonia.rankup.rankup.challenge.name"),
                          rankChallenge.getChallengeItemMaterial().name()))
                  .decoration(TextDecoration.ITALIC, false))
          .lore(
              Stream.concat(
                      Stream.of(
                          miniMessage
                              .deserialize(
                                  String.format(
                                      resourceBundle.getString(
                                          "diagonia.rankup.rankup.challenge.progress"),
                                      rcp != null ? rcp.getChallengeAmountGiven() : 0,
                                      rankChallenge.getChallengeItemAmount()))
                              .decoration(TextDecoration.ITALIC, false),
                          Component.empty()),
                      Stream.of(
                          miniMessage
                              .deserialize(
                                  resourceBundle.getString(
                                      "diagonia.rankup.rankup.challenge.left_click"))
                              .decoration(TextDecoration.ITALIC, false),
                          miniMessage
                              .deserialize(
                                  resourceBundle.getString(
                                      "diagonia.rankup.rankup.challenge.right_click"))
                              .decoration(TextDecoration.ITALIC, false),
                          miniMessage
                              .deserialize(
                                  resourceBundle.getString(
                                      "diagonia.rankup.rankup.challenge.middle_click"))
                              .decoration(TextDecoration.ITALIC, false)))
                  .toList())
          .asGuiItem(onClick(rankInfo, rankChallenge));
    } else {
      return ItemBuilder.from(rankChallenge.getChallengeItemMaterial())
          .name(
              miniMessage
                  .deserialize(
                      String.format(
                          resourceBundle.getString("diagonia.rankup.rankup.challenge.name"),
                          rankChallenge.getChallengeItemMaterial().name()))
                  .decoration(TextDecoration.ITALIC, false))
          .lore(
              List.of(
                  miniMessage
                      .deserialize(
                          resourceBundle.getString("diagonia.rankup.rankup.challenge.completed"))
                      .decoration(TextDecoration.ITALIC, false)))
          .asGuiItem();
    }
  }

  public @NotNull GuiAction<InventoryClickEvent> onClick(
      @NotNull RankConfig.RankInfo rankInfo, @NotNull RankConfig.RankChallenge rankChallenge) {
    return event -> {
      Player whoClicked = (Player) event.getWhoClicked();
      ClickType clickType = event.getClick();
      ItemStack clickedItem = event.getCurrentItem();
      if (clickedItem != null) {
        int amountToGive = 0;
        int nbItemsInInventory = countItem(whoClicked.getInventory(), clickedItem.getType());
        if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT) {
          if (clickType == ClickType.LEFT) {
            amountToGive = 1;
          }
          if (clickType == ClickType.RIGHT) {
            amountToGive = 64;
          }
          if (nbItemsInInventory == 0) {
            amountToGive = 0;
          } else if (amountToGive > nbItemsInInventory) {
            amountToGive = -1;
          }
        }
        if (clickType == ClickType.MIDDLE) {
          amountToGive = nbItemsInInventory;
        }
        if (amountToGive > 0) {
          logger.info("GUI-amountToGive={}", amountToGive);
          int effectiveGivenAmount =
              rankUpController.giveItemChallenge(
                  whoClicked, rankInfo.getId(), clickedItem.getType(), amountToGive);
          logger.info("effectiveGivenAmount={}", effectiveGivenAmount);
          if (effectiveGivenAmount > 0) {
            HashMap<Integer, ItemStack> notRemovedItems =
                whoClicked
                    .getInventory()
                    .removeItem(new ItemStack(clickedItem.getType(), effectiveGivenAmount));
            if (!notRemovedItems.isEmpty()) {
              logger.error(
                  "Some items failed to be removed from the {}'s inventory: {}",
                  whoClicked.getName(),
                  notRemovedItems);
            }
            whoClicked.sendMessage(
                miniMessage.deserialize(
                    String.format(
                        resourceBundle.getString("diagonia.rankup.rankup.challenge.success_give"),
                        effectiveGivenAmount,
                        clickedItem.getType().name())));
            RankChallengeProgression rcp =
                rankUpController
                    .findChallenge(
                        whoClicked.getUniqueId(), rankInfo.getId(), clickedItem.getType())
                    .orElseThrow();
            if (rcp.getChallengeAmountGiven() == rankChallenge.getChallengeItemAmount()) {
              whoClicked.sendMessage(
                  miniMessage.deserialize(
                      String.format(
                          resourceBundle.getString(
                              "diagonia.rankup.rankup.challenge.now_completed"),
                          rankChallenge.getChallengeItemMaterial().name())));
            }
            rankUpController.openRankUpGui(whoClicked, rankInfo);
          } else if (effectiveGivenAmount == 0) {
            whoClicked.sendMessage(
                miniMessage.deserialize(
                    String.format(
                        resourceBundle.getString(
                            "diagonia.rankup.rankup.challenge.challenge_already_completed"),
                        effectiveGivenAmount,
                        clickedItem.getType().name())));
          }
        } else if (amountToGive == 0) {
          whoClicked.sendMessage(
              miniMessage
                  .deserialize(
                      resourceBundle.getString(
                          "diagonia.rankup.rankup.challenge.no_item_in_inventory"))
                  .decoration(TextDecoration.ITALIC, false));
        } else {
          whoClicked.sendMessage(
              miniMessage
                  .deserialize(
                      resourceBundle.getString("diagonia.rankup.rankup.challenge.not_enough_item"))
                  .decoration(TextDecoration.ITALIC, false));
        }
      }
    };
  }

  public int countItem(Inventory inventory, Material material) {
    return Arrays.stream(inventory.getContents())
        .filter(item -> item != null && item.getType().equals(material))
        .mapToInt(ItemStack::getAmount)
        .sum();
  }
}
