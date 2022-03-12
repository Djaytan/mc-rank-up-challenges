package fr.voltariuss.diagonia.model.config.rank;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankUpPrerequisites {

  private final double moneyPrice;
  private final int totalMcExpLevels;
  private final int totalJobsLevel;
}
