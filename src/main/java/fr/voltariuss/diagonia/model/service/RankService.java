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

package fr.voltariuss.diagonia.model.service;

import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.dto.RankUpProgression;
import java.util.List;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.track.PromotionResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Rank service interface.
 *
 * @author Voltariuss
 */
// TODO: refactor - make used objects more generic that those of LuckPerms
public interface RankService {

  /**
   * Recovers the current rank of the given player.
   *
   * <p>A newbie player may not have rank.
   *
   * @param player The player.
   * @return The current rank of the specified player.
   */
  @Nullable
  Group getCurrentRank(@NotNull Player player);

  /**
   * Gets the unlockable rank if the player doesn't have the last rank yet. Otherwise, the returned
   * value will be null.
   *
   * @param player The player.
   * @return The unlockable rank if the player doesn't have the last rank yet. Otherwise, the
   *     returned value will be null.
   */
  @Nullable
  Group getUnlockableRank(@NotNull Player player);

  /**
   * Recovers the owned ranks of the given player.
   *
   * <p>A newbie player may not have rank.
   *
   * @param player The player.
   * @return The owned ranks of the specified player.
   */
  @NotNull
  List<Group> getOwnedRanks(@NotNull Player player);

  /**
   * Checks if the specified rank correspond to the current one of the given player.
   *
   * @param player The player.
   * @param rankId The rank's ID.
   * @return "true" if the specified rank correspond to the current one of the given player, "false"
   *     otherwise.
   */
  boolean isCurrentRank(@NotNull Player player, @NotNull String rankId);

  /**
   * Checks if the specified rank correspond to the unlockable one for the given player.
   *
   * @param player The player.
   * @param rankId The rank's ID.
   * @return "true" if the specified rank correspond to the unlockable one for the given player,
   *     "false" otherwise.
   */
  boolean isUnlockableRank(@NotNull Player player, @NotNull String rankId);

  /**
   * Checks if the specified rank is owned by the given player.
   *
   * @param player The player.
   * @param rankId The rank's ID.
   * @return "true" if the specified rank is owned by the given player, "false" otherwise.
   */
  boolean isRankOwned(@NotNull Player player, @NotNull String rankId);

  /**
   * Give the upper rank to the specified player.
   *
   * @param player The concerned player by the rank up.
   * @return The result response about the promotion.
   */
  @NotNull
  // TODO: custom PromotionResult + include directly the new rank (with check about existence)
  PromotionResult promote(@NotNull Player player);

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
}
