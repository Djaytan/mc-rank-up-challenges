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

package fr.voltariuss.diagonia.model.config.data.challenge;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@Getter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ChallengeConfig {

  private List<ChallengeTier> challengesTiers;

  public int countNbChallenges() {
    return challengesTiers.stream()
        .mapToInt(challengeTier -> challengeTier.getChallenges().size())
        .sum();
  }

  public @NotNull ChallengeTier getChallengeTier(int tier) {
    Preconditions.checkArgument(tier >= 1 && tier <= challengesTiers.size());
    return challengesTiers.get(tier - 1);
  }

  public @NotNull List<ComputedChallenge> getComputedChallenges(int tier) {
    Preconditions.checkArgument(tier >= 1 && tier <= challengesTiers.size());

    List<ComputedChallenge> computedChallenges = new ArrayList<>();
    float[] tierMultipliers = new float[tier];

    // Determine gradually multipliers for each tier
    for (int i = 0; i < tier; i++) {
      ChallengeTier challengeTier = challengesTiers.get(i);
      tierMultipliers[i] = 1;

      for (int j = i - 1; j >= 0; j--) {
        tierMultipliers[j] = tierMultipliers[j] * challengeTier.getMultiplier();
      }
    }

    for (int i = 0; i < tier; i++) {
      ChallengeTier challengeTier = challengesTiers.get(i);
      float tierMultiplier = tierMultipliers[i];

      for (Challenge challenge : challengeTier.getChallenges()) {
        int computedAmount = (int) Math.round(Math.ceil(challenge.getAmount() * tierMultiplier));
        ComputedChallenge computedChallenge = new ComputedChallenge(challenge, computedAmount);
        computedChallenges.add(computedChallenge);
      }
    }

    return computedChallenges;
  }
}
