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

package fr.djaytan.minecraft.rank_up_challenges.model.service.api;

import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.Rank;
import fr.djaytan.minecraft.rank_up_challenges.model.entity.RankChallengeProgression;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.dto.GiveActionType;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.dto.RankUpProgression;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface RankUpService {

  boolean areChallengesCompleted(@NotNull Player player, @NotNull Rank rank);

  @NotNull
  Optional<RankChallengeProgression> findChallengeProgression(
      @NotNull UUID playerUuid, @NotNull String rankId, @NotNull Material material);

  @NotNull
  Optional<RankChallengeProgression> findChallengeProgressionById(long id);

  @NotNull
  List<RankChallengeProgression> findChallengesProgressions(
      @NotNull UUID playerUuid, @NotNull String rankId);

  /**
   * Provides the rank up progression of a given player for a specified rank.
   *
   * @param player The player.
   * @param targetedRank The targeted rank for rank up.
   * @param totalJobsLevels Total jobs levels for the specified player.
   * @param currentBalance The current economy balance of the player.
   * @return The rank up progression of the given player for the specified rank.
   */
  @NotNull
  RankUpProgression getRankUpProgression(
      @NotNull Player player,
      @NotNull Rank targetedRank,
      int totalJobsLevels,
      double currentBalance);

  int giveItemChallenge(
      @NotNull UUID playerUuid,
      @NotNull Rank rank,
      @NotNull Material challengeMaterial,
      @NotNull GiveActionType giveActionType,
      int nbItemsInInventory);

  boolean isChallengeCompleted(
      @NotNull UUID uuid, @NotNull String rankId, @NotNull Material challengeMaterial);

  void persistChallengeProgression(@NotNull RankChallengeProgression rankChallengeProgression);

  boolean hasRankChallenges(@NotNull UUID playerUuid, @NotNull Rank rank);

  void rollRankChallenges(@NotNull UUID playerUuid, @NotNull Rank rank);
}
