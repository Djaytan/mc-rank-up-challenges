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
import fr.voltariuss.diagonia.controller.RankUpController;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.config.rank.RankUpPrerequisites;
import fr.voltariuss.diagonia.model.dto.RankUpProgression;
import fr.voltariuss.diagonia.view.EconomyFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankUpItem {

  private static final Material RANK_UP_MATERIAL = Material.WRITABLE_BOOK;

  private final EconomyFormatter economyFormatter;
  private final MiniMessage miniMessage;
  private final RankUpController rankUpController;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankUpItem(
      @NotNull EconomyFormatter economyFormatter,
      @NotNull MiniMessage miniMessage,
      @NotNull RankUpController rankUpController,
      @NotNull ResourceBundle resourceBundle) {
    this.economyFormatter = economyFormatter;
    this.miniMessage = miniMessage;
    this.rankUpController = rankUpController;
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

    return rankUpProgression.isRankOwned()
        ? itemBuilder.asGuiItem()
        : itemBuilder.asGuiItem(onClick(rankUpProgression));
  }

  public @NotNull Component getName(boolean isRankOwned) {
    return isRankOwned
        ? miniMessage
            .deserialize(resourceBundle.getString("diagonia.rankup.rankup.name.already_unlocked"))
            .decoration(TextDecoration.ITALIC, false)
        : miniMessage
            .deserialize(resourceBundle.getString("diagonia.rankup.rankup.name"))
            .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull List<Component> getLore(
      @NotNull RankUpProgression rankUpProgression,
      @NotNull RankUpPrerequisites rankUpPrerequisites) {

    if (rankUpProgression.isRankOwned()) {
      return Collections.emptyList();
    }

    return Arrays.asList(
        miniMessage
            .deserialize(
                resourceBundle.getString("diagonia.rankup.rankup.cost.minecraft_xp"),
                TemplateResolver.templates(
                    getPrerequisiteStateColorTemplate(
                        rankUpProgression.isXpLevelPrerequisiteDone()),
                    Template.template(
                        "diag_current_level",
                        String.valueOf(rankUpProgression.getCurrentXpLevel())),
                    Template.template(
                        "diag_required_level",
                        String.valueOf(rankUpPrerequisites.getTotalMcExpLevels()))))
            .decoration(TextDecoration.ITALIC, false),
        miniMessage
            .deserialize(
                resourceBundle.getString("diagonia.rankup.rankup.cost.jobs_levels"),
                TemplateResolver.templates(
                    getPrerequisiteStateColorTemplate(
                        rankUpProgression.isTotalJobsLevelsPrerequisiteDone()),
                    Template.template(
                        "diag_current_level",
                        String.valueOf(rankUpProgression.getTotalJobsLevels())),
                    Template.template(
                        "diag_required_level",
                        String.valueOf(rankUpPrerequisites.getTotalJobsLevel()))))
            .decoration(TextDecoration.ITALIC, false),
        miniMessage
            .deserialize(
                resourceBundle.getString("diagonia.rankup.rankup.cost.money"),
                TemplateResolver.templates(
                    getPrerequisiteStateColorTemplate(rankUpProgression.isMoneyPrerequisiteDone()),
                    Template.template(
                        "diag_current_balance",
                        economyFormatter.format(rankUpProgression.getCurrentBalance())),
                    Template.template(
                        "diag_rankup_price",
                        economyFormatter.format(rankUpPrerequisites.getMoneyPrice()))))
            .decoration(TextDecoration.ITALIC, false),
        Component.empty(),
        rankUpProgression.canRankUp()
            ? miniMessage
                .deserialize(resourceBundle.getString("diagonia.rankup.rankup.unlock_rank"))
                .decoration(TextDecoration.ITALIC, false)
            : miniMessage
                .deserialize(
                    resourceBundle.getString("diagonia.rankup.rankup.prerequisites_required"))
                .decoration(TextDecoration.ITALIC, false));
  }

  public @NotNull GuiAction<InventoryClickEvent> onClick(
      @NotNull RankUpProgression rankUpProgression) {
    return event -> {
      Player player = (Player) event.getWhoClicked();
      rankUpController.onRankUpRequested(player, rankUpProgression);
    };
  }

  private @NotNull Template getPrerequisiteStateColorTemplate(boolean isPrerequisiteValidated) {
    Component prerequisiteStateColor;

    if (isPrerequisiteValidated) {
      prerequisiteStateColor =
          miniMessage.deserialize(
              resourceBundle.getString(
                  "diagonia.rankup.rankup.prerequisite.state.validated.color"));
    } else {
      prerequisiteStateColor =
          miniMessage.deserialize(
              resourceBundle.getString("diagonia.rankup.rankup.prerequisite.state.required.color"));
    }

    return Template.template("diag_prerequisite_state_color", prerequisiteStateColor);
  }
}
