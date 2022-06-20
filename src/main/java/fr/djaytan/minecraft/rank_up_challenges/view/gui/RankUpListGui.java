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

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.RankService;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.Rank;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.RankConfig;
import fr.djaytan.minecraft.rank_up_challenges.view.item.GoToMainMenuItem;
import fr.djaytan.minecraft.rank_up_challenges.view.item.rankup.RankItem;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankUpListGui {

  private final GoToMainMenuItem goToMainMenuItem;
  private final MiniMessage miniMessage;
  private final RankConfig rankConfig;
  private final RankItem rankItem;
  private final RankService rankService;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankUpListGui(
      @NotNull GoToMainMenuItem goToMainMenuItem,
      @NotNull MiniMessage miniMessage,
      @NotNull RankConfig rankConfig,
      @NotNull RankItem rankItem,
      @NotNull RankService rankService,
      @NotNull ResourceBundle resourceBundle) {
    this.goToMainMenuItem = goToMainMenuItem;
    this.miniMessage = miniMessage;
    this.rankConfig = rankConfig;
    this.rankItem = rankItem;
    this.rankService = rankService;
    this.resourceBundle = resourceBundle;
  }

  public void open(@NotNull Player whoOpen) {
    int nbRows = (int) Math.ceil(rankConfig.getRanks().size() / 9.0D) + 2;

    Gui gui =
        Gui.gui()
            .title(
                miniMessage.deserialize(
                    resourceBundle.getString("rank_up_challenges.rankup.ranks.gui.title")))
            .rows(nbRows)
            .create();

    for (Rank rank : rankConfig.getRanks()) {
      boolean isRankOwned = rankService.isRankOwned(whoOpen, rank.getId());
      boolean isCurrentRank = rankService.isCurrentRank(whoOpen, rank.getId());
      boolean isUnlockableRank = rankService.isUnlockableRank(whoOpen, rank.getId());

      GuiItem rankGuiItem = rankItem.createItem(rank, isRankOwned, isCurrentRank, isUnlockableRank);
      gui.addItem(rankGuiItem);
    }

    gui.setItem(nbRows, 1, goToMainMenuItem.createItem());

    gui.setDefaultClickAction(event -> event.setCancelled(true));

    gui.open(whoOpen);
  }
}
