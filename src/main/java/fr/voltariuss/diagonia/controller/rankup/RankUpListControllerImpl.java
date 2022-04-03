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

package fr.voltariuss.diagonia.controller.rankup;

import fr.voltariuss.diagonia.model.service.RankService;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankUpListControllerImpl implements RankUpListController {

  private final RankService rankService;

  @Inject
  public RankUpListControllerImpl(@NotNull RankService rankService) {
    this.rankService = rankService;
  }

  @Override
  public boolean isRankOwned(@NotNull Player player, @NotNull String rankId) {
    return rankService.isRankOwned(player, rankId);
  }

  @Override
  public boolean isCurrentRank(@NotNull Player player, @NotNull String rankId) {
    return rankService.isCurrentRank(player, rankId);
  }

  @Override
  public boolean isUnlockableRank(@NotNull Player player, @NotNull String rankId) {
    return rankService.isUnlockableRank(player, rankId);
  }
}
