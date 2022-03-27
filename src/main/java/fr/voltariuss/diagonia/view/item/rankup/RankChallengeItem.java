package fr.voltariuss.diagonia.view.item.rankup;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.RankUpController;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.config.rank.RankChallenge;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
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
      @NotNull UUID playerUuid, @NotNull Rank rank, @NotNull RankChallenge rankChallenge) {
    RankChallengeProgression rcp =
        rankUpController
            .findChallenge(playerUuid, rank.getId(), rankChallenge.getChallengeItemMaterial())
            .orElse(null);

    boolean isChallengeCompleted =
        rcp != null && rankChallenge.getChallengeItemAmount() == rcp.getChallengeAmountGiven();

    // TODO: refactoring - remove duplication
    if (!isChallengeCompleted) {
      return ItemBuilder.from(rankChallenge.getChallengeItemMaterial())
          .name(
              miniMessage
                  .deserialize(
                      resourceBundle.getString("diagonia.rankup.rankup.challenge.name"),
                      TemplateResolver.templates(
                          Template.template(
                              "diag_challenge_name",
                              rankChallenge.getChallengeItemMaterial().name())))
                  .decoration(TextDecoration.ITALIC, false))
          .lore(
              Stream.concat(
                      Stream.of(
                          miniMessage
                              .deserialize(
                                  resourceBundle.getString(
                                      "diagonia.rankup.rankup.challenge.progress"),
                                  TemplateResolver.templates(
                                      Template.template(
                                          "diag_amount_given",
                                          String.valueOf(
                                              rcp != null ? rcp.getChallengeAmountGiven() : 0)),
                                      Template.template(
                                          "diag_amount_required",
                                          String.valueOf(rankChallenge.getChallengeItemAmount()))))
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
                                      "diagonia.rankup.rankup.challenge.shift_right_click.1"))
                              .decoration(TextDecoration.ITALIC, false),
                          miniMessage
                              .deserialize(
                                  resourceBundle.getString(
                                      "diagonia.rankup.rankup.challenge.shift_right_click.2"))
                              .decoration(TextDecoration.ITALIC, false)))
                  .toList())
          .asGuiItem(onClick(rank, rankChallenge));
    }
    return ItemBuilder.from(rankChallenge.getChallengeItemMaterial())
        .name(
            miniMessage
                .deserialize(
                    resourceBundle.getString("diagonia.rankup.rankup.challenge.name"),
                    TemplateResolver.templates(
                        Template.template(
                            "diag_challenge_name",
                            rankChallenge.getChallengeItemMaterial().name())))
                .decoration(TextDecoration.ITALIC, false))
        .lore(
            List.of(
                miniMessage
                    .deserialize(
                        resourceBundle.getString("diagonia.rankup.rankup.challenge.completed"))
                    .decoration(TextDecoration.ITALIC, false)))
        .asGuiItem();
  }

  public @NotNull GuiAction<InventoryClickEvent> onClick(
      @NotNull Rank rank, @NotNull RankChallenge rankChallenge) {
    // TODO: create a real event with Observer pattern or Bukkit API
    return event -> {
      Player whoClicked = (Player) event.getWhoClicked();
      ClickType clickType = event.getClick();
      ItemStack clickedItem = event.getCurrentItem();
      // TODO: move business logic into model
      if (clickedItem != null
          && (clickType == ClickType.LEFT
              || clickType == ClickType.RIGHT
              || clickType == ClickType.SHIFT_RIGHT)) {
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
        if (clickType == ClickType.SHIFT_RIGHT) {
          amountToGive = nbItemsInInventory;
        }
        if (amountToGive > 0) {
          logger.debug("GUI-amountToGive={}", amountToGive);
          int effectiveGivenAmount =
              rankUpController.giveItemChallenge(
                  whoClicked, rank.getId(), clickedItem.getType(), amountToGive);
          logger.debug("effectiveGivenAmount={}", effectiveGivenAmount);
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
                    resourceBundle.getString("diagonia.rankup.rankup.challenge.success_give"),
                    TemplateResolver.templates(
                        Template.template(
                            "diag_amount_given", String.valueOf(effectiveGivenAmount)),
                        Template.template("diag_item_name", clickedItem.getType().name()))));
            RankChallengeProgression rcp =
                rankUpController
                    .findChallenge(whoClicked.getUniqueId(), rank.getId(), clickedItem.getType())
                    .orElseThrow();
            if (rcp.getChallengeAmountGiven() == rankChallenge.getChallengeItemAmount()) {
              whoClicked.sendMessage(
                  miniMessage.deserialize(
                      resourceBundle.getString("diagonia.rankup.rankup.challenge.now_completed"),
                      TemplateResolver.templates(
                          Template.template(
                              "diag_challenge_name",
                              rankChallenge.getChallengeItemMaterial().name()))));
            }
            rankUpController.openRankUpGui(whoClicked, rank);
          } else if (effectiveGivenAmount == 0) {
            whoClicked.sendMessage(
                miniMessage.deserialize(
                    resourceBundle.getString(
                        "diagonia.rankup.rankup.challenge.challenge_already_completed")));
          }
          return;
        }
        if (amountToGive == 0) {
          whoClicked.sendMessage(
              miniMessage
                  .deserialize(
                      resourceBundle.getString(
                          "diagonia.rankup.rankup.challenge.no_item_in_inventory"))
                  .decoration(TextDecoration.ITALIC, false));
          return;
        }
        whoClicked.sendMessage(
            miniMessage
                .deserialize(
                    resourceBundle.getString("diagonia.rankup.rankup.challenge.not_enough_item"))
                .decoration(TextDecoration.ITALIC, false));
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
