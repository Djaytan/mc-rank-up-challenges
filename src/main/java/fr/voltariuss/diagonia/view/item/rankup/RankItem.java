package fr.voltariuss.diagonia.view.item.rankup;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.RankUpController;
import fr.voltariuss.diagonia.model.config.RankConfig;
import java.util.*;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankItem {

  private final MiniMessage miniMessage;
  private final RankUpController rankUpController;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankItem(
      @NotNull MiniMessage miniMessage,
      @NotNull RankUpController rankUpController,
      @NotNull ResourceBundle resourceBundle) {
    this.miniMessage = miniMessage;
    this.rankUpController = rankUpController;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem(
      @NotNull RankConfig.RankInfo rankInfo,
      boolean isRankOwned,
      boolean isCurrentRank,
      boolean isUnlockableRank) {
    List<Component> introProfits = new ArrayList<>();
    introProfits.add(Component.empty());
    introProfits.add(
        miniMessage
            .deserialize(resourceBundle.getString("diagonia.rankup.rank_list.profits"))
            .decoration(TextDecoration.ITALIC, false));

    List<Component> endRank = new ArrayList<>();
    endRank.add(Component.empty());
    if (rankInfo.isRankUpActivated()) {
      if (isRankOwned || isUnlockableRank) {
        endRank.add(
            miniMessage
                .deserialize(resourceBundle.getString("diagonia.rankup.rank_list.rankup_activated"))
                .decoration(TextDecoration.ITALIC, false));
      } else {
        endRank.add(
            miniMessage
                .deserialize(
                    resourceBundle.getString("diagonia.rankup.rank_list.previous_ranks_required"))
                .decoration(TextDecoration.ITALIC, false));
      }
    } else {
      endRank.add(
          miniMessage
              .deserialize(resourceBundle.getString("diagonia.rankup.rank_list.rankup_deactivated"))
              .decoration(TextDecoration.ITALIC, false));
    }

    ItemBuilder itemBuilder =
        ItemBuilder.from(Material.LEATHER_CHESTPLATE)
            .color(
                Color.fromRGB(
                    rankInfo.getColor().red(),
                    rankInfo.getColor().green(),
                    rankInfo.getColor().blue()))
            .name(
                Component.text(rankInfo.getName())
                    .color(rankInfo.getColor())
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, true)
                    .append(
                        Component.text(" ")
                            .append(
                                miniMessage.deserialize(
                                    resourceBundle.getString(
                                        isUnlockableRank
                                            ? "diagonia.rankup.rank_list.rankable"
                                            : (isRankOwned
                                                ? "diagonia.rankup.rank_list.owned"
                                                : "diagonia.rankup.rank_list.locked"))))))
            .lore(
                Stream.concat(
                        rankInfo.getDescription().stream()
                            .map(
                                descLine ->
                                    miniMessage
                                        .deserialize(descLine)
                                        .color(TextColor.color(Color.GRAY.asRGB()))
                                        .decoration(TextDecoration.ITALIC, false)),
                        Stream.concat(
                            introProfits.stream(),
                            Stream.concat(
                                rankInfo.getProfits().stream()
                                    .map(
                                        descLine ->
                                            miniMessage
                                                .deserialize(
                                                    String.format(
                                                        resourceBundle.getString(
                                                            "diagonia.rankup.rank_list.profit"),
                                                        descLine))
                                                .decoration(TextDecoration.ITALIC, false)),
                                endRank.stream())))
                    .toList())
            .flags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

    if (isCurrentRank) {
      itemBuilder.enchant(Enchantment.DURABILITY);
    }

    GuiItem guiItem;
    if (!rankInfo.isRankUpActivated() || !isRankOwned && !isUnlockableRank) {
      guiItem = itemBuilder.asGuiItem();
    } else {
      guiItem = itemBuilder.asGuiItem(onClick(rankInfo));
    }
    return guiItem;
  }

  public @NotNull GuiAction<InventoryClickEvent> onClick(RankConfig.RankInfo rankInfo) {
    return event -> rankUpController.openRankUpGui((Player) event.getWhoClicked(), rankInfo);
  }
}
