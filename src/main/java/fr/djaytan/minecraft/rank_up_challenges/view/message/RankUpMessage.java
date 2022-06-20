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

package fr.djaytan.minecraft.rank_up_challenges.view.message;

import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.Rank;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
            TagResolver.resolver(
                Placeholder.unparsed("player_name", whoRankUp.getName()),
                Placeholder.component(
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

  public @NotNull Component successAmountGiven(int amountGiven, Component itemsGivenNameCpnt) {
    return miniMessage
        .deserialize(
            resourceBundle.getString("diagonia.rankup.challenges.give.success.amount_given"),
            TagResolver.resolver(
                Placeholder.unparsed("diag_amount_given", String.valueOf(amountGiven)),
                Placeholder.component("diag_item_name", itemsGivenNameCpnt)))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component challengeCompleted(@NotNull Component challengeNameCpnt) {
    return miniMessage
        .deserialize(
            resourceBundle.getString("diagonia.rankup.challenges.give.success.now_completed"),
            TagResolver.resolver(Placeholder.component("diag_challenge_name", challengeNameCpnt)))
        .decoration(TextDecoration.ITALIC, false);
  }
}
