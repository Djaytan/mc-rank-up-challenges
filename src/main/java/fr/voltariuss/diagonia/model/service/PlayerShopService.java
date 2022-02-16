package fr.voltariuss.diagonia.model.service;

import fr.voltariuss.diagonia.Debugger;
import fr.voltariuss.diagonia.model.dao.PlayerShopDao;
import fr.voltariuss.diagonia.model.dao.PlayerShopDaoImpl;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlayerShopService {

  private final Debugger debugger;
  private final PlayerShopDao playerShopDao;

  @Inject
  public PlayerShopService(@NotNull Debugger debugger, @NotNull PlayerShopDaoImpl playerShopDao) {
    this.debugger = debugger;
    this.playerShopDao = playerShopDao;
  }

  public void persist(@NotNull PlayerShop playerShop) {
    playerShopDao.openSession();
    Transaction tx = playerShopDao.beginTransaction();
    playerShopDao.persist(playerShop);
    tx.commit();
    playerShopDao.destroySession();
    debugger.debug("PlayerShop persisted: {}", playerShop);
  }

  public void update(@NotNull PlayerShop playerShop) {
    playerShopDao.openSession();
    Transaction tx = playerShopDao.beginTransaction();
    playerShopDao.update(playerShop);
    tx.commit();
    playerShopDao.destroySession();
    debugger.debug("PlayerShop updated: {}", playerShop);
  }

  public @NotNull Optional<PlayerShop> findById(long id) {
    playerShopDao.openSession();
    Optional<PlayerShop> playerShop = playerShopDao.findById(id);
    playerShopDao.destroySession();
    debugger.debug("PlayerShop found by ID: {}", playerShop);
    return playerShop;
  }

  public void delete(@NotNull PlayerShop playerShop) {
    playerShopDao.openSession();
    Transaction tx = playerShopDao.beginTransaction();
    long idPlayerShop = playerShop.getId();
    playerShopDao.delete(playerShop);
    tx.commit();
    playerShopDao.destroySession();
    debugger.debug("PlayerShop deleted: id={}", idPlayerShop);
  }

  public @NotNull List<PlayerShop> findAll() {
    playerShopDao.openSession();
    List<PlayerShop> playerShops = playerShopDao.findAll();
    playerShopDao.destroySession();
    debugger.debug("PlayerShop find all: {}", playerShops);
    return playerShops;
  }
}
