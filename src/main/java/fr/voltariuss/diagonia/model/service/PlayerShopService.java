package fr.voltariuss.diagonia.model.service;

import fr.voltariuss.diagonia.model.JpaDaoException;
import fr.voltariuss.diagonia.model.dao.PlayerShopDao;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class PlayerShopService {

  private static final String TRANSACTION_ROLLBACK_FAIL_MESSAGE = "Failed to rollback transaction";

  private final Logger logger;
  private final PlayerShopDao playerShopDao;

  @Inject
  public PlayerShopService(@NotNull Logger logger, @NotNull PlayerShopDao playerShopDao) {
    this.logger = logger;
    this.playerShopDao = playerShopDao;
  }

  public void persist(@NotNull PlayerShop playerShop) {
    playerShopDao.openSession();
    Transaction tx = playerShopDao.beginTransaction();
    try {
      playerShopDao.persist(playerShop);
      tx.commit();
      logger.debug("PlayerShop persisted: {}", playerShop);
    } catch (HibernateException e) {
      try {
        tx.rollback();
      } catch (RuntimeException re) {
        throw new JpaDaoException(TRANSACTION_ROLLBACK_FAIL_MESSAGE, re);
      }
      throw e;
    } finally {
      playerShopDao.destroySession();
    }
  }

  public void update(@NotNull PlayerShop playerShop) {
    playerShopDao.openSession();
    Transaction tx = playerShopDao.beginTransaction();
    try {
      playerShopDao.update(playerShop);
      tx.commit();
      logger.debug("PlayerShop updated: {}", playerShop);
    } catch (HibernateException e) {
      try {
        tx.rollback();
      } catch (RuntimeException re) {
        throw new JpaDaoException(TRANSACTION_ROLLBACK_FAIL_MESSAGE, re);
      }
      throw e;
    } finally {
      playerShopDao.destroySession();
    }
  }

  public @NotNull Optional<PlayerShop> findById(long id) {
    playerShopDao.openSession();
    try {
      Optional<PlayerShop> playerShop = playerShopDao.findById(id);
      logger.debug("PlayerShop found for ID {}: {}", id, playerShop);
      return playerShop;
    } finally {
      playerShopDao.destroySession();
    }
  }

  public @NotNull Optional<PlayerShop> findByUuid(@NotNull UUID uuid) {
    playerShopDao.openSession();
    try {
      Optional<PlayerShop> playerShop = playerShopDao.findByUuid(uuid);
      logger.debug("PlayerShop found for UUID {}: {}", uuid, playerShop);
      return playerShop;
    } finally {
      playerShopDao.destroySession();
    }
  }

  public void delete(@NotNull PlayerShop playerShop) {
    playerShopDao.openSession();
    Transaction tx = playerShopDao.beginTransaction();
    try {
      long idPlayerShop = playerShop.getId();
      playerShopDao.delete(playerShop);
      tx.commit();
      logger.debug("PlayerShop deleted: id={}", idPlayerShop);
    } catch (HibernateException e) {
      try {
        tx.rollback();
      } catch (RuntimeException re) {
        throw new JpaDaoException(TRANSACTION_ROLLBACK_FAIL_MESSAGE, re);
      }
      throw e;
    } finally {
      playerShopDao.destroySession();
    }
  }

  public @NotNull List<PlayerShop> findAll() {
    playerShopDao.openSession();
    try {
      List<PlayerShop> playerShops = playerShopDao.findAll();
      logger.debug("PlayerShop find all: {}", playerShops);
      return playerShops;
    } finally {
      playerShopDao.destroySession();
    }
  }
}
