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

import fr.voltariuss.diagonia.DiagoniaLogger;
import fr.voltariuss.diagonia.controller.ControllerHelper;
import fr.voltariuss.diagonia.model.LocationMapper;
import fr.voltariuss.diagonia.model.config.PluginConfig;
import fr.voltariuss.diagonia.model.dto.response.EconomyResponse;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.model.service.EconomyException;
import fr.voltariuss.diagonia.model.service.EconomyService;
import fr.voltariuss.diagonia.model.service.PlayerShopService;
import fr.voltariuss.diagonia.view.message.PlayerShopMessage;
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

  private final ControllerHelper controllerHelper;
  private final EconomyService economyService;
  private final LocationMapper locationMapper;
  private final DiagoniaLogger logger;
  private final PlayerShopController playerShopController;
  private final PlayerShopMessage playerShopMessage;
  private final PlayerShopService playerShopService;
  private final PluginConfig pluginConfig;
  private final Server server;

  @Inject
  public PlayerShopListControllerImpl(
      @NotNull ControllerHelper controllerHelper,
      @NotNull EconomyService economyService,
      @NotNull LocationMapper locationMapper,
      @NotNull DiagoniaLogger logger,
      @NotNull PlayerShopController playerShopController,
      @NotNull PlayerShopMessage playerShopMessage,
      @NotNull PlayerShopService playerShopService,
      @NotNull PluginConfig pluginConfig,
      @NotNull Server server) {
    this.controllerHelper = controllerHelper;
    this.economyService = economyService;
    this.logger = logger;
    this.locationMapper = locationMapper;
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
      controllerHelper.sendSystemMessage(playerToTp, playerShopMessage.noTeleportPointDefined());
      return;
    }

    playerToTp.teleport(tpLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);

    OfflinePlayer psOwner = server.getOfflinePlayer(playerShopDestination.getOwnerUuid());
    String psOwnerName = controllerHelper.getOfflinePlayerName(psOwner);
    playerToTp.sendMessage(playerShopMessage.successTeleport(psOwnerName));

    logger.debug(
        "Teleportation of a player to a playershop: playerToTpUuid={}, playerShopId={}",
        playerToTp.getUniqueId(),
        playerShopDestination.getId());
  }

  @Override
  public void buyPlayerShop(@NotNull Player player) {
    // TODO: 2PC with JTA
    logger.debug("Start buying a playershop for player {}", player.getName());

    double playerShopPrice = pluginConfig.getPlayerShopConfig().getBuyCost();

    if (!economyService.isAffordable(player, playerShopPrice)) {
      controllerHelper.sendSystemMessage(player, playerShopMessage.insufficientFunds());
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
      controllerHelper.sendSystemMessage(player, playerShopMessage.buySuccess(economyResponse));
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
      controllerHelper.sendSystemMessage(player, playerShopMessage.transactionFailed());
    }
  }
}
