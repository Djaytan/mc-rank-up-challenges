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
import fr.voltariuss.diagonia.model.LocationMapper;
import fr.voltariuss.diagonia.model.config.PluginConfig;
import fr.voltariuss.diagonia.model.dto.LocationDto;
import fr.voltariuss.diagonia.model.dto.response.EconomyResponse;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.model.service.EconomyException;
import fr.voltariuss.diagonia.model.service.EconomyService;
import fr.voltariuss.diagonia.model.service.PlayerShopService;
import fr.voltariuss.diagonia.view.gui.PlayerShopConfigGui;
import fr.voltariuss.diagonia.view.gui.PlayerShopListGui;
import fr.voltariuss.diagonia.view.message.PlayerShopMessage;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

// TODO: one controller per GUI
@Singleton
public class PlayerShopController {

  private final DiagoniaLogger logger;
  private final EconomyService economyService;
  private final LocationMapper locationMapper;
  private final ControllerHelper controllerHelper;
  private final PlayerShopMessage playerShopMessage;
  private final PlayerShopService playerShopService;
  private final PluginConfig pluginConfig;

  private final Provider<PlayerShopConfigGui> playerShopConfigGui;
  private final Provider<PlayerShopListGui> playerShopListGui;

  @Inject
  public PlayerShopController(
      @NotNull DiagoniaLogger logger,
      @NotNull EconomyService economyService,
      @NotNull LocationMapper locationMapper,
      @NotNull ControllerHelper controllerHelper,
      @NotNull PlayerShopMessage playerShopMessage,
      @NotNull PlayerShopService playerShopService,
      @NotNull PluginConfig pluginConfig,
      @NotNull Provider<PlayerShopConfigGui> playerShopConfigGui,
      @NotNull Provider<PlayerShopListGui> playerShopListGui) {
    this.economyService = economyService;
    this.locationMapper = locationMapper;
    this.logger = logger;
    this.controllerHelper = controllerHelper;
    this.playerShopMessage = playerShopMessage;
    this.playerShopService = playerShopService;
    this.pluginConfig = pluginConfig;
    this.playerShopConfigGui = playerShopConfigGui;
    this.playerShopListGui = playerShopListGui;
  }

  public void openPlayerShopListGui(@NotNull Player whoOpen) {
    logger.debug("Open PlayerShopList GUI for a player: playerName={}", whoOpen.getName());
    Optional<PlayerShop> playerShopOwned = playerShopService.findByUuid(whoOpen.getUniqueId());
    List<PlayerShop> playerShopList = playerShopService.findAll();
    playerShopListGui.get().open(whoOpen, playerShopList, playerShopOwned.isPresent());
  }

  public void openPlayerShopConfigGui(@NotNull Player whoOpen) {
    logger.debug("Open PlayerShopConfig GUI for player {}", whoOpen.getName());
    PlayerShop playerShop = playerShopService.findByUuid(whoOpen.getUniqueId()).orElseThrow();
    playerShopConfigGui.get().open(whoOpen, playerShop);
  }

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
      openPlayerShopListGui(player);
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

  public void defineTeleportPoint(
      @NotNull CommandSender sender,
      @NotNull PlayerShop playerShop,
      @NotNull Location newLocation) {
    logger.debug(
        "Start defining a teleport point: senderName={}, playerShopId={},"
            + " playerShopCurrentTpLocation={}, newLocation={}",
        sender.getName(),
        playerShop.getId(),
        playerShop.getTpLocationDto(),
        newLocation);

    LocationDto newLocationDto = locationMapper.toDto(newLocation);

    playerShop.setTpLocationDto(newLocationDto);
    playerShopService.update(playerShop);

    logger.debug(
        "Updated teleport point for playershop: playerShopOwnerUuid={}, playerShopId={},"
            + " playerShopNewTpLocation={}",
        playerShop.getOwnerUuid(),
        playerShop.getId(),
        playerShop.getTpLocationDto());

    controllerHelper.sendSystemMessage(
        sender, playerShopMessage.teleportPointDefined(newLocationDto));
  }

  public void togglePlayerShop(@NotNull Player sender, @NotNull PlayerShop playerShop) {
    logger.debug(
        "Start toggling playershop: senderName={}, playerShopId={}, playerShopIsActive={},"
            + " playerShopTpLocation={}",
        sender.getName(),
        playerShop.getId(),
        playerShop.isActive(),
        playerShop.getTpLocationDto());

    if (playerShop.getTpLocationDto() == null && playerShop.isActive()) {
      throw new IllegalStateException(
          "A playershop without teleport point defined mustn't be activated.");
    }

    if (playerShop.getTpLocationDto() == null) {
      logger.debug("Failed to toggle playershop: tp location must be defined.");

      controllerHelper.sendSystemMessage(
          sender, playerShopMessage.shopActivationRequireTeleportPointFirst());

      return;
    }

    playerShop.setActive(!playerShop.isActive());
    playerShopService.update(playerShop);

    logger.debug(
        "Toggled playershop: senderName={}, playerShopId={}, isNowActive={}",
        sender.getName(),
        playerShop.getId(),
        playerShop.isActive());

    controllerHelper.sendSystemMessage(sender, playerShopMessage.toggleShop(playerShop.isActive()));
    openPlayerShopConfigGui(sender);
  }

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

    logger.debug(
        "Teleportation of a player to a playershop: playerToTpUuid={}, playerShopId={}",
        playerToTp.getUniqueId(),
        playerShopDestination.getId());
  }
}
