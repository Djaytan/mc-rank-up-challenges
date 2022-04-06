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

package fr.voltariuss.diagonia.view.gui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.config.rank.RankConfig;
import fr.voltariuss.diagonia.model.service.RankService;
import fr.voltariuss.diagonia.view.item.GoToMainMenuItem;
import fr.voltariuss.diagonia.view.item.rankup.RankItem;
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
                    resourceBundle.getString("diagonia.rankup.ranks.gui.title")))
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
