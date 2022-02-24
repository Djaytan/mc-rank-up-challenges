package fr.voltariuss.diagonia.model.dao;

import fr.voltariuss.diagonia.model.AbstractJpaDao;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankChallengeProgressionDao extends AbstractJpaDao<RankChallengeProgression, Long> {

  @Inject
  public RankChallengeProgressionDao(@NotNull SessionFactory sessionFactory) {
    super(sessionFactory);
  }
}
