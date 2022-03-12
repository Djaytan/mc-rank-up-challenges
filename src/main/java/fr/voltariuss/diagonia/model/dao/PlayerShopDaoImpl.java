package fr.voltariuss.diagonia.model.dao;

import com.google.common.base.Preconditions;
import fr.voltariuss.diagonia.model.AbstractJpaDao;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;

/** DAO class of {@link PlayerShop} entity. */
@Singleton
public class PlayerShopDaoImpl extends AbstractJpaDao<PlayerShop, Long> implements PlayerShopDao {

  @Inject
  public PlayerShopDaoImpl(@NotNull SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public @NotNull Optional<PlayerShop> findByUuid(@NotNull UUID uuid) {
    Preconditions.checkNotNull(uuid);
    return getCurrentSession()
        .createQuery("FROM PlayerShop WHERE ownerUuid = :uuid", PlayerShop.class)
        .setParameter("uuid", uuid)
        .uniqueResultOptional();
  }
}
