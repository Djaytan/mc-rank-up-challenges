package fr.voltariuss.diagonia.model.config;

import java.util.List;

import fr.voltariuss.diagonia.model.RankChallengeType;
import lombok.Builder;
import lombok.Data;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

@Data
@Builder
public class RankConfig {
  private final List<Rank> ranks;

  @Data
  @Builder
  public static class Rank {
    private final String id;
    private final String name;
    private final List<String> description;
    private final TextColor color;
    private final List<String> profits;
    private final boolean isRankUpActivated;
    @Nullable private final List<RankChallenge> rankUpChallenges;
    @Nullable private final RankUpPrerequisite rankUpPrerequisite;
    @Nullable private final String rankUpTarget;
  }

  @Data
  @Builder
  public static class RankChallenge {
    private final RankChallengeType challengeType;
    private final Material challengeItemMaterial;
    private final int challengeItemAmount;
  }

  @Data
  @Builder
  public static class RankUpPrerequisite {
    private final double moneyPrice;
    private final int totalMcExpLevels;
    private final int totalJobsLevel;
  }
}
