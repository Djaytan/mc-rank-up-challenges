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

package fr.voltariuss.diagonia.model.service;

import com.google.common.base.Preconditions;
import fr.voltariuss.diagonia.RemakeBukkitLogger;
import fr.voltariuss.diagonia.model.GiveActionType;
import fr.voltariuss.diagonia.model.config.data.rank.Rank;
import fr.voltariuss.diagonia.model.config.data.rank.RankChallenge;
import fr.voltariuss.diagonia.model.dao.JpaDaoException;
import fr.voltariuss.diagonia.model.dao.RankChallengeProgressionDao;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankUpServiceImpl implements RankUpService {

  private static final String TRANSACTION_ROLLBACK_FAIL_MESSAGE = "Failed to rollback transaction";

  private final RemakeBukkitLogger logger;
  private final RankChallengeProgressionDao rankChallengeProgressionDao;

  @Inject
  public RankUpServiceImpl(
      @NotNull RemakeBukkitLogger logger,
      @NotNull RankChallengeProgressionDao rankChallengeProgressionDao) {
    this.logger = logger;
    this.rankChallengeProgressionDao = rankChallengeProgressionDao;
  }

  @Override
  public boolean areChallengesCompleted(@NotNull Player player, @NotNull Rank rank) {
    Preconditions.checkNotNull(rank.getRankUpChallenges());

    boolean areChallengesCompleted = true;

    List<RankChallengeProgression> playerProgression =
        findChallengesProgressions(player.getUniqueId(), rank.getId()).stream()
            .filter(
                rcp ->
                    rank.getRankUpChallenges().stream()
                        .map(RankChallenge::getMaterial)
                        .toList()
                        .contains(rcp.getChallengeMaterial()))
            .toList();

    for (RankChallenge rankChallenge : rank.getRankUpChallenges()) {
      int amount =
          playerProgression.stream()
              .filter(pp -> rankChallenge.getMaterial().equals(pp.getChallengeMaterial()))
              .mapToInt(RankChallengeProgression::getChallengeAmountGiven)
              .sum();
      if (amount < rankChallenge.getAmount()) {
        areChallengesCompleted = false;
        break;
      }
    }

    return areChallengesCompleted;
  }

  @Override
  public @NotNull Optional<RankChallengeProgression> findChallengeProgression(
      @NotNull UUID playerUuid, @NotNull String rankId, @NotNull Material material) {
    rankChallengeProgressionDao.openSession();
    try {
      Optional<RankChallengeProgression> rankChallengeProgression =
          rankChallengeProgressionDao.find(playerUuid, rankId, material);
      logger.debug("RankChallengeProgression found: {}", rankChallengeProgression);
      return rankChallengeProgression;
    } finally {
      rankChallengeProgressionDao.destroySession();
    }
  }

  @Override
  public @NotNull Optional<RankChallengeProgression> findChallengeProgressionById(long id) {
    rankChallengeProgressionDao.openSession();
    try {
      Optional<RankChallengeProgression> rankChallengeProgression =
          rankChallengeProgressionDao.findById(id);
      logger.debug("RankChallengeProgression found for ID {}: {}", id, rankChallengeProgression);
      return rankChallengeProgression;
    } finally {
      rankChallengeProgressionDao.destroySession();
    }
  }

  @Override
  public @NotNull List<RankChallengeProgression> findChallengesProgressions(
      @NotNull UUID playerUuid, @NotNull String rankId) {
    rankChallengeProgressionDao.openSession();
    try {
      List<RankChallengeProgression> rankChallengeProgressions =
          rankChallengeProgressionDao.find(playerUuid, rankId);
      logger.debug("RankChallengeProgressions found: {}", rankChallengeProgressions);
      return rankChallengeProgressions;
    } finally {
      rankChallengeProgressionDao.destroySession();
    }
  }

  @Override
  public int giveItemChallenge(
      @NotNull UUID playerUuid,
      @NotNull Rank rank,
      @NotNull RankChallenge rankChallenge,
      @NotNull GiveActionType giveActionType,
      int nbItemsInInventory) {
    int effectiveGivenAmount;
    rankChallengeProgressionDao.openSession();
    Transaction tx = rankChallengeProgressionDao.beginTransaction();
    try {
      RankChallengeProgression rankChallengeProgression =
          rankChallengeProgressionDao
              .find(playerUuid, rank.getId(), rankChallenge.getMaterial())
              .orElse(null);

      if (rankChallengeProgression == null) {
        logger.debug("RankChallengeProgression not found.");
        rankChallengeProgression =
            new RankChallengeProgression(playerUuid, rank.getId(), rankChallenge.getMaterial());
        rankChallengeProgressionDao.persist(rankChallengeProgression);
        logger.debug("RankChallengeProgression persisted.");
      }

      int remainingItemsToGive =
          rankChallenge.getAmount() - rankChallengeProgression.getChallengeAmountGiven();

      int maxItemsToGiveAsked = giveActionType.getNbItemsToGive();
      int maxItemsToGive =
          maxItemsToGiveAsked == -1
              ? nbItemsInInventory
              : Math.min(nbItemsInInventory, maxItemsToGiveAsked);

      effectiveGivenAmount = Math.min(maxItemsToGive, remainingItemsToGive);
      int newAmountGiven =
          rankChallengeProgression.getChallengeAmountGiven() + effectiveGivenAmount;
      rankChallengeProgression.setChallengeAmountGiven(newAmountGiven);
      rankChallengeProgressionDao.update(rankChallengeProgression);
      tx.commit();
      logger.debug("RankChallengeProgression updated: {}", rankChallengeProgression);
    } catch (HibernateException e) {
      try {
        tx.rollback();
      } catch (RuntimeException re) {
        throw new JpaDaoException(TRANSACTION_ROLLBACK_FAIL_MESSAGE, re);
      }
      throw e;
    } finally {
      rankChallengeProgressionDao.destroySession();
    }
    return effectiveGivenAmount;
  }

  @Override
  public boolean isChallengeCompleted(
      @NotNull UUID uuid, @NotNull String rankId, @NotNull RankChallenge rankChallenge) {
    rankChallengeProgressionDao.openSession();

    Optional<RankChallengeProgression> rankChallengeProgression;

    try {
      rankChallengeProgression =
          rankChallengeProgressionDao.find(uuid, rankId, rankChallenge.getMaterial());
    } finally {
      rankChallengeProgressionDao.destroySession();
    }

    if (rankChallengeProgression.isEmpty()) {
      return false;
    }

    return rankChallengeProgression.get().getChallengeAmountGiven() >= rankChallenge.getAmount();
  }

  @Override
  public void persistChallengeProgression(
      @NotNull RankChallengeProgression rankChallengeProgression) {
    rankChallengeProgressionDao.openSession();
    Transaction tx = rankChallengeProgressionDao.beginTransaction();
    try {
      rankChallengeProgressionDao.persist(rankChallengeProgression);
      tx.commit();
      logger.debug("RankChallengeProgression persisted: {}", rankChallengeProgression);
    } catch (HibernateException e) {
      try {
        tx.rollback();
      } catch (RuntimeException re) {
        throw new JpaDaoException(TRANSACTION_ROLLBACK_FAIL_MESSAGE, re);
      }
      throw e;
    } finally {
      rankChallengeProgressionDao.destroySession();
    }
  }
}
