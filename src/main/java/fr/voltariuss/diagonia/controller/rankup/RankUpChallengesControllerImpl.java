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
import fr.voltariuss.diagonia.controller.BukkitUtils;
import fr.voltariuss.diagonia.controller.MessageController;
import fr.voltariuss.diagonia.model.GiveActionType;
import fr.voltariuss.diagonia.model.config.data.rank.Rank;
import fr.voltariuss.diagonia.model.config.data.rank.RankChallenge;
import fr.voltariuss.diagonia.model.config.data.rank.RankConfig;
import fr.voltariuss.diagonia.model.service.api.dto.RankUpProgression;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import fr.voltariuss.diagonia.model.service.api.RankService;
import fr.voltariuss.diagonia.model.service.api.RankUpService;
import fr.voltariuss.diagonia.view.message.CommonMessage;
import fr.voltariuss.diagonia.view.message.RankUpMessage;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.luckperms.api.track.PromotionResult;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankUpChallengesControllerImpl implements RankUpChallengesController {

  private final BukkitUtils bukkitUtils;
  private final CommonMessage commonMessage;
  private final RemakeBukkitLogger logger;
  private final MessageController messageController;
  private final RankUpService rankUpService;
  private final RankConfig rankConfig;
  private final RankService rankService;
  private final RankUpController rankUpController;
  private final RankUpMessage rankUpMessage;

  @Inject
  public RankUpChallengesControllerImpl(
      @NotNull BukkitUtils bukkitUtils,
      @NotNull CommonMessage commonMessage,
      @NotNull RemakeBukkitLogger logger,
      @NotNull MessageController messageController,
      @NotNull RankUpService rankUpService,
      @NotNull RankConfig rankConfig,
      @NotNull RankService rankService,
      @NotNull RankUpController rankUpController,
      @NotNull RankUpMessage rankUpMessage) {
    this.bukkitUtils = bukkitUtils;
    this.commonMessage = commonMessage;
    this.logger = logger;
    this.messageController = messageController;
    this.rankUpService = rankUpService;
    this.rankConfig = rankConfig;
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
    if (rankUpService.isChallengeCompleted(
        targetPlayer.getUniqueId(), rank.getId(), rankChallenge)) {
      messageController.sendFailureMessage(targetPlayer, rankUpMessage.challengeAlreadyCompleted());
      return;
    }

    if (nbItemsInInventory == 0) {
      messageController.sendFailureMessage(targetPlayer, rankUpMessage.noItemInInventory());
      return;
    }

    // TODO: again... Transaction!
    int nbItemsEffectivelyGiven =
        rankUpService.giveItemChallenge(
            targetPlayer.getUniqueId(), rank, rankChallenge, giveActionType, nbItemsInInventory);

    int nbItemsNotRemoved =
        bukkitUtils.removeItemsInInventory(
            targetPlayer.getInventory(), rankChallenge.getMaterial(), nbItemsEffectivelyGiven);

    if (nbItemsNotRemoved > 0) {
      logger.error(
          "Something went wrong during the removing of items in the targeted player's inventory:"
              + " playerName={}, challengeMaterialName={}, nbItemsNotRemoved={}",
          targetPlayer.getName(),
          rankChallenge.getMaterial(),
          nbItemsNotRemoved);
      messageController.sendErrorMessage(targetPlayer, commonMessage.unexpectedError());
      return;
    }

    Component challengeNameCpnt =
        Component.translatable(rankChallenge.getMaterial().translationKey());

    messageController.sendInfoMessage(
        targetPlayer, rankUpMessage.successAmountGiven(nbItemsEffectivelyGiven, challengeNameCpnt));

    if (rankUpService.isChallengeCompleted(
        targetPlayer.getUniqueId(), rank.getId(), rankChallenge)) {
      messageController.sendSuccessMessage(
          targetPlayer, rankUpMessage.challengeCompleted(challengeNameCpnt));
    }

    rankUpController.openRankUpChallengesGui(targetPlayer, rank);
  }

  @Override
  public @NotNull Optional<RankChallengeProgression> findChallenge(
      @NotNull UUID playerUuid, @NotNull String rankId, @NotNull Material material) {
    return rankUpService.findChallengeProgression(playerUuid, rankId, material);
  }

  @Override
  public void onRankUpRequested(
      @NotNull Player player, @NotNull RankUpProgression rankUpProgression) {
    if (rankUpProgression.isRankOwned()) {
      messageController.sendFailureMessage(player, rankUpMessage.rankAlreadyOwned());
      return;
    }

    if (!rankUpProgression.canRankUp()) {
      messageController.sendFailureMessage(player, rankUpMessage.prerequisitesNotRespected());
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
        rankConfig.findRankById(promotionResult.getGroupTo().orElseThrow()).orElseThrow();

    player.closeInventory(Reason.PLUGIN);
    messageController.broadcastMessage(rankUpMessage.rankUpSuccess(player, newRank));
  }
}
