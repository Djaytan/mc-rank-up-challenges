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

package fr.voltariuss.diagonia.controller.implementation;

import com.google.common.base.Preconditions;
import fr.voltariuss.diagonia.RemakeBukkitLogger;
import fr.voltariuss.diagonia.controller.api.MessageController;
import fr.voltariuss.diagonia.controller.api.RankUpChallengesController;
import fr.voltariuss.diagonia.controller.api.RankUpController;
import fr.voltariuss.diagonia.model.config.data.rank.Rank;
import fr.voltariuss.diagonia.model.config.data.rank.RankConfig;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import fr.voltariuss.diagonia.model.service.api.RankService;
import fr.voltariuss.diagonia.model.service.api.RankUpService;
import fr.voltariuss.diagonia.model.service.api.dto.GiveActionType;
import fr.voltariuss.diagonia.model.service.api.dto.RankUpProgression;
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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankUpChallengesControllerImpl implements RankUpChallengesController {

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
      @NotNull CommonMessage commonMessage,
      @NotNull RemakeBukkitLogger logger,
      @NotNull MessageController messageController,
      @NotNull RankUpService rankUpService,
      @NotNull RankConfig rankConfig,
      @NotNull RankService rankService,
      @NotNull RankUpController rankUpController,
      @NotNull RankUpMessage rankUpMessage) {
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
      @NotNull Material challengeMaterial,
      @NotNull GiveActionType giveActionType,
      int nbItemsInInventory) {
    if (rankUpService.isChallengeCompleted(
        targetPlayer.getUniqueId(), rank.getId(), challengeMaterial)) {
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
            targetPlayer.getUniqueId(),
            rank,
            challengeMaterial,
            giveActionType,
            nbItemsInInventory);

    int nbItemsNotRemoved =
        removeItemsInInventory(
            targetPlayer.getInventory(), challengeMaterial, nbItemsEffectivelyGiven);

    if (nbItemsNotRemoved > 0) {
      logger.error(
          "Something went wrong during the removing of items in the targeted player's inventory:"
              + " playerName={}, challengeMaterialName={}, nbItemsNotRemoved={}",
          targetPlayer.getName(),
          challengeMaterial,
          nbItemsNotRemoved);
      messageController.sendErrorMessage(targetPlayer, commonMessage.unexpectedError());
      return;
    }

    Component challengeNameComponent = Component.translatable(challengeMaterial.translationKey());

    messageController.sendInfoMessage(
        targetPlayer,
        rankUpMessage.successAmountGiven(nbItemsEffectivelyGiven, challengeNameComponent));

    if (rankUpService.isChallengeCompleted(
        targetPlayer.getUniqueId(), rank.getId(), challengeMaterial)) {
      messageController.sendSuccessMessage(
          targetPlayer, rankUpMessage.challengeCompleted(challengeNameComponent));
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

  /**
   * Removes N items in the specified inventory which match with the given material.
   *
   * <p>Unfortunately, no method works like expected in {@link Inventory} interface.
   *
   * @param inventory The inventory targeted by the remove of items.
   * @param material The material of targeted items to remove in the inventory.
   * @param n The number of items to remove in the inventory.
   * @return The number of items not removed from the inventory (>= 0).
   */
  private int removeItemsInInventory(
      @NotNull Inventory inventory, @NotNull Material material, int n) {
    Preconditions.checkNotNull(inventory);
    Preconditions.checkNotNull(material);
    Preconditions.checkArgument(
        n >= 0, "The number of items to remove in inventory must be higher or equals to zero.");

    if (inventory.getType() != InventoryType.PLAYER) {
      logger.warn(
          "Trying to remove items to another inventory than those of type PLAYER: cancelled.");
      return 0;
    }

    if (inventory.isEmpty()) {
      return 0;
    }

    int remainingItemsToRemove = n;
    int storageContentsSize = inventory.getStorageContents().length;
    ItemStack[] newStorageContent = new ItemStack[storageContentsSize];

    for (int i = 0; i < storageContentsSize; i++) {
      ItemStack itemStack = inventory.getStorageContents()[i];

      if (itemStack == null || !itemStack.getType().equals(material)) {
        newStorageContent[i] = itemStack;
        continue;
      }

      int amountToRemove = Math.min(remainingItemsToRemove, itemStack.getAmount());
      int newAmount = itemStack.getAmount() - amountToRemove;

      remainingItemsToRemove -= amountToRemove;

      if (newAmount > 0) {
        itemStack.setAmount(newAmount);
        newStorageContent[i] = itemStack;
      }
    }

    inventory.setStorageContents(newStorageContent);
    return remainingItemsToRemove;
  }
}
