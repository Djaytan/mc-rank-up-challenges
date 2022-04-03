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

import fr.voltariuss.diagonia.DiagoniaLogger;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.dto.RankUpProgression;
import fr.voltariuss.diagonia.model.service.EconomyService;
import fr.voltariuss.diagonia.model.service.JobsService;
import fr.voltariuss.diagonia.model.service.RankService;
import fr.voltariuss.diagonia.view.gui.RankUpChallengesGui;
import fr.voltariuss.diagonia.view.gui.RankUpListGui;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankUpControllerImpl implements RankUpController {

  private final DiagoniaLogger logger;
  private final EconomyService economyService;
  private final JobsService jobsService;
  private final RankService rankService;

  private final Provider<RankUpListGui> rankUpListGui;
  private final Provider<RankUpChallengesGui> rankUpChallengesGui;

  @Inject
  public RankUpControllerImpl(
      @NotNull DiagoniaLogger logger,
      @NotNull EconomyService economyService,
      @NotNull JobsService jobsService,
      @NotNull RankService rankService,
      @NotNull Provider<RankUpListGui> rankUpListGui,
      @NotNull Provider<RankUpChallengesGui> rankUpChallengesGui) {
    this.logger = logger;
    this.economyService = economyService;
    this.jobsService = jobsService;
    this.rankService = rankService;
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

    int totalJobsLevels = jobsService.getTotalLevels(whoOpen);
    double currentBalance = economyService.getBalance(whoOpen);

    RankUpProgression rankUpProgression =
        rankService.getRankUpProgression(whoOpen, rank, totalJobsLevels, currentBalance);

    rankUpChallengesGui.get().open(whoOpen, rank, rankUpProgression);
  }
}