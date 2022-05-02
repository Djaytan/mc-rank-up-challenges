/*
 * Copyright (c) 2022 - Loïc DUBOIS-TERMOZ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.voltariuss.diagonia.model.service.implementation;

import fr.voltariuss.diagonia.RemakeBukkitLogger;
import fr.voltariuss.diagonia.model.dao.JpaDaoException;
import fr.voltariuss.diagonia.model.dao.PlayerShopDao;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.model.service.api.PlayerShopService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlayerShopServiceImpl implements PlayerShopService {

  private static final String TRANSACTION_ROLLBACK_FAIL_MESSAGE = "Failed to rollback transaction";

  private final RemakeBukkitLogger logger;
  private final PlayerShopDao playerShopDao;

  @Inject
  public PlayerShopServiceImpl(
      @NotNull RemakeBukkitLogger logger, @NotNull PlayerShopDao playerShopDao) {
    this.logger = logger;
    this.playerShopDao = playerShopDao;
  }

  @Override
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

  @Override
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

  @Override
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

  @Override
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

  @Override
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