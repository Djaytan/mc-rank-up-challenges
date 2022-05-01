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

import com.google.common.base.Preconditions;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.rankup.RankUpChallengesController;
import fr.voltariuss.diagonia.model.config.data.rank.Rank;
import fr.voltariuss.diagonia.model.config.data.rank.RankUpPrerequisites;
import fr.voltariuss.diagonia.model.dto.RankUpProgression;
import fr.voltariuss.diagonia.view.EconomyFormatter;
import java.util.ArrayList;
import java.util.Collections;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

@Singleton
public class RankUpItem {

  private static final Material RANK_UP_MATERIAL = Material.WRITABLE_BOOK;

  private final EconomyFormatter economyFormatter;
  private final MiniMessage miniMessage;
  private final RankUpChallengesController rankUpChallengesController;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankUpItem(
      @NotNull EconomyFormatter economyFormatter,
      @NotNull MiniMessage miniMessage,
      @NotNull RankUpChallengesController rankUpChallengesController,
      @NotNull ResourceBundle resourceBundle) {
    this.economyFormatter = economyFormatter;
    this.miniMessage = miniMessage;
    this.rankUpChallengesController = rankUpChallengesController;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem(
      @NotNull Rank rank, @NotNull RankUpProgression rankUpProgression) {
    Preconditions.checkNotNull(
        rank.getRankUpPrerequisites(), "Rankup prerequisites shouldn't be null here.");

    RankUpPrerequisites rankUpPrerequisites = rank.getRankUpPrerequisites();

    Component itemName = getName(rankUpProgression.isRankOwned());
    List<Component> itemLore = getLore(rankUpProgression, rankUpPrerequisites);

    ItemBuilder itemBuilder = ItemBuilder.from(RANK_UP_MATERIAL).name(itemName).lore(itemLore);

    return itemBuilder.asGuiItem(onClick(rankUpProgression));
  }

  private @NotNull GuiAction<InventoryClickEvent> onClick(
      @NotNull RankUpProgression rankUpProgression) {
    return event -> {
      Player player = (Player) event.getWhoClicked();
      rankUpChallengesController.onRankUpRequested(player, rankUpProgression);
    };
  }

  private @NotNull Component getName(boolean isRankOwned) {
    String nameKey = "diagonia.rankup.rankup.item.name.unlockable";

    if (isRankOwned) {
      nameKey = "diagonia.rankup.rankup.item.name.already_unlocked";
    }

    return miniMessage
        .deserialize(resourceBundle.getString(nameKey))
        .decoration(TextDecoration.ITALIC, false);
  }

  private @NotNull @UnmodifiableView List<Component> getLore(
      @NotNull RankUpProgression rankUpProgression,
      @NotNull RankUpPrerequisites rankUpPrerequisites) {
    if (rankUpProgression.isRankOwned()) {
      return Collections.emptyList();
    }

    List<Component> lore = new ArrayList<>();
    lore.addAll(getPrerequisitesLorePart(rankUpProgression, rankUpPrerequisites));
    lore.add(Component.empty());
    lore.addAll(getEndLorePart(rankUpProgression.canRankUp()));

    return Collections.unmodifiableList(lore);
  }

  private @NotNull @UnmodifiableView List<Component> getPrerequisitesLorePart(
      @NotNull RankUpProgression rankUpProgression,
      @NotNull RankUpPrerequisites rankUpPrerequisites) {
    return List.of(
        miniMessage
            .deserialize(
                resourceBundle.getString(
                    "diagonia.rankup.rankup.item.lore.prerequisite.required.minecraft_xp"),
                TagResolver.resolver(
                    Placeholder.component(
                        "diag_current_level",
                        getCurrentProgression(
                            rankUpProgression.isXpLevelPrerequisiteDone(),
                            String.valueOf(rankUpProgression.getCurrentXpLevel()))),
                    Placeholder.unparsed(
                        "diag_required_level",
                        String.valueOf(rankUpPrerequisites.getEnchantingLevelsCost()))))
            .decoration(TextDecoration.ITALIC, false),
        miniMessage
            .deserialize(
                resourceBundle.getString(
                    "diagonia.rankup.rankup.item.lore.prerequisite.required.jobs_levels"),
                TagResolver.resolver(
                    Placeholder.component(
                        "diag_current_level",
                        getCurrentProgression(
                            rankUpProgression.isTotalJobsLevelsPrerequisiteDone(),
                            String.valueOf(rankUpProgression.getTotalJobsLevels()))),
                    Placeholder.unparsed(
                        "diag_required_level",
                        String.valueOf(rankUpPrerequisites.getJobsLevels()))))
            .decoration(TextDecoration.ITALIC, false),
        miniMessage
            .deserialize(
                resourceBundle.getString(
                    "diagonia.rankup.rankup.item.lore.prerequisite.required.money"),
                TagResolver.resolver(
                    Placeholder.component(
                        "diag_current_balance",
                        getCurrentProgression(
                            rankUpProgression.isMoneyPrerequisiteDone(),
                            economyFormatter.format(rankUpProgression.getCurrentBalance()))),
                    Placeholder.unparsed(
                        "diag_rankup_price",
                        economyFormatter.format(rankUpPrerequisites.getMoneyCost()))))
            .decoration(TextDecoration.ITALIC, false));
  }

  private @NotNull @UnmodifiableView List<Component> getEndLorePart(boolean canRankUp) {
    String canRankUpKey = "diagonia.rankup.rankup.item.lore.prerequisites_not_respected";

    if (canRankUp) {
      canRankUpKey = "diagonia.rankup.rankup.item.lore.unlock_rank";
    }

    return List.of(
        miniMessage
            .deserialize(resourceBundle.getString(canRankUpKey))
            .decoration(TextDecoration.ITALIC, false));
  }

  private @NotNull Component getCurrentProgression(
      boolean isPrerequisiteValidated, @NotNull String value) {
    String templateMessageKey =
        "diagonia.rankup.rankup.item.lore.prerequisite.progression.insufficient";

    if (isPrerequisiteValidated) {
      templateMessageKey = "diagonia.rankup.rankup.item.lore.prerequisite.progression.sufficient";
    }

    return miniMessage.deserialize(
        resourceBundle.getString(templateMessageKey),
        TagResolver.resolver(Placeholder.unparsed("diag_current_progression", value)));
  }
}
