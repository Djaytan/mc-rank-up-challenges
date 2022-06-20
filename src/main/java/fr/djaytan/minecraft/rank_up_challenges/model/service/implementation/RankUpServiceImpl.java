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

package fr.djaytan.minecraft.rank_up_challenges.model.service.implementation;

import com.google.common.base.Preconditions;
import fr.djaytan.minecraft.rank_up_challenges.RankUpChallengesRuntimeException;
import fr.djaytan.minecraft.rank_up_challenges.RemakeBukkitLogger;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.challenge.ChallengeConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.challenge.ChallengeType;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.challenge.ComputedChallenge;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.RankUpChallengeDistribution;
import fr.djaytan.minecraft.rank_up_challenges.model.dao.RankChallengeProgressionDao;
import fr.djaytan.minecraft.rank_up_challenges.model.entity.RankChallengeProgression;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.RankService;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.RankUpService;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.dto.GiveActionType;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.dto.RankUpProgression;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.Rank;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.RankConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.RankUpChallenges;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.RankUpPrerequisites;
import fr.djaytan.minecraft.rank_up_challenges.model.dao.JpaDaoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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

  private final ChallengeConfig challengeConfig;
  private final RemakeBukkitLogger logger;
  private final RankChallengeProgressionDao rankChallengeProgressionDao;
  private final RankConfig rankConfig;
  private final RankService rankService;

  @Inject
  public RankUpServiceImpl(
      @NotNull ChallengeConfig challengeConfig,
      @NotNull RemakeBukkitLogger logger,
      @NotNull RankChallengeProgressionDao rankChallengeProgressionDao,
      @NotNull RankConfig rankConfig,
      @NotNull RankService rankService) {
    this.challengeConfig = challengeConfig;
    this.logger = logger;
    this.rankChallengeProgressionDao = rankChallengeProgressionDao;
    this.rankConfig = rankConfig;
    this.rankService = rankService;
  }

  @Override
  public boolean areChallengesCompleted(@NotNull Player player, @NotNull Rank rank) {
    Preconditions.checkNotNull(player);
    Preconditions.checkNotNull(rank);
    Preconditions.checkNotNull(rank.getRankUpChallenges());

    List<RankChallengeProgression> playerProgression =
        findChallengesProgressions(player.getUniqueId(), rank.getId());

    long numberUnaccomplishedChallenges =
        playerProgression.stream()
            .filter(rcp -> rcp.getChallengeAmountGiven() < rcp.getChallengeAmountRequired())
            .count();

    return numberUnaccomplishedChallenges == 0;
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
  public @NotNull RankUpProgression getRankUpProgression(
      @NotNull Player player,
      @NotNull Rank targetedRank,
      int totalJobsLevels,
      double currentBalance) {
    Preconditions.checkArgument(
        totalJobsLevels >= 0, "The total jobs levels must be higher or equals to 0.");

    Rank unlockableRank =
        rankConfig
            .findRankById(targetedRank.getId())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "The specified rank ID isn't associated with any registered rank in"
                            + " configuration."));

    RankUpPrerequisites prerequisites = unlockableRank.getRankUpPrerequisites();

    if (prerequisites == null) {
      throw new IllegalStateException(
          String.format(
              "Unlockable rank '%s' don't have any challenge to accomplish which is not allowed.",
              unlockableRank.getId()),
          new NullPointerException("Prerequisites list of unlockable rank is null."));
    }

    int currentEnchantingLevels = player.getLevel();
    int requiredEnchantingLevels = prerequisites.getEnchantingLevelsCost();
    boolean isEnchantingLevelsPrerequisiteDone =
        currentEnchantingLevels >= requiredEnchantingLevels;

    int requiredJobsLevels = prerequisites.getJobsLevels();
    boolean isJobsLevelsPrerequisiteDone = totalJobsLevels >= requiredJobsLevels;

    double moneyCost = prerequisites.getMoneyCost();
    boolean isMoneyPrerequisiteDone = currentBalance >= moneyCost;

    boolean isChallengesPrerequisiteDone = areChallengesCompleted(player, unlockableRank);

    boolean isRankOwned = rankService.isRankOwned(player, unlockableRank.getId());

    return RankUpProgression.builder()
        .currentXpLevel(currentEnchantingLevels)
        .isXpLevelPrerequisiteDone(isEnchantingLevelsPrerequisiteDone)
        .totalJobsLevels(totalJobsLevels)
        .isTotalJobsLevelsPrerequisiteDone(isJobsLevelsPrerequisiteDone)
        .currentBalance(currentBalance)
        .isMoneyPrerequisiteDone(isMoneyPrerequisiteDone)
        .isChallengesPrerequisiteDone(isChallengesPrerequisiteDone)
        .isRankOwned(isRankOwned)
        .build();
  }

  @Override
  public int giveItemChallenge(
      @NotNull UUID playerUuid,
      @NotNull Rank rank,
      @NotNull Material challengeMaterial,
      @NotNull GiveActionType giveActionType,
      int nbItemsInInventory) {
    int effectiveGivenAmount;
    rankChallengeProgressionDao.openSession();
    Transaction tx = rankChallengeProgressionDao.beginTransaction();
    try {
      RankChallengeProgression rankChallengeProgression =
          rankChallengeProgressionDao
              .find(playerUuid, rank.getId(), challengeMaterial)
              .orElse(null);

      // This is never supposed to happen
      if (rankChallengeProgression == null) {
        throw new RankUpChallengesRuntimeException(
            "RankChallengeProgression can't be null: wrong material specified? missing to create"
                + " RankChallengeProgression instance before calling this method?");
      }

      int remainingItemsToGive =
          rankChallengeProgression.getChallengeAmountRequired()
              - rankChallengeProgression.getChallengeAmountGiven();

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
      @NotNull UUID uuid, @NotNull String rankId, @NotNull Material challengeMaterial) {
    rankChallengeProgressionDao.openSession();

    Optional<RankChallengeProgression> rankChallengeProgression;

    try {
      rankChallengeProgression = rankChallengeProgressionDao.find(uuid, rankId, challengeMaterial);
    } finally {
      rankChallengeProgressionDao.destroySession();
    }

    if (rankChallengeProgression.isEmpty()) {
      return false;
    }

    return rankChallengeProgression.get().getChallengeAmountGiven()
        >= rankChallengeProgression.get().getChallengeAmountRequired();
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

  @Override
  public boolean hasRankChallenges(@NotNull UUID playerUuid, @NotNull Rank rank) {
    return !findChallengesProgressions(playerUuid, rank.getId()).isEmpty();
  }

  @Override
  public void rollRankChallenges(@NotNull UUID playerUuid, @NotNull Rank rank) {
    List<RankUpChallenges> rankUpChallengesList = rank.getRankUpChallenges();
    List<RankChallengeProgression> rankChallengeProgressions = new ArrayList<>();
    Random random = new Random();

    // Algorithm which determine challenges of the given rank
    for (RankUpChallenges rankUpChallenges : rankUpChallengesList) {
      int tier = rankUpChallenges.getTier();
      List<ComputedChallenge> computedChallenges = challengeConfig.getComputedChallenges(tier);

      // Create challenges
      for (RankUpChallengeDistribution distribution : rankUpChallenges.getDistributions()) {
        ChallengeType challengeType = distribution.getChallengeType();
        int numberOfChallenges = distribution.getNumberOfChallenges();

        for (int i = 0; i < numberOfChallenges; i++) {
          List<ComputedChallenge> eligibleComputedChallenges =
              computedChallenges.stream()
                  .filter(
                      computedChallenge ->
                          computedChallenge.getChallenge().getChallengeType() == challengeType)
                  .filter(
                      computedChallenge ->
                          rankChallengeProgressions.stream()
                              .map(RankChallengeProgression::getChallengeMaterial)
                              .noneMatch(
                                  material ->
                                      material == computedChallenge.getChallenge().getMaterial()))
                  .toList();

          int randomComputedChallengeIndex = random.nextInt(0, eligibleComputedChallenges.size());
          ComputedChallenge computedChallenge =
              eligibleComputedChallenges.get(randomComputedChallengeIndex);

          RankChallengeProgression rankChallengeProgression =
              RankChallengeProgression.builder()
                  .playerUuid(playerUuid)
                  .rankId(rank.getId())
                  .difficultyTier(tier)
                  .challengeType(challengeType)
                  .challengeMaterial(computedChallenge.getChallenge().getMaterial())
                  .challengeAmountGiven(0)
                  .challengeAmountRequired(computedChallenge.getComputedAmount())
                  .build();

          rankChallengeProgressions.add(rankChallengeProgression);
        }
      }
    }

    for (RankChallengeProgression rankChallengeProgression : rankChallengeProgressions) {
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
}
