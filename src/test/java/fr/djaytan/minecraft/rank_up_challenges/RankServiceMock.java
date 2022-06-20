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
