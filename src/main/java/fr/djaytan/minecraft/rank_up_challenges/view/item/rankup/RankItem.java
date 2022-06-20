/*
 * Copyright (c) 2022 - Loïc DUBOIS-TERMOZ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.djaytan.minecraft.rank_up_challenges.view.item.rankup;

import com.google.common.base.Preconditions;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.RankUpController;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.Rank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

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
      @NotNull Rank rank, boolean isRankOwned, boolean isCurrentRank, boolean isUnlockableRank) {
    Preconditions.checkState(
        !(isUnlockableRank && isRankOwned),
        "A rank can't be unlockable and owned at the same time.");
    Preconditions.checkState(
        !(isUnlockableRank && isCurrentRank),
        "A rank can't be unlockable and be the current rank of the player at the same time.");

    Component itemName = getName(rank, isUnlockableRank, isRankOwned);
    List<Component> itemLore = getLore(rank, isUnlockableRank, isRankOwned);

    ItemBuilder itemBuilder =
        ItemBuilder.from(Material.LEATHER_CHESTPLATE)
            .color(
                Color.fromRGB(
                    rank.getColor().red(), rank.getColor().green(), rank.getColor().blue()))
            .name(itemName)
            .lore(itemLore)
            .flags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

    if (isCurrentRank) {
      itemBuilder.enchant(Enchantment.DURABILITY);
    }

    if (isClickable(rank.isRankUpActivated(), isRankOwned, isUnlockableRank)) {
      return itemBuilder.asGuiItem();
    }
    return itemBuilder.asGuiItem(onClick(rank));
  }

  private @NotNull GuiAction<InventoryClickEvent> onClick(Rank rank) {
    return event -> {
      Player player = (Player) event.getWhoClicked();
      rankUpController.openRankUpChallengesGui(player, rank);
    };
  }

  private boolean isClickable(
      boolean isRankUpActivated, boolean isRankOwned, boolean isUnlockableRank) {
    return !isRankUpActivated || !isRankOwned && !isUnlockableRank;
  }

  private @NotNull Component getName(
      @NotNull Rank rank, boolean isUnlockableRank, boolean isRankOwned) {
    String rankStatusKey = "diagonia.rankup.ranks.item.name.locked";

    if (isUnlockableRank) {
      rankStatusKey = "diagonia.rankup.ranks.item.name.unlocked";
    }
    if (isRankOwned) {
      rankStatusKey = "diagonia.rankup.ranks.item.name.owned";
    }

    // Override previous assignment by having the final word
    if (!rank.isRankUpActivated()) {
      rankStatusKey = "diagonia.rankup.ranks.item.name.deactivated";
    }

    return miniMessage
        .deserialize(
            resourceBundle.getString("diagonia.rankup.ranks.item.name"),
            TagResolver.resolver(
                Placeholder.component(
                    "diag_rank_name", Component.text(rank.getName()).color(rank.getColor())),
                Placeholder.parsed("diag_rank_status", resourceBundle.getString(rankStatusKey))))
        .decoration(TextDecoration.ITALIC, false);
  }

  private @NotNull @UnmodifiableView List<Component> getLore(
      @NotNull Rank rank, boolean isUnlockableRank, boolean isRankOwned) {
    List<Component> lore = new ArrayList<>();
    lore.addAll(getDescriptionLorePart(rank));
    lore.add(Component.empty());
    lore.addAll(getProfitsLorePart(rank));
    lore.add(Component.empty());
    lore.addAll(getEndLorePart(rank.isRankUpActivated(), isUnlockableRank, isRankOwned));
    return Collections.unmodifiableList(lore);
  }

  private @NotNull @UnmodifiableView List<Component> getDescriptionLorePart(@NotNull Rank rank) {
    return rank.getDescription().stream()
        .map(
            descLine ->
                miniMessage
                    .deserialize(descLine)
                    .color(TextColor.color(Color.GRAY.asRGB()))
                    .decoration(TextDecoration.ITALIC, false))
        .toList();
  }

  private @NotNull @UnmodifiableView List<Component> getProfitsLorePart(@NotNull Rank rank) {
    List<Component> profitsLore = new ArrayList<>();
    profitsLore.add(
        miniMessage
            .deserialize(resourceBundle.getString("diagonia.rankup.ranks.item.lore.profits"))
            .decoration(TextDecoration.ITALIC, false));

    rank.getProfits()
        .forEach(
            profitDescLine ->
                profitsLore.add(
                    miniMessage
                        .deserialize(
                            resourceBundle.getString("diagonia.rankup.ranks.item.lore.profit"),
                            TagResolver.resolver(
                                Placeholder.unparsed("diag_profit", profitDescLine)))
                        .decoration(TextDecoration.ITALIC, false)));

    return Collections.unmodifiableList(profitsLore);
  }

  private @NotNull @Unmodifiable List<Component> getEndLorePart(
      boolean isRankActivated, boolean isUnlockableRank, boolean isRankOwned) {
    String endLoreKey = "diagonia.rankup.ranks.item.lore.locked";

    if (isUnlockableRank || isRankOwned) {
      endLoreKey = "diagonia.rankup.ranks.item.lore.unlocked";
    }

    // Override previous assignment by having the final word
    if (!isRankActivated) {
      endLoreKey = "diagonia.rankup.ranks.item.lore.deactivated";
    }

    return List.of(
        miniMessage
            .deserialize(resourceBundle.getString(endLoreKey))
            .decoration(TextDecoration.ITALIC, false));
  }
}
