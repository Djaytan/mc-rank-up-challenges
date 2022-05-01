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

import fr.voltariuss.diagonia.model.GiveActionType;
import fr.voltariuss.diagonia.model.config.Rank;
import fr.voltariuss.diagonia.model.config.RankChallenge;
import fr.voltariuss.diagonia.model.dto.RankUpProgression;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface RankUpChallengesController {

  void giveItemChallenge(
      @NotNull Player targetPlayer,
      @NotNull Rank rank,
      @NotNull RankChallenge rankChallenge,
      @NotNull GiveActionType giveActionType,
      int nbItemsInInventory);

  @NotNull
  Optional<RankChallengeProgression> findChallenge(
      @NotNull UUID playerUuid, @NotNull String rankId, @NotNull Material material);

  void onRankUpRequested(
    @NotNull Player player, @NotNull RankUpProgression rankUpProgression);
}
