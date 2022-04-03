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
import fr.voltariuss.diagonia.controller.ControllerHelper;
import fr.voltariuss.diagonia.model.GiveActionType;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.config.rank.RankChallenge;
import fr.voltariuss.diagonia.model.dto.RankUpProgression;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import fr.voltariuss.diagonia.model.service.RankChallengeProgressionService;
import fr.voltariuss.diagonia.model.service.RankConfigService;
import fr.voltariuss.diagonia.model.service.RankService;
import fr.voltariuss.diagonia.view.message.RankUpMessage;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.luckperms.api.track.PromotionResult;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankUpChallengesControllerImpl implements RankUpChallengesController {

  private final ControllerHelper controllerHelper;
  private final DiagoniaLogger logger;
  private final RankChallengeProgressionService rankChallengeProgressionService;
  private final RankConfigService rankConfigService;
  private final RankService rankService;
  private final RankUpController rankUpController;
  private final RankUpMessage rankUpMessage;

  @Inject
  public RankUpChallengesControllerImpl(
      @NotNull ControllerHelper controllerHelper,
      @NotNull DiagoniaLogger logger,
      @NotNull RankChallengeProgressionService rankChallengeProgressionService,
      @NotNull RankConfigService rankConfigService,
      @NotNull RankService rankService,
      @NotNull RankUpController rankUpController,
      @NotNull RankUpMessage rankUpMessage) {
    this.controllerHelper = controllerHelper;
    this.logger = logger;
    this.rankChallengeProgressionService = rankChallengeProgressionService;
    this.rankConfigService = rankConfigService;
    this.rankService = rankService;
    this.rankUpController = rankUpController;
    this.rankUpMessage = rankUpMessage;
  }

  @Override
  public void giveItemChallenge(
      @NotNull Player targetPlayer,
      @NotNull Rank rank,
      @NotNull RankChallenge rankChallenge,
      @NotNull GiveActionType giveActionType,
      int nbItemsInInventory) {
    // TODO: again... Transaction!
    int nbItemsGiven =
        rankChallengeProgressionService.giveItemChallenge(
            targetPlayer.getUniqueId(), rank, rankChallenge, giveActionType, nbItemsInInventory);

    Map<Integer, ItemStack> notRemovedItems =
        targetPlayer
            .getInventory()
            .removeItem(new ItemStack(rankChallenge.getChallengeItemMaterial(), nbItemsGiven));

    if (!notRemovedItems.isEmpty()) {
      logger.error(
          "Some items failed to be removed from the {}'s inventory: {}",
          targetPlayer.getName(),
          notRemovedItems);
      // TODO: feedback player
      return;
    }

    String challengeName = rankChallenge.getChallengeItemMaterial().name();

    targetPlayer.sendMessage(rankUpMessage.successAmountGiven(nbItemsGiven, challengeName));

    if (rankChallengeProgressionService.isChallengeCompleted(
        targetPlayer.getUniqueId(), rank.getId(), rankChallenge)) {
      rankUpMessage.challengeCompleted(challengeName);
    }

    rankUpController.openRankUpChallengesGui(targetPlayer, rank);
  }

  @Override
  public @NotNull Optional<RankChallengeProgression> findChallenge(
      @NotNull UUID playerUuid, @NotNull String rankId, @NotNull Material material) {
    return rankChallengeProgressionService.find(playerUuid, rankId, material);
  }

  @Override
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