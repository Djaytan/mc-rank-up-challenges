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

package fr.djaytan.minecraft.rank_up_challenges.view.item.rankup;

import com.google.common.base.Preconditions;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.dto.RankUpProgression;
import fr.djaytan.minecraft.rank_up_challenges.view.EconomyFormatter;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.RankUpChallengesController;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.Rank;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.RankUpPrerequisites;
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

    return itemBuilder.asGuiItem(onClick(rank, rankUpProgression));
  }

  private @NotNull GuiAction<InventoryClickEvent> onClick(
      @NotNull Rank rank, @NotNull RankUpProgression rankUpProgression) {
    return event -> {
      Player player = (Player) event.getWhoClicked();
      rankUpChallengesController.onRankUpRequested(player, rank, rankUpProgression);
    };
  }

  private @NotNull Component getName(boolean isRankOwned) {
    String nameKey = "rank_up_challenges.rankup.rankup.item.name.unlockable";

    if (isRankOwned) {
      nameKey = "rank_up_challenges.rankup.rankup.item.name.already_unlocked";
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
                    "rank_up_challenges.rankup.rankup.item.lore.prerequisite.required.minecraft_xp"),
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
                    "rank_up_challenges.rankup.rankup.item.lore.prerequisite.required.jobs_levels"),
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
                    "rank_up_challenges.rankup.rankup.item.lore.prerequisite.required.money"),
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
    String canRankUpKey = "rank_up_challenges.rankup.rankup.item.lore.prerequisites_not_respected";

    if (canRankUp) {
      canRankUpKey = "rank_up_challenges.rankup.rankup.item.lore.unlock_rank";
    }

    return List.of(
        miniMessage
            .deserialize(resourceBundle.getString(canRankUpKey))
            .decoration(TextDecoration.ITALIC, false));
  }

  private @NotNull Component getCurrentProgression(
      boolean isPrerequisiteValidated, @NotNull String value) {
    String templateMessageKey =
        "rank_up_challenges.rankup.rankup.item.lore.prerequisite.progression.insufficient";

    if (isPrerequisiteValidated) {
      templateMessageKey = "rank_up_challenges.rankup.rankup.item.lore.prerequisite.progression.sufficient";
    }

    return miniMessage.deserialize(
        resourceBundle.getString(templateMessageKey),
        TagResolver.resolver(Placeholder.unparsed("diag_current_progression", value)));
  }
}
