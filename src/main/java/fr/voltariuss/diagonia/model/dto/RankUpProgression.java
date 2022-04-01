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

package fr.voltariuss.diagonia.model.dto;

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
