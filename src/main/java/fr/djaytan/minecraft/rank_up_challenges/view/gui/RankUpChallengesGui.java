/*
 * Rank-Up challenges plugin for Minecraft (Bukkit servers)
 * Copyright (C) 2022 - Loïc DUBOIS-TERMOZ
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.djaytan.minecraft.rank_up_challenges.view.gui;

import com.google.common.base.Preconditions;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.RankUpController;
import fr.djaytan.minecraft.rank_up_challenges.model.entity.RankChallengeProgression;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.dto.RankUpProgression;
import fr.djaytan.minecraft.rank_up_challenges.view.item.rankup.RankChallengeItem;
import fr.djaytan.minecraft.rank_up_challenges.view.item.rankup.RankUpItem;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.Rank;
import fr.djaytan.minecraft.rank_up_challenges.view.item.PaginatedItem;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankUpChallengesGui {

  private static final Material PREVIOUS_GUI_MATERIAL = Material.ARROW;
  private static final Material DECORATION_MATERIAL = Material.GRAY_STAINED_GLASS_PANE;
  private static final int PAGE_SIZE = 36;

  private final MiniMessage miniMessage;
  private final PaginatedItem paginatedItem;
  private final RankChallengeItem rankChallengeItem;
  private final RankUpController rankUpController;
  private final RankUpItem rankUpItem;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankUpChallengesGui(
      @NotNull MiniMessage miniMessage,
      @NotNull PaginatedItem paginatedItem,
      @NotNull RankChallengeItem rankChallengeItem,
      @NotNull RankUpController rankUpController,
      @NotNull RankUpItem rankUpItem,
      @NotNull ResourceBundle resourceBundle) {
    this.miniMessage = miniMessage;
    this.paginatedItem = paginatedItem;
    this.rankChallengeItem = rankChallengeItem;
    this.rankUpController = rankUpController;
    this.rankUpItem = rankUpItem;
    this.resourceBundle = resourceBundle;
  }

  public void open(
      @NotNull Player whoOpen,
      @NotNull Rank rank,
      @NotNull RankUpProgression rankUpProgression,
      @NotNull List<RankChallengeProgression> rankChallengeProgressions) {
    Preconditions.checkNotNull(rank.getRankUpChallenges());

    PaginatedGui gui =
        Gui.paginated()
            .pageSize(PAGE_SIZE)
            .rows(6)
            .title(
                miniMessage.deserialize(
                    resourceBundle.getString("rank_up_challenges.rankup.challenges.gui.title"),
                    TagResolver.resolver(Placeholder.unparsed("ruc_rank", rank.getName()))))
            .create();

    GuiItem decorationItem =
        ItemBuilder.from(DECORATION_MATERIAL).name(Component.empty()).asGuiItem();

    for (int i = 1; i <= 9; i++) {
      gui.setItem(5, i, decorationItem);
    }

    for (RankChallengeProgression rankChallengeProgression : rankChallengeProgressions) {
      gui.addItem(rankChallengeItem.createItem(rank, rankChallengeProgression));
    }

    gui.setItem(6, 5, rankUpItem.createItem(rank, rankUpProgression));

    if (rankChallengeProgressions.size() > PAGE_SIZE) {
      gui.setItem(5, 3, paginatedItem.createPreviousPageItem(gui));
      gui.setItem(5, 7, paginatedItem.createNextPageItem(gui));
    }

    gui.setItem(
        6,
        1,
        ItemBuilder.from(PREVIOUS_GUI_MATERIAL)
            .name(
                miniMessage
                    .deserialize(resourceBundle.getString("rank_up_challenges.gui.go_to_previous_menu"))
                    .decoration(TextDecoration.ITALIC, false))
            .asGuiItem(
                event -> rankUpController.openRankUpListGui((Player) event.getWhoClicked())));

    gui.setDefaultClickAction(event -> event.setCancelled(true));

    gui.open(whoOpen);
  }
}
