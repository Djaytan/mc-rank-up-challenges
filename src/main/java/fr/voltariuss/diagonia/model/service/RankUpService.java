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

import fr.voltariuss.diagonia.model.GiveActionType;
import fr.voltariuss.diagonia.model.config.data.rank.Rank;
import fr.voltariuss.diagonia.model.config.data.rank.RankChallenge;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
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

  int giveItemChallenge(
      @NotNull UUID playerUuid,
      @NotNull Rank rank,
      @NotNull RankChallenge rankChallenge,
      @NotNull GiveActionType giveActionType,
      int nbItemsInInventory);

  boolean isChallengeCompleted(
      @NotNull UUID uuid, @NotNull String rankId, @NotNull RankChallenge rankChallenge);

  void persistChallengeProgression(@NotNull RankChallengeProgression rankChallengeProgression);
}
