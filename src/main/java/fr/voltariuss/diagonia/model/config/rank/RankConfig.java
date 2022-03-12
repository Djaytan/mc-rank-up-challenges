package fr.voltariuss.diagonia.model.config.rank;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankConfig {

  private final List<Rank> ranks;
}
