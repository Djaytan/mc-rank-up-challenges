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

import com.google.common.base.Preconditions;
import fr.djaytan.minecraft.rank_up_challenges.RankUpChallengesRuntimeException;
import fr.djaytan.minecraft.rank_up_challenges.RemakeBukkitLogger;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.MessageController;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.RankUpController;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.EconomyService;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.RankService;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.RankUpService;
import fr.djaytan.minecraft.rank_up_challenges.view.message.RankUpMessage;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.RankUpChallengesController;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.Rank;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.RankConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.entity.RankChallengeProgression;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.dto.GiveActionType;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.dto.RankUpProgression;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.exception.EconomyException;
import fr.djaytan.minecraft.rank_up_challenges.view.message.CommonMessage;
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
  private final EconomyService economyService;
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
      @NotNull EconomyService economyService,
      @NotNull RemakeBukkitLogger logger,
      @NotNull MessageController messageController,
      @NotNull RankUpService rankUpService,
      @NotNull RankConfig rankConfig,
      @NotNull RankService rankService,
      @NotNull RankUpController rankUpController,
      @NotNull RankUpMessage rankUpMessage) {
    this.commonMessage = commonMessage;
    this.economyService = economyService;
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
      @NotNull Player player, @NotNull Rank rank, @NotNull RankUpProgression rankUpProgression) {
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

    try {
      economyService.withdraw(player, rank.getRankUpPrerequisites().getMoneyCost());
    } catch (EconomyException e) {
      throw new RankUpChallengesRuntimeException("Failed to withdraw money to player.", e);
    }

    player.giveExpLevels(-rank.getRankUpPrerequisites().getEnchantingLevelsCost());

    prepareRankChallenges(player);

    Rank newRank =
        rankConfig.findRankById(promotionResult.getGroupTo().orElseThrow()).orElseThrow();

    player.closeInventory(Reason.PLUGIN);
    messageController.broadcastMessage(rankUpMessage.rankUpSuccess(player, newRank));
  }

  @Override
  public void prepareRankChallenges(@NotNull Player player) {
    Rank rank = rankService.getUnlockableRank(player);

    // The player already have the latest rank
    if (rank == null) {
      return;
    }

    if (!rankUpService.hasRankChallenges(player.getUniqueId(), rank)) {
      logger.info("Rolling challenges of rank {} for player {}", rank.getName(), player.getName());
      rankUpService.rollRankChallenges(player.getUniqueId(), rank);
    }
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
