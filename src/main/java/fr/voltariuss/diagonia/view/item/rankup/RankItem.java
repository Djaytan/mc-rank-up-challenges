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

package fr.voltariuss.diagonia.view.item.rankup;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.RankUpController;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
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
      @NotNull Rank rank, boolean isRankOwned, boolean isCurrentRank, boolean isUnlockableRank) {
    List<Component> introProfits = new ArrayList<>();
    introProfits.add(Component.empty());
    introProfits.add(
        miniMessage
            .deserialize(resourceBundle.getString("diagonia.rankup.rank_list.profits"))
            .decoration(TextDecoration.ITALIC, false));

    List<Component> endRank = new ArrayList<>();
    endRank.add(Component.empty());
    if (rank.isRankUpActivated()) {
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

    // TODO: simplify instruction and reorganize
    ItemBuilder itemBuilder =
        ItemBuilder.from(Material.LEATHER_CHESTPLATE)
            .color(
                Color.fromRGB(
                    rank.getColor().red(), rank.getColor().green(), rank.getColor().blue()))
            .name(
                Component.text(rank.getName())
                    .color(rank.getColor())
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
                        rank.getDescription().stream()
                            .map(
                                descLine ->
                                    miniMessage
                                        .deserialize(descLine)
                                        .color(TextColor.color(Color.GRAY.asRGB()))
                                        .decoration(TextDecoration.ITALIC, false)),
                        Stream.concat(
                            introProfits.stream(),
                            Stream.concat(
                                rank.getProfits().stream()
                                    .map(
                                        profitDescLine ->
                                            miniMessage
                                                .deserialize(
                                                    resourceBundle.getString(
                                                        "diagonia.rankup.rank_list.profit"),
                                                    TemplateResolver.templates(
                                                        Template.template(
                                                            "diag_profit", profitDescLine)))
                                                .decoration(TextDecoration.ITALIC, false)),
                                endRank.stream())))
                    .toList())
            .flags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

    if (isCurrentRank) {
      itemBuilder.enchant(Enchantment.DURABILITY);
    }

    if (!rank.isRankUpActivated() || !isRankOwned && !isUnlockableRank) {
      return itemBuilder.asGuiItem();
    }
    return itemBuilder.asGuiItem(onClick(rank));
  }

  public @NotNull GuiAction<InventoryClickEvent> onClick(Rank rank) {
    return event -> {
      Player player = (Player) event.getWhoClicked();
      rankUpController.openRankUpGui(player, rank);
    };
  }
}
