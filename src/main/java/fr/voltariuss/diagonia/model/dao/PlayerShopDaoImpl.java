package fr.voltariuss.diagonia.model.dao;

import com.google.common.base.Preconditions;
import fr.voltariuss.diagonia.model.AbstractJpaDao;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** DAO class of {@link PlayerShop} entity. */
@Singleton
public class PlayerShopDaoImpl extends AbstractJpaDao<PlayerShop, Long> implements PlayerShopDao {

  @Inject
  public PlayerShopDaoImpl(@NotNull SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void persist(@NotNull PlayerShop entity) {
    Preconditions.checkNotNull(entity);
    getCurrentSession().persist(entity);
  }

  @Override
  public void update(@NotNull PlayerShop entity) {
    Preconditions.checkNotNull(entity);
    getCurrentSession().merge(entity);
  }

  @Override
  public @NotNull Optional<PlayerShop> findById(@Nullable Long id) {
    Preconditions.checkNotNull(id);
    return Optional.ofNullable(getCurrentSession().get(PlayerShop.class, id));
  }

  @Override
  public void delete(@NotNull PlayerShop entity) {
    Preconditions.checkNotNull(entity);
    getCurrentSession().delete(entity);
  }

  @Override
  public @NotNull List<PlayerShop> findAll() {
    return getCurrentSession().createQuery("FROM PlayerShop", PlayerShop.class).list();
  }
}