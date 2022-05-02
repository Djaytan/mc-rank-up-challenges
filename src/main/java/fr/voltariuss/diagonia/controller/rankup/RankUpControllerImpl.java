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

package fr.voltariuss.diagonia.controller.rankup;

import fr.voltariuss.diagonia.RemakeBukkitLogger;
import fr.voltariuss.diagonia.controller.MessageController;
import fr.voltariuss.diagonia.model.config.data.rank.Rank;
import fr.voltariuss.diagonia.model.dto.RankUpProgression;
import fr.voltariuss.diagonia.model.service.api.EconomyService;
import fr.voltariuss.diagonia.model.service.api.JobsService;
import fr.voltariuss.diagonia.model.service.api.RankService;
import fr.voltariuss.diagonia.model.service.api.RankUpService;
import fr.voltariuss.diagonia.view.gui.RankUpChallengesGui;
import fr.voltariuss.diagonia.view.gui.RankUpListGui;
import fr.voltariuss.diagonia.view.message.CommonMessage;
import fr.voltariuss.diagonia.view.message.RankUpMessage;
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

    rankUpChallengesGui.get().open(whoOpen, rank, rankUpProgression);
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
