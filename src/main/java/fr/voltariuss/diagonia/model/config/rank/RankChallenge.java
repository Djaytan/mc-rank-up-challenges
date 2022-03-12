package fr.voltariuss.diagonia.model.config.rank;

import fr.voltariuss.diagonia.model.RankChallengeType;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Material;

@Data
@Builder
public class RankChallenge {

  private final RankChallengeType challengeType;
  private final Material challengeItemMaterial;
  private final int challengeItemAmount;
}
