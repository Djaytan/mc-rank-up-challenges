package fr.voltariuss.diagonia.view.item.rankup;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.RankUpController;
import fr.voltariuss.diagonia.model.config.RankConfig;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
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
      @NotNull RankConfig.RankInfo rankInfo,
      @NotNull RankConfig.RankChallenge rankChallenge,
      int amountGiven) {
    return ItemBuilder.from(rankChallenge.getChallengeItemMaterial())
        .name(
            miniMessage
                .deserialize(
                    String.format(
                        resourceBundle.getString("diagonia.rankup.rankup.challenge.name"),
                        rankChallenge.getChallengeItemMaterial().name()))
                .decoration(TextDecoration.ITALIC, false))
        .lore(
            Collections.singletonList(
                miniMessage
                    .deserialize(
                        String.format(
                            resourceBundle.getString("diagonia.rankup.rankup.challenge.progress"),
                            amountGiven,
                            rankChallenge.getChallengeItemAmount()))
                    .decoration(TextDecoration.ITALIC, false)))
        .asGuiItem(onClick(rankInfo));
  }

  public @NotNull GuiAction<InventoryClickEvent> onClick(@NotNull RankConfig.RankInfo rankInfo) {
    return event -> {
      Player whoClicked = (Player) event.getWhoClicked();
      ClickType clickType = event.getClick();
      ItemStack clickedItem = event.getCurrentItem();
      if (clickedItem != null) {
        int amountToGive = 0;
        switch (clickType) {
          case LEFT:
            {
              amountToGive = 1;
              break;
            }
          case RIGHT:
            {
              amountToGive = 64;
              break;
            }
        }
        if (clickType == ClickType.LEFT
            || clickType == ClickType.RIGHT
            || clickType == ClickType.MIDDLE) {
          amountToGive =
              Math.min(countItem(whoClicked.getInventory(), clickedItem.getType()), amountToGive);
        }
        logger.info("amountToGive={}", amountToGive);
        int effectiveGivenAmount =
            rankUpController.giveItemChallenge(
                whoClicked, rankInfo.getId(), clickedItem.getType(), amountToGive);
        logger.info("effectiveGivenAmount={}", effectiveGivenAmount);
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
        rankUpController.openRankUpGui(whoClicked, rankInfo);
      }
    };
  }

  public int countItem(Inventory inventory, Material material) {
    return (int)
        Arrays.stream(inventory.getContents())
            .filter(item -> item != null && item.getType().equals(material))
            .count();
  }
}
