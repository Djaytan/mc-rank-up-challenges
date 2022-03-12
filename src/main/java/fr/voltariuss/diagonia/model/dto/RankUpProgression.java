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

  public boolean canRankUp() {
    return isXpLevelPrerequisiteDone
        && isTotalJobsLevelsPrerequisiteDone
        && isMoneyPrerequisiteDone
        && isChallengesPrerequisiteDone;
  }
}
