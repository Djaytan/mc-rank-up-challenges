package fr.voltariuss.diagonia.model.config.rank;

import lombok.Builder;
import lombok.Data;
import org.bukkit.Material;

@Data
@Builder
public class RankChallenge {

  private final Material challengeItemMaterial;
  private final int challengeItemAmount;
}
