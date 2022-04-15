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

import com.google.common.base.Preconditions;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import fr.voltariuss.diagonia.controller.rankup.RankUpChallengesController;
import fr.voltariuss.diagonia.controller.rankup.RankUpController;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.dto.RankUpProgression;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import fr.voltariuss.diagonia.view.item.PaginatedItem;
import fr.voltariuss.diagonia.view.item.rankup.RankChallengeItem;
import fr.voltariuss.diagonia.view.item.rankup.RankUpItem;
import java.util.Optional;
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
  private final RankUpChallengesController rankUpChallengesController;
  private final RankUpController rankUpController;
  private final RankUpItem rankUpItem;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankUpChallengesGui(
      @NotNull MiniMessage miniMessage,
      @NotNull PaginatedItem paginatedItem,
      @NotNull RankChallengeItem rankChallengeItem,
      @NotNull RankUpChallengesController rankUpChallengesController,
      @NotNull RankUpController rankUpController,
      @NotNull RankUpItem rankUpItem,
      @NotNull ResourceBundle resourceBundle) {
    this.miniMessage = miniMessage;
    this.paginatedItem = paginatedItem;
    this.rankChallengeItem = rankChallengeItem;
    this.rankUpChallengesController = rankUpChallengesController;
    this.rankUpController = rankUpController;
    this.rankUpItem = rankUpItem;
    this.resourceBundle = resourceBundle;
  }

  public void open(
      @NotNull Player whoOpen, @NotNull Rank rank, @NotNull RankUpProgression rankUpProgression) {
    Preconditions.checkNotNull(rank.getRankUpChallenges());

    PaginatedGui gui =
        Gui.paginated()
            .pageSize(PAGE_SIZE)
            .rows(6)
            .title(
                miniMessage.deserialize(
                    resourceBundle.getString("diagonia.rankup.challenges.gui.title"),
                    TagResolver.resolver(Placeholder.unparsed("diag_rank", rank.getName()))))
            .create();

    GuiItem decorationItem =
        ItemBuilder.from(DECORATION_MATERIAL).name(Component.empty()).asGuiItem();

    for (int i = 1; i <= 9; i++) {
      gui.setItem(5, i, decorationItem);
    }

    rank.getRankUpChallenges()
        .forEach(
            rankChallenge -> {
              Optional<RankChallengeProgression> rankChallengeProgression =
                  rankUpChallengesController.findChallenge(
                      whoOpen.getUniqueId(), rank.getId(), rankChallenge.getMaterial());

              gui.addItem(
                  rankChallengeItem.createItem(
                      rank, rankChallenge, rankChallengeProgression.orElse(null)));
            });

    gui.setItem(6, 5, rankUpItem.createItem(rank, rankUpProgression));

    if (rank.getRankUpChallenges().size() > PAGE_SIZE) {
      gui.setItem(5, 3, paginatedItem.createPreviousPageItem(gui));
      gui.setItem(5, 7, paginatedItem.createNextPageItem(gui));
    }

    gui.setItem(
        6,
        1,
        ItemBuilder.from(PREVIOUS_GUI_MATERIAL)
            .name(
                miniMessage
                    .deserialize(resourceBundle.getString("diagonia.gui.go_to_previous_menu"))
                    .decoration(TextDecoration.ITALIC, false))
            .asGuiItem(
                event -> rankUpController.openRankUpListGui((Player) event.getWhoClicked())));

    gui.setDefaultClickAction(event -> event.setCancelled(true));

    gui.open(whoOpen);
  }
}
