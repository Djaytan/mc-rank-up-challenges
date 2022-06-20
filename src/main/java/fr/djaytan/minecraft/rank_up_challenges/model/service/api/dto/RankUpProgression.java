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

package fr.djaytan.minecraft.rank_up_challenges.model.service.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankUpProgression {

  private final int currentXpLevel;
  private final boolean isXpLevelPrerequisiteDone;
  private final int totalJobsLevels;
  private final boolean isTotalJobsLevelsPrerequisiteDone;
  private final double currentBalance;
  private final boolean isMoneyPrerequisiteDone;
  private final boolean isChallengesPrerequisiteDone;
  private final boolean isRankOwned;

  public boolean canRankUp() {
    return isXpLevelPrerequisiteDone
        && isTotalJobsLevelsPrerequisiteDone
        && isMoneyPrerequisiteDone
        && isChallengesPrerequisiteDone;
  }
}
