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

package fr.voltariuss.diagonia.controller;

import fr.voltariuss.diagonia.DiagoniaLogger;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.dto.RankUpProgression;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import fr.voltariuss.diagonia.model.service.EconomyService;
import fr.voltariuss.diagonia.model.service.JobsService;
import fr.voltariuss.diagonia.model.service.RankChallengeProgressionService;
import fr.voltariuss.diagonia.model.service.RankConfigService;
import fr.voltariuss.diagonia.model.service.RankService;
import fr.voltariuss.diagonia.view.gui.RankUpChallengesGui;
import fr.voltariuss.diagonia.view.gui.RankUpListGui;
import fr.voltariuss.diagonia.view.message.RankUpMessage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import net.luckperms.api.track.PromotionResult;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankUpController {

  private final DiagoniaLogger logger;
  private final EconomyService economyService;
  private final JobsService jobsService;
  private final ControllerHelper controllerHelper;
  private final RankChallengeProgressionService rankChallengeProgressionService;
  private final RankConfigService rankConfigService;
  private final RankService rankService;
  private final RankUpMessage rankUpMessage;

  private final Provider<RankUpListGui> rankUpListGui;
  private final Provider<RankUpChallengesGui> rankUpChallengesGui;

  @Inject
  public RankUpController(
      @NotNull DiagoniaLogger logger,
      @NotNull EconomyService economyService,
      @NotNull JobsService jobsService,
      @NotNull ControllerHelper controllerHelper,
      @NotNull RankChallengeProgressionService rankChallengeProgressionService,
      @NotNull RankConfigService rankConfigService,
      @NotNull RankService rankService,
      @NotNull RankUpMessage rankUpMessage,
      @NotNull Provider<RankUpListGui> rankUpListGui,
      @NotNull Provider<RankUpChallengesGui> rankUpChallengesGui) {
    this.logger = logger;
    this.economyService = economyService;
    this.jobsService = jobsService;
    this.controllerHelper = controllerHelper;
    this.rankChallengeProgressionService = rankChallengeProgressionService;
    this.rankConfigService = rankConfigService;
    this.rankService = rankService;
    this.rankUpMessage = rankUpMessage;
    this.rankUpListGui = rankUpListGui;
    this.rankUpChallengesGui = rankUpChallengesGui;
  }

  public void openRankUpListGui(@NotNull Player whoOpen) {
    logger.debug("Open RankUpList GUI for player '{}'", whoOpen.getName());
    rankUpListGui.get().open(whoOpen);
  }

  public void openRankUpChallengesGui(@NotNull Player whoOpen, @NotNull Rank rank) {
    logger.debug(
        "Open RankUpChallenges GUI of rank '{}' for player '{}'", rank.getId(), whoOpen.getName());

    int totalJobsLevels = jobsService.getTotalLevels(whoOpen);
    double currentBalance = economyService.getBalance(whoOpen);

    RankUpProgression rankUpProgression =
        rankService.getRankUpProgression(whoOpen, rank, totalJobsLevels, currentBalance);

    rankUpChallengesGui.get().open(whoOpen, rank, rankUpProgression);
  }

  public int giveItemChallenge(
      @NotNull Player targetPlayer,
      @NotNull String rankId,
      @NotNull Material material,
      int givenAmount) {
    return rankChallengeProgressionService.giveItemChallenge(
        targetPlayer.getUniqueId(), rankId, material, givenAmount);
  }

  public @NotNull Optional<RankChallengeProgression> findChallenge(
      @NotNull UUID playerUuid, @NotNull String rankId, @NotNull Material material) {
    return rankChallengeProgressionService.find(playerUuid, rankId, material);
  }

  public @NotNull List<RankChallengeProgression> getRankUpProgression(
      @NotNull Player targetPlayer, @NotNull String rankId) {
    return rankChallengeProgressionService.find(targetPlayer.getUniqueId(), rankId);
  }

  // TODO: remove these three following methods
  public boolean isRankOwned(@NotNull Player player, @NotNull String rankId) {
    return rankService.isRankOwned(player, rankId);
  }

  public boolean isCurrentRank(@NotNull Player player, @NotNull String rankId) {
    return rankService.isCurrentRank(player, rankId);
  }

  public boolean isUnlockableRank(@NotNull Player player, @NotNull String rankId) {
    return rankService.isUnlockableRank(player, rankId);
  }

  public void onRankUpRequested(
      @NotNull Player player, @NotNull RankUpProgression rankUpProgression) {

    if (rankUpProgression.canRankUp()) {
      PromotionResult promotionResult = rankService.promote(player);

      if (!promotionResult.wasSuccessful()) {
        controllerHelper.sendSystemMessage(player, rankUpMessage.rankUpFailure());
        logger.error(
            "Player promotion failed: playerName={}, promotionResult={}",
            player.getName(),
            promotionResult);
        return;
      }

      Rank newRank =
          rankConfigService.findById(promotionResult.getGroupTo().orElseThrow()).orElseThrow();

      player.closeInventory(Reason.PLUGIN);
      controllerHelper.broadcastMessage(rankUpMessage.rankUpSuccess(player, newRank));
      return;
    }

    controllerHelper.sendSystemMessage(player, rankUpMessage.prerequisitesNotRespected());
  }
}
