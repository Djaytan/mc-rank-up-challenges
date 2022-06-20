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
        .deserialize(resourceBundle.getString("rank_up_challenges.common.fail.unknown_error"))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component rankUpSuccess(@NotNull Player whoRankUp, @NotNull Rank newRank) {
    return miniMessage
        .deserialize(
            resourceBundle.getString("rank_up_challenges.rankup.rankup.success"),
            TagResolver.resolver(
                Placeholder.unparsed("player_name", whoRankUp.getName()),
                Placeholder.component(
                    "rank", Component.text(newRank.getName()).color(newRank.getColor()))))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component prerequisitesNotRespected() {
    return miniMessage
        .deserialize(
            resourceBundle.getString("rank_up_challenges.rankup.rankup.fail.prerequisites_not_respected"))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component alreadyHasHighestRank() {
    return miniMessage
        .deserialize(
            resourceBundle.getString("rank_up_challenges.rankup.ranks.failure.already_has_highest_rank"))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component noItemInInventory() {
    return miniMessage
        .deserialize(
            resourceBundle.getString("rank_up_challenges.rankup.challenges.give.fail.no_item_in_inventory"))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component challengeAlreadyCompleted() {
    return miniMessage
        .deserialize(
            resourceBundle.getString(
                "rank_up_challenges.rankup.challenges.give.fail.challenge_already_completed"))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component rankAlreadyOwned() {
    return miniMessage
        .deserialize(
            resourceBundle.getString("rank_up_challenges.rankup.challenges.give.failure.rank_already_owned"))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component successAmountGiven(int amountGiven, Component itemsGivenNameCpnt) {
    return miniMessage
        .deserialize(
            resourceBundle.getString("rank_up_challenges.rankup.challenges.give.success.amount_given"),
            TagResolver.resolver(
                Placeholder.unparsed("ruc_amount_given", String.valueOf(amountGiven)),
                Placeholder.component("ruc_item_name", itemsGivenNameCpnt)))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component challengeCompleted(@NotNull Component challengeNameCpnt) {
    return miniMessage
        .deserialize(
            resourceBundle.getString("rank_up_challenges.rankup.challenges.give.success.now_completed"),
            TagResolver.resolver(Placeholder.component("ruc_challenge_name", challengeNameCpnt)))
        .decoration(TextDecoration.ITALIC, false);
  }
}
