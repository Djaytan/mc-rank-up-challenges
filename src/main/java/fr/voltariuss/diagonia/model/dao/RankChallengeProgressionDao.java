package fr.voltariuss.diagonia.model.dao;

import com.google.common.base.Preconditions;
import fr.voltariuss.diagonia.model.AbstractJpaDao;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.Material;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankChallengeProgressionDao extends AbstractJpaDao<RankChallengeProgression, Long> {

  @Inject
  public RankChallengeProgressionDao(@NotNull SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Optional<RankChallengeProgression> find(
      @NotNull UUID playerUuid, @NotNull String rankId, @NotNull Material material) {
    Preconditions.checkNotNull(playerUuid);
    Preconditions.checkNotNull(rankId);
    Preconditions.checkNotNull(material);

    return getCurrentSession()
        .createQuery(
            "FROM RankChallengeProgression rcp WHERE rcp.playerUuid = :playerUuid AND rcp.rankId ="
                + " :rankId AND rcp.challengeMaterial = :challengeMaterial",
            RankChallengeProgression.class)
        .setParameter("playerUuid", playerUuid)
        .setParameter("rankId", rankId)
        .setParameter("challengeMaterial", material)
        .uniqueResultOptional();
  }
}
