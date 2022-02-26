package fr.voltariuss.diagonia.model.service;

import fr.voltariuss.diagonia.Debugger;
import fr.voltariuss.diagonia.model.JpaDaoException;
import fr.voltariuss.diagonia.model.config.RankConfig;
import fr.voltariuss.diagonia.model.dao.RankChallengeProgressionDao;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.Material;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankChallengeProgressionService {

  private static final String TRANSACTION_ROLLBACK_FAIL_MESSAGE = "Failed to rollback transaction";

  private final Debugger debugger;
  private final RankChallengeProgressionDao rankChallengeProgressionDao;
  private final RankConfig rankConfig;

  @Inject
  public RankChallengeProgressionService(
      @NotNull Debugger debugger,
      @NotNull RankChallengeProgressionDao rankChallengeProgressionDao,
      @NotNull RankConfig rankConfig) {
    this.debugger = debugger;
    this.rankChallengeProgressionDao = rankChallengeProgressionDao;
    this.rankConfig = rankConfig;
  }

  public void persist(@NotNull RankChallengeProgression rankChallengeProgression) {
    rankChallengeProgressionDao.openSession();
    Transaction tx = rankChallengeProgressionDao.beginTransaction();
    try {
      rankChallengeProgressionDao.persist(rankChallengeProgression);
      tx.commit();
      debugger.debug("RankChallengeProgression persisted: {}", rankChallengeProgression);
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

  public void update(@NotNull RankChallengeProgression rankChallengeProgression) {
    rankChallengeProgressionDao.openSession();
    Transaction tx = rankChallengeProgressionDao.beginTransaction();
    try {
      rankChallengeProgressionDao.update(rankChallengeProgression);
      tx.commit();
      debugger.debug("RankChallengeProgression updated: {}", rankChallengeProgression);
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

  public @NotNull Optional<RankChallengeProgression> findById(long id) {
    rankChallengeProgressionDao.openSession();
    try {
      Optional<RankChallengeProgression> rankChallengeProgression =
          rankChallengeProgressionDao.findById(id);
      debugger.debug("RankChallengeProgression found for ID {}: {}", id, rankChallengeProgression);
      return rankChallengeProgression;
    } finally {
      rankChallengeProgressionDao.destroySession();
    }
  }

  public void delete(@NotNull RankChallengeProgression rankChallengeProgression) {
    rankChallengeProgressionDao.openSession();
    Transaction tx = rankChallengeProgressionDao.beginTransaction();
    try {
      long idPlayerShop = rankChallengeProgression.getId();
      rankChallengeProgressionDao.delete(rankChallengeProgression);
      tx.commit();
      debugger.debug("RankChallengeProgression deleted: id={}", idPlayerShop);
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

  public @NotNull List<RankChallengeProgression> findAll() {
    rankChallengeProgressionDao.openSession();
    try {
      List<RankChallengeProgression> rankChallengeProgressions =
          rankChallengeProgressionDao.findAll();
      debugger.debug("RankChallengeProgression find all: {}", rankChallengeProgressions);
      return rankChallengeProgressions;
    } finally {
      rankChallengeProgressionDao.destroySession();
    }
  }

  public @NotNull Optional<RankChallengeProgression> find(
      @NotNull UUID playerUuid, @NotNull String rankId, @NotNull Material material) {
    rankChallengeProgressionDao.openSession();
    try {
      Optional<RankChallengeProgression> rankChallengeProgression =
          rankChallengeProgressionDao.find(playerUuid, rankId, material);
      debugger.debug("RankChallengeProgression found: {}", rankChallengeProgression);
      return rankChallengeProgression;
    } finally {
      rankChallengeProgressionDao.destroySession();
    }
  }

  public @NotNull List<RankChallengeProgression> find(
      @NotNull UUID playerUuid, @NotNull String rankId) {
    rankChallengeProgressionDao.openSession();
    try {
      List<RankChallengeProgression> rankChallengeProgressions =
          rankChallengeProgressionDao.find(playerUuid, rankId);
      debugger.debug("RankChallengeProgressions found: {}", rankChallengeProgressions);
      return rankChallengeProgressions;
    } finally {
      rankChallengeProgressionDao.destroySession();
    }
  }

  public int giveItemChallenge(
      @NotNull UUID playerUuid,
      @NotNull String rankId,
      @NotNull Material material,
      int givenAmount) {
    int effectiveGivenAmount = 0;
    RankConfig.RankInfo rankInfo =
        rankConfig.getRanks().stream()
            .filter(rank -> rank.getId().equals(rankId))
            .findFirst()
            .orElseThrow();
    RankConfig.RankChallenge rankChallenge =
        Objects.requireNonNull(rankInfo.getRankUpChallenges()).stream()
            .filter(challenge -> challenge.getChallengeItemMaterial().equals(material))
            .findFirst()
            .orElseThrow();

    rankChallengeProgressionDao.openSession();
    Transaction tx = rankChallengeProgressionDao.beginTransaction();
    try {
      RankChallengeProgression rankChallengeProgression =
          rankChallengeProgressionDao.find(playerUuid, rankId, material).orElse(null);
      if (rankChallengeProgression == null) {
        debugger.debug("RankChallengeProgression not found.");
        rankChallengeProgression = new RankChallengeProgression(playerUuid, rankId, material);
        rankChallengeProgressionDao.persist(rankChallengeProgression);
        debugger.debug("RankChallengeProgression persisted.");
      }

      int remainingToGiveAmount =
          rankChallenge.getChallengeItemAmount()
              - rankChallengeProgression.getChallengeAmountGiven();
      debugger.debug(String.format("remainingToGiveAmount=%d", remainingToGiveAmount));
      effectiveGivenAmount = Math.min(givenAmount, remainingToGiveAmount);
      debugger.debug(String.format("effectiveGivenAmount=%d", effectiveGivenAmount));
      int newAmount = rankChallengeProgression.getChallengeAmountGiven() + effectiveGivenAmount;
      rankChallengeProgression.setChallengeAmountGiven(newAmount);
      debugger.debug(String.format("newAmount=%d", newAmount));
      rankChallengeProgressionDao.update(rankChallengeProgression);
      tx.commit();
      debugger.debug("RankChallengeProgression updated: {}", rankChallengeProgression);
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
}
