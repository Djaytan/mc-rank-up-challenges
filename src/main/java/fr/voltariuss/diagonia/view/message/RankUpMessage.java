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

package fr.voltariuss.diagonia.view.message;

import fr.voltariuss.diagonia.model.config.rank.Rank;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankUpMessage {

  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankUpMessage(@NotNull MiniMessage miniMessage, @NotNull ResourceBundle resourceBundle) {
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull Component rankUpFailure() {
    return miniMessage
        .deserialize(resourceBundle.getString("diagonia.common.fail.unknown_error"))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component rankUpSuccess(@NotNull Player whoRankUp, @NotNull Rank newRank) {
    return miniMessage
        .deserialize(
            resourceBundle.getString("diagonia.rankup.rankup.success"),
            TemplateResolver.templates(
                Template.template("player_name", whoRankUp.getName()),
                Template.template(
                    "rank", Component.text(newRank.getName()).color(newRank.getColor()))))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component prerequisitesNotRespected() {
    return miniMessage
        .deserialize(
            resourceBundle.getString("diagonia.rankup.rankup.fail.prerequisites_not_respected"))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component alreadyHasHighestRank() {
    return miniMessage
        .deserialize(
            resourceBundle.getString("diagonia.rankup.ranks.failure.already_has_highest_rank"))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component noItemInInventory() {
    return miniMessage
        .deserialize(
            resourceBundle.getString("diagonia.rankup.challenges.give.fail.no_item_in_inventory"))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component challengeAlreadyCompleted() {
    return miniMessage
        .deserialize(
            resourceBundle.getString(
                "diagonia.rankup.challenges.give.fail.challenge_already_completed"))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component rankAlreadyOwned() {
    return miniMessage
        .deserialize(
            resourceBundle.getString("diagonia.rankup.challenges.give.failure.rank_already_owned"))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component successAmountGiven(int amountGiven, String itemsGivenName) {
    return miniMessage
        .deserialize(
            resourceBundle.getString("diagonia.rankup.challenges.give.success.amount_given"),
            TemplateResolver.templates(
                Template.template("diag_amount_given", String.valueOf(amountGiven)),
                Template.template("diag_item_name", itemsGivenName)))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component challengeCompleted(@NotNull String challengeName) {
    return miniMessage
        .deserialize(
            resourceBundle.getString("diagonia.rankup.challenges.give.success.now_completed"),
            TemplateResolver.templates(Template.template("diag_challenge_name", challengeName)))
        .decoration(TextDecoration.ITALIC, false);
  }
}
