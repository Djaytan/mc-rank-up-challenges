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

package fr.djaytan.minecraft.rank_up_challenges;

import fr.djaytan.minecraft.rank_up_challenges.model.service.api.RankService;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.Rank;
import java.util.Collections;
import java.util.List;
import net.luckperms.api.track.PromotionResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RankServiceMock implements RankService {

  @Override
  public @Nullable Rank getCurrentRank(@NotNull Player player) {
    return null;
  }

  @Override
  public @Nullable Rank getUnlockableRank(@NotNull Player player) {
    return null;
  }

  @Override
  public @NotNull List<Rank> getOwnedRanks(@NotNull Player player) {
    return Collections.emptyList();
  }

  @Override
  public boolean isCurrentRank(@NotNull Player player, @NotNull String rankId) {
    return false;
  }

  @Override
  public boolean isUnlockableRank(@NotNull Player player, @NotNull String rankId) {
    return false;
  }

  @Override
  public boolean isRankOwned(@NotNull Player player, @NotNull String rankId) {
    return false;
  }

  @Override
  public @NotNull PromotionResult promote(@NotNull Player player) {
    return null;
  }
}
