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

package fr.voltariuss.diagonia.controller.playershop;

import fr.voltariuss.diagonia.RemakeBukkitLogger;
import fr.voltariuss.diagonia.controller.BukkitUtils;
import fr.voltariuss.diagonia.controller.MessageController;
import fr.voltariuss.diagonia.model.dto.mapper.LocationMapper;
import fr.voltariuss.diagonia.model.config.data.PluginConfig;
import fr.voltariuss.diagonia.model.dto.response.EconomyResponse;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.model.service.api.exception.EconomyException;
import fr.voltariuss.diagonia.model.service.api.EconomyService;
import fr.voltariuss.diagonia.model.service.api.PlayerShopService;
import fr.voltariuss.diagonia.view.message.CommonMessage;
import fr.voltariuss.diagonia.view.message.PlayerShopMessage;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlayerShopListControllerImpl implements PlayerShopListController {

  private final BukkitUtils bukkitUtils;
  private final CommonMessage commonMessage;
  private final EconomyService economyService;
  private final LocationMapper locationMapper;
  private final RemakeBukkitLogger logger;
  private final MessageController messageController;
  private final PlayerShopController playerShopController;
  private final PlayerShopMessage playerShopMessage;
  private final PlayerShopService playerShopService;
  private final PluginConfig pluginConfig;
  private final Server server;

  @Inject
  public PlayerShopListControllerImpl(
      @NotNull BukkitUtils bukkitUtils,
      @NotNull CommonMessage commonMessage,
      @NotNull EconomyService economyService,
      @NotNull LocationMapper locationMapper,
      @NotNull RemakeBukkitLogger logger,
      @NotNull MessageController messageController,
      @NotNull PlayerShopController playerShopController,
      @NotNull PlayerShopMessage playerShopMessage,
      @NotNull PlayerShopService playerShopService,
      @NotNull PluginConfig pluginConfig,
      @NotNull Server server) {
    this.bukkitUtils = bukkitUtils;
    this.commonMessage = commonMessage;
    this.economyService = economyService;
    this.locationMapper = locationMapper;
    this.logger = logger;
    this.messageController = messageController;
    this.playerShopController = playerShopController;
    this.playerShopMessage = playerShopMessage;
    this.playerShopService = playerShopService;
    this.pluginConfig = pluginConfig;
    this.server = server;
  }

  @Override
  public void teleportToPlayerShop(
      @NotNull Player playerToTp, @NotNull PlayerShop playerShopDestination) {
    logger.debug(
        "Start event handling of teleporting a player to a playershop: playerToTpUuid={},"
            + " playerShopDestinationId={}",
        playerToTp.getUniqueId(),
        playerShopDestination.getId());

    Location tpLocation = locationMapper.fromDto(playerShopDestination.getTpLocationDto());

    if (tpLocation == null) {
      logger.warn(
          "Failed to teleport a player to a playershop because no teleport point has been defined."
              + " This may be an error because activated playershops are supposed to have a"
              + " teleport point defined: playerToTpUuid={}, playerShopId={}",
          playerToTp.getUniqueId(),
          playerShopDestination.getId());
      messageController.sendFailureMessage(playerToTp, playerShopMessage.noTeleportPointDefined());
      return;
    }

    playerToTp.teleport(tpLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);

    OfflinePlayer psOwner = server.getOfflinePlayer(playerShopDestination.getOwnerUuid());
    String psOwnerName = bukkitUtils.getOfflinePlayerName(psOwner);
    messageController.sendInfoMessage(playerToTp, playerShopMessage.successTeleport(psOwnerName));

    logger.debug(
        "Teleportation of a player to a playershop: playerToTpUuid={}, playerShopId={}",
        playerToTp.getUniqueId(),
        playerShopDestination.getId());
  }

  @Override
  public void teleportToPlayerShop(@NotNull Player playerToTp, @NotNull String targetedPlayerName) {
    OfflinePlayer targetedOfflinePlayer = server.getOfflinePlayerIfCached(targetedPlayerName);

    if (targetedOfflinePlayer == null) {
      messageController.sendFailureMessage(
          playerToTp, commonMessage.playerNotFound(targetedPlayerName));
      return;
    }

    Optional<PlayerShop> playerShop =
        playerShopService.findByUuid(targetedOfflinePlayer.getUniqueId());

    if (playerShop.isEmpty()) {
      messageController.sendFailureMessage(
          playerToTp, playerShopMessage.noPlayerShopForSpecifiedPlayer());
      return;
    }

    if (!playerShop.get().isActive()) {
      messageController.sendFailureMessage(playerToTp, playerShopMessage.playerShopDeactivated());
      return;
    }

    teleportToPlayerShop(playerToTp, playerShop.get());
  }

  @Override
  public void buyPlayerShop(@NotNull Player player) {
    // TODO: 2PC with JTA
    logger.debug("Start buying a playershop for player {}", player.getName());

    double playerShopPrice = pluginConfig.getPlayerShop().getBuyCost();

    if (!economyService.isAffordable(player, playerShopPrice)) {
      messageController.sendFailureMessage(player, playerShopMessage.insufficientFunds());
      logger.debug(
          "The player can't afford a playershop: playerName={}, playerShopPrice={}",
          player.getName(),
          playerShopPrice);
      return;
    }

    try {
      EconomyResponse economyResponse = economyService.withdraw(player, playerShopPrice);
      PlayerShop ps = new PlayerShop(player.getUniqueId());
      playerShopService.persist(ps);
      messageController.sendSuccessMessage(player, playerShopMessage.buySuccess(economyResponse));
      playerShopController.openPlayerShopListGui(player);
      logger.info(
          "Purchase of a playershop for the player {} ({}) for the price of {}. New solde: {}",
          player.getName(),
          player.getUniqueId(),
          economyResponse.getModifiedAmount(),
          economyResponse.getNewBalance());
    } catch (EconomyException e) {
      logger.error(
          "Failed to withdraw money from the player's balance: playerShopPrice={}, playerName={},"
              + " errorMessage={}",
          playerShopPrice,
          player.getName(),
          e.getMessage());
      messageController.sendErrorMessage(player, playerShopMessage.transactionFailed());
    }
  }
}
