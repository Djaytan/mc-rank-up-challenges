package fr.voltariuss.diagonia.controller;

import fr.voltariuss.diagonia.PluginConfig;
import fr.voltariuss.diagonia.model.LocationMapper;
import fr.voltariuss.diagonia.model.dto.LocationDto;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.model.service.PlayerShopService;
import fr.voltariuss.diagonia.view.gui.ConfigPlayerShopGui;
import fr.voltariuss.diagonia.view.gui.MainPlayerShopGui;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class PlayerShopController {

  private final Economy economy;
  private final LocationMapper locationMapper;
  private final Logger logger;
  private final MiniMessage miniMessage;
  private final PlayerShopService playerShopService;
  private final PluginConfig pluginConfig;
  private final ResourceBundle resourceBundle;

  private final Provider<ConfigPlayerShopGui> configPlayerShopGui;
  private final Provider<MainPlayerShopGui> mainPlayerShopGui;

  @Inject
  public PlayerShopController(
      @NotNull Economy economy,
      @NotNull LocationMapper locationMapper,
      @NotNull Logger logger,
      @NotNull MiniMessage miniMessage,
      @NotNull PlayerShopService playerShopService,
      @NotNull PluginConfig pluginConfig,
      @NotNull ResourceBundle resourceBundle,
      @NotNull Provider<ConfigPlayerShopGui> configPlayerShopGui,
      @NotNull Provider<MainPlayerShopGui> mainPlayerShopGui) {
    this.economy = economy;
    this.locationMapper = locationMapper;
    this.logger = logger;
    this.miniMessage = miniMessage;
    this.playerShopService = playerShopService;
    this.pluginConfig = pluginConfig;
    this.resourceBundle = resourceBundle;
    this.configPlayerShopGui = configPlayerShopGui;
    this.mainPlayerShopGui = mainPlayerShopGui;
  }

  public void openPlayerShop(@NotNull Player whoOpen) {
    logger.info("Open playershop for player {}", whoOpen.getName());
    List<PlayerShop> playerShopList = playerShopService.findAll();
    mainPlayerShopGui.get().open(whoOpen, playerShopList);
  }

  public void openConfigPlayerShop(@NotNull Player whoOpen, @NotNull PlayerShop playerShop) {
    logger.info(
        "Open ConfigPlayerShop of playershop for player {}: {}", whoOpen.getName(), playerShop);
    configPlayerShopGui.get().open(whoOpen, playerShop);
  }

  public void buyPlayerShop(@NotNull Player player) {
    logger.info("Buy of a playershop for player {}", player.getName());
    PlayerShop ps = new PlayerShop(player.getUniqueId());
    playerShopService.persist(ps);
    player.sendMessage(
        Component.text(
            String.format(
                resourceBundle.getString("diagonia.playershop.buy.successfully_bought"),
                economy.format(pluginConfig.getPlayerShopConfig().getBuyCost()))));
  }

  public boolean hasPlayerShop(@NotNull Player player) {
    return playerShopService.findByUuid(player.getUniqueId()).isPresent();
  }

  public void defineTeleportPoint(
      @NotNull CommandSender sender, @NotNull PlayerShop playerShop, @NotNull Location location) {
    LocationDto locationDto = locationMapper.toDto(location);
    logger.info(
        "Update teleport point for the following playershop with the location {}: {}",
        locationDto,
        playerShop);
    playerShop.setTpLocation(locationDto);
    playerShopService.update(playerShop);
    sender.sendMessage(
        miniMessage.deserialize(
            String.format(
                resourceBundle.getString("diagonia.playershop.config.define_teleport.defined"),
                locationDto)));
  }

  public Optional<PlayerShop> getFromUuid(@NotNull UUID uuid) {
    return playerShopService.findByUuid(uuid);
  }

  public void togglePlayerShop(@NotNull CommandSender sender, @NotNull PlayerShop playerShop) {
    logger.info("Toggle playershop: {}", playerShop);
    if (playerShop.isActive() || playerShop.getTpLocation() != null) {
      playerShop.setActive(!playerShop.isActive());
      playerShopService.update(playerShop);
      sender.sendMessage(
          miniMessage.deserialize(
              String.format(
                  resourceBundle.getString("diagonia.playershop.config.activation.toggled"),
                  playerShop.isActive()
                      ? resourceBundle.getString(
                          "diagonia.playershop.config.activation.toggled.enabled")
                      : resourceBundle.getString(
                          "diagonia.playershop.config.activation.toggled.disabled"))));
    } else {
      sender.sendMessage(
          miniMessage.deserialize(
              resourceBundle.getString(
                  "diagonia.playershop.config.activation.enabling.teleport_point_definition_required")));
    }
  }
}
