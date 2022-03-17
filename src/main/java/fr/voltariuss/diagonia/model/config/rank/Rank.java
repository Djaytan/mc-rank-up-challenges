package fr.voltariuss.diagonia.model.config.rank;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Nullable;

@Data
@Builder
// TODO: add a rank config verification at plugin enabling
public class Rank {

  private final String id;
  private final String name;
  private final List<String> description;
  private final TextColor color;
  private final List<String> profits;
  private final boolean isRankUpActivated;
  @Nullable private final List<RankChallenge> rankUpChallenges;
  @Nullable private final RankUpPrerequisites rankUpPrerequisites;
  @Nullable private final String rankUpTarget;
}
