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
import fr.voltariuss.diagonia.view.EconomyFormatter;
import fr.voltariuss.diagonia.view.gui.ConfigPlayerShopGui;
import fr.voltariuss.diagonia.view.gui.MainPlayerShopGui;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class PlayerShopController {

  private final DiagoniaLogger logger;
  private final EconomyFormatter economyFormatter;
  private final EconomyService economyService;
  private final LocationMapper locationMapper;
  private final MiniMessage miniMessage;
  private final PlayerShopService playerShopService;
  private final PluginConfig pluginConfig;
  private final ResourceBundle resourceBundle;
  private final Server server;

  private final Provider<ConfigPlayerShopGui> configPlayerShopGui;
  private final Provider<MainPlayerShopGui> mainPlayerShopGui;

  @Inject
  public PlayerShopController(
      @NotNull DiagoniaLogger logger,
      @NotNull EconomyFormatter economyFormatter,
      @NotNull EconomyService economyService,
      @NotNull LocationMapper locationMapper,
      @NotNull MiniMessage miniMessage,
      @NotNull PlayerShopService playerShopService,
      @NotNull PluginConfig pluginConfig,
      @NotNull ResourceBundle resourceBundle,
      @NotNull Server server,
      @NotNull Provider<ConfigPlayerShopGui> configPlayerShopGui,
      @NotNull Provider<MainPlayerShopGui> mainPlayerShopGui) {
    this.economyFormatter = economyFormatter;
    this.economyService = economyService;
    this.locationMapper = locationMapper;
    this.logger = logger;
    this.miniMessage = miniMessage;
    this.playerShopService = playerShopService;
    this.pluginConfig = pluginConfig;
    this.resourceBundle = resourceBundle;
    this.server = server;
    this.configPlayerShopGui = configPlayerShopGui;
    this.mainPlayerShopGui = mainPlayerShopGui;
  }

  public void openPlayerShop(@NotNull Player whoOpen) {
    logger.info("Open playershop for player {}", whoOpen.getName());
    List<PlayerShop> playerShopList = playerShopService.findAll();
    mainPlayerShopGui.get().open(whoOpen, playerShopList);
  }

  public void openConfigPlayerShop(@NotNull Player whoOpen, @NotNull PlayerShop playerShop) {
    if (pluginConfig.isDebugMode()) {
      logger.debug("Open config playershop for player {} ({})", whoOpen.getName(), playerShop);
    } else {
      logger.info("Open config playershop gui for player {}", whoOpen.getName());
    }
    configPlayerShopGui.get().open(whoOpen, playerShop);
  }

  public void buyPlayerShop(@NotNull Player player) {
    logger.info("Buy of a playershop for player {}", player.getName());
    PlayerShop ps = new PlayerShop(player.getUniqueId());
    playerShopService.persist(ps);
    // TODO: and if something went wrong after economy transaction and before or during the shop
    // creation?
    player.sendMessage(
        miniMessage.deserialize(
            String.format(
                resourceBundle.getString("diagonia.playershop.buy.successfully_bought"),
                economyFormatter.format(pluginConfig.getPlayerShopConfig().getBuyCost()))));
  }

  public boolean hasPlayerShop(@NotNull Player player) {
    return playerShopService.findByUuid(player.getUniqueId()).isPresent();
  }

  public void defineTeleportPoint(
      @NotNull CommandSender sender, @NotNull PlayerShop playerShop, @NotNull Location location) {
    @Nullable OfflinePlayer owner = server.getOfflinePlayer(playerShop.getOwnerUuid());
    LocationDto locationDto = locationMapper.toDto(location);
    if (pluginConfig.isDebugMode()) {
      logger.debug(
          "Update teleport point for the playershop of {} (playershop: {}, new location: {})",
          playerShop,
          locationDto);
    } else {
      logger.info("Update teleport point for the playershop of {}", owner.getName());
    }
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
    // TODO: move business logic to model part
    @Nullable OfflinePlayer owner = server.getOfflinePlayer(playerShop.getOwnerUuid());
    if (pluginConfig.isDebugMode()) {
      logger.debug("Toggle playershop of {}: {}", owner, playerShop);
    } else {
      logger.info("Toggle playershop of {}", owner.getName());
    }
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

  public void onTogglePlayerShopActivation(@NotNull Player player, @NotNull PlayerShop playerShop) {
    togglePlayerShop(player, playerShop);
    openConfigPlayerShop(player, playerShop);
  }

  public void onBuyPlayerShop(@NotNull Player player) {
    double balance = economyService.getBalance(player);
    double buyCost = pluginConfig.getPlayerShopConfig().getBuyCost();

    if (balance < buyCost) {
      // TODO: stop using else part (harder to read)
      player.sendMessage(
          miniMessage.deserialize(
              resourceBundle.getString("diagonia.playershop.buy.insufficient_funds")));
    } else {
      // TODO: fix breaking of MVC rules by managing economy in controllers
      try {
        EconomyResponse economyResponse = economyService.withdraw(player, buyCost);
        // TODO: use EconomyResponse for sending feedback to player
        buyPlayerShop(player);
        openPlayerShop(player);
      } catch (EconomyException e) {
        logger.error(
            "Failed to withdraw {} money from the player's balance {}: {}",
            buyCost,
            player.getName(),
            e.getMessage());
        player.sendMessage(
            miniMessage.deserialize(
                resourceBundle.getString("diagonia.playershop.buy.transaction_failed")));
      }
    }
  }

  public void onTeleportPlayerShop(
      @NotNull Player player, @NotNull PlayerShop playerShopDestination) {
    Location tpLocation = locationMapper.fromDto(playerShopDestination.getTpLocation());
    if (tpLocation != null) {
      player.teleport(tpLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
    } else {
      player.sendMessage(
          miniMessage.deserialize(
              resourceBundle.getString("diagonia.playershop.teleport.no_tp_defined_error")));
    }
  }

  public void onDefiningPlayerShopTeleportPoint(
      @NotNull Player player, @NotNull PlayerShop playerShop) {
    Location location = player.getLocation();
    defineTeleportPoint(player, playerShop, location);
  }
}
