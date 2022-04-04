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
import fr.voltariuss.diagonia.controller.MessageController;
import fr.voltariuss.diagonia.model.GiveActionType;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.config.rank.RankChallenge;
import fr.voltariuss.diagonia.model.dto.RankUpProgression;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import fr.voltariuss.diagonia.model.service.RankChallengeProgressionService;
import fr.voltariuss.diagonia.model.service.RankConfigService;
import fr.voltariuss.diagonia.model.service.RankService;
import fr.voltariuss.diagonia.view.message.CommonMessage;
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

  private final CommonMessage commonMessage;
  private final DiagoniaLogger logger;
  private final MessageController messageController;
  private final RankChallengeProgressionService rankChallengeProgressionService;
  private final RankConfigService rankConfigService;
  private final RankService rankService;
  private final RankUpController rankUpController;
  private final RankUpMessage rankUpMessage;

  @Inject
  public RankUpChallengesControllerImpl(
      @NotNull CommonMessage commonMessage,
      @NotNull DiagoniaLogger logger,
      @NotNull MessageController messageController,
      @NotNull RankChallengeProgressionService rankChallengeProgressionService,
      @NotNull RankConfigService rankConfigService,
      @NotNull RankService rankService,
      @NotNull RankUpController rankUpController,
      @NotNull RankUpMessage rankUpMessage) {
    this.commonMessage = commonMessage;
    this.logger = logger;
    this.messageController = messageController;
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
    if (nbItemsInInventory == 0) {
      messageController.sendWarningMessage(targetPlayer, rankUpMessage.noItemInInventory());
      return;
    }

    if (rankChallengeProgressionService.isChallengeCompleted(
        targetPlayer.getUniqueId(), rank.getId(), rankChallenge)) {
      messageController.sendWarningMessage(targetPlayer, rankUpMessage.challengeAlreadyCompleted());
      return;
    }

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
      messageController.sendErrorMessage(targetPlayer, commonMessage.unexpectedError());
      return;
    }

    String challengeName = rankChallenge.getChallengeItemMaterial().name();

    messageController.sendSystemMessage(
        targetPlayer, rankUpMessage.successAmountGiven(nbItemsGiven, challengeName));

    if (rankChallengeProgressionService.isChallengeCompleted(
        targetPlayer.getUniqueId(), rank.getId(), rankChallenge)) {
      messageController.sendSystemMessage(
          targetPlayer, rankUpMessage.challengeCompleted(challengeName));
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

    if (!rankUpProgression.canRankUp()) {
      messageController.sendWarningMessage(player, rankUpMessage.prerequisitesNotRespected());
      return;
    }

    PromotionResult promotionResult = rankService.promote(player);

    if (!promotionResult.wasSuccessful()) {
      messageController.sendErrorMessage(player, rankUpMessage.rankUpFailure());
      logger.error(
          "Player promotion failed: playerName={}, promotionResult={}",
          player.getName(),
          promotionResult);
      return;
    }

    Rank newRank =
        rankConfigService.findById(promotionResult.getGroupTo().orElseThrow()).orElseThrow();

    player.closeInventory(Reason.PLUGIN);
    messageController.broadcastMessage(rankUpMessage.rankUpSuccess(player, newRank));
  }
}
