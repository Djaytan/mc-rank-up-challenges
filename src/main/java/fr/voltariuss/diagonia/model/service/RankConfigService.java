package fr.voltariuss.diagonia.model.service;

import com.google.common.base.Preconditions;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.config.rank.RankConfig;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankConfigService {

  private final RankConfig rankConfig;

  @Inject
  public RankConfigService(@NotNull RankConfig rankConfig) {
    this.rankConfig = rankConfig;
  }

  public @NotNull Optional<Rank> findById(@NotNull String id) {
    Preconditions.checkNotNull(id);

    return rankConfig.getRanks().stream().filter(rank -> rank.getId().equals(id)).findFirst();
  }
}
