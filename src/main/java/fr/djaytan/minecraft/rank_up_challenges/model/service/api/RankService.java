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
import java.util.List;
import net.luckperms.api.track.PromotionResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Rank service interface.
 *
 * @author Voltariuss
 */
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
  Rank getCurrentRank(@NotNull Player player);

  /**
   * Gets the unlockable rank if the player doesn't have the last rank yet. Otherwise, the returned
   * value will be null.
   *
   * @param player The player.
   * @return The unlockable rank if the player doesn't have the last rank yet. Otherwise, the
   *     returned value will be null.
   */
  @Nullable
  Rank getUnlockableRank(@NotNull Player player);

  /**
   * Recovers the owned ranks of the given player.
   *
   * <p>A newbie player may not have rank.
   *
   * @param player The player.
   * @return The owned ranks of the specified player.
   */
  @NotNull
  List<Rank> getOwnedRanks(@NotNull Player player);

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
  PromotionResult promote(@NotNull Player player);
}
