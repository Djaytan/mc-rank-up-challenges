/*
 * Rank-Up challenges plugin for Minecraft (Bukkit servers)
 * Copyright (C) 2022 - Loïc DUBOIS-TERMOZ
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.djaytan.minecraft.rank_up_challenges.controller.implementation;

import fr.djaytan.minecraft.rank_up_challenges.RemakeBukkitLogger;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.MessageController;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.RankUpController;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.EconomyService;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.JobsService;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.RankService;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.RankUpService;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.dto.RankUpProgression;
import fr.djaytan.minecraft.rank_up_challenges.view.gui.RankUpListGui;
import fr.djaytan.minecraft.rank_up_challenges.view.message.RankUpMessage;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.Rank;
import fr.djaytan.minecraft.rank_up_challenges.model.entity.RankChallengeProgression;
import fr.djaytan.minecraft.rank_up_challenges.view.gui.RankUpChallengesGui;
import fr.djaytan.minecraft.rank_up_challenges.view.message.CommonMessage;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankUpControllerImpl implements RankUpController {

  private final CommonMessage commonMessage;
  private final EconomyService economyService;
  private final JobsService jobsService;
  private final RemakeBukkitLogger logger;
  private final MessageController messageController;
  private final RankService rankService;
  private final RankUpMessage rankUpMessage;
  private final RankUpService rankUpService;

  private final Provider<RankUpListGui> rankUpListGui;
  private final Provider<RankUpChallengesGui> rankUpChallengesGui;

  @Inject
  public RankUpControllerImpl(
      @NotNull CommonMessage commonMessage,
      @NotNull EconomyService economyService,
      @NotNull JobsService jobsService,
      @NotNull RemakeBukkitLogger logger,
      @NotNull MessageController messageController,
      @NotNull RankService rankService,
      @NotNull RankUpMessage rankUpMessage,
      @NotNull RankUpService rankUpService,
      @NotNull Provider<RankUpListGui> rankUpListGui,
      @NotNull Provider<RankUpChallengesGui> rankUpChallengesGui) {
    this.commonMessage = commonMessage;
    this.economyService = economyService;
    this.jobsService = jobsService;
    this.logger = logger;
    this.messageController = messageController;
    this.rankService = rankService;
    this.rankUpMessage = rankUpMessage;
    this.rankUpService = rankUpService;
    this.rankUpListGui = rankUpListGui;
    this.rankUpChallengesGui = rankUpChallengesGui;
  }

  @Override
  public void openRankUpListGui(@NotNull Player whoOpen) {
    logger.debug("Open RankUpList GUI for player '{}'", whoOpen.getName());
    rankUpListGui.get().open(whoOpen);
  }

  @Override
  public void openRankUpChallengesGui(@NotNull Player whoOpen, @NotNull Rank rank) {
    logger.debug(
        "Open RankUpChallenges GUI of rank '{}' for player '{}'", rank.getId(), whoOpen.getName());

    if (rank.getRankUpChallenges() == null) {
      logger.error("No challenge is associated with the rank {}", rank.getId());
      messageController.sendErrorMessage(whoOpen, commonMessage.unexpectedError());
      return;
    }

    int totalJobsLevels = jobsService.getTotalLevels(whoOpen);
    double currentBalance = economyService.getBalance(whoOpen);

    RankUpProgression rankUpProgression =
        rankUpService.getRankUpProgression(whoOpen, rank, totalJobsLevels, currentBalance);

    List<RankChallengeProgression> rankChallengesProgressions =
        rankUpService.findChallengesProgressions(whoOpen.getUniqueId(), rank.getId());

    rankUpChallengesGui.get().open(whoOpen, rank, rankUpProgression, rankChallengesProgressions);
  }

  @Override
  public void openCurrentRankUpChallengesGui(@NotNull Player whoOpen) {
    Rank unlockableRank = rankService.getUnlockableRank(whoOpen);

    if (unlockableRank == null) {
      logger.debug("The player already has the highest rank: playerName={}", whoOpen.getName());
      messageController.sendFailureMessage(whoOpen, rankUpMessage.alreadyHasHighestRank());
      return;
    }

    openRankUpChallengesGui(whoOpen, unlockableRank);
  }
}
