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

package fr.djaytan.minecraft.rank_up_challenges.model.config.data.challenge;

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
