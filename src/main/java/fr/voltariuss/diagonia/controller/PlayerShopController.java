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
import fr.voltariuss.diagonia.view.gui.ConfigPlayerShopGui;
import fr.voltariuss.diagonia.view.gui.MainPlayerShopGui;
import fr.voltariuss.diagonia.view.message.PlayerShopMessage;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
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
  private final EconomyService economyService;
  private final LocationMapper locationMapper;
  private final MasterController masterController;
  private final PlayerShopMessage playerShopMessage;
  private final PlayerShopService playerShopService;
  private final PluginConfig pluginConfig;
  private final Server server;

  private final Provider<ConfigPlayerShopGui> configPlayerShopGui;
  private final Provider<MainPlayerShopGui> mainPlayerShopGui;

  @Inject
  public PlayerShopController(
      @NotNull DiagoniaLogger logger,
      @NotNull EconomyService economyService,
      @NotNull LocationMapper locationMapper,
      @NotNull MasterController masterController,
      @NotNull PlayerShopMessage playerShopMessage,
      @NotNull PlayerShopService playerShopService,
      @NotNull PluginConfig pluginConfig,
      @NotNull Server server,
      @NotNull Provider<ConfigPlayerShopGui> configPlayerShopGui,
      @NotNull Provider<MainPlayerShopGui> mainPlayerShopGui) {
    this.economyService = economyService;
    this.locationMapper = locationMapper;
    this.logger = logger;
    this.masterController = masterController;
    this.playerShopMessage = playerShopMessage;
    this.playerShopService = playerShopService;
    this.pluginConfig = pluginConfig;
    this.server = server;
    this.configPlayerShopGui = configPlayerShopGui;
    this.mainPlayerShopGui = mainPlayerShopGui;
  }

  public void openPlayerShopListView(@NotNull Player whoOpen) {
    logger.info("Open playershop list view for player {}", whoOpen.getName());
    Optional<PlayerShop> playerShopOwned = playerShopService.findByUuid(whoOpen.getUniqueId());
    List<PlayerShop> playerShopList = playerShopService.findAll();
    mainPlayerShopGui.get().open(whoOpen, playerShopList, playerShopOwned.isPresent());
  }

  public void openConfigPlayerShopView(@NotNull Player whoOpen) {
    logger.info("Open config playershop view for player {}", whoOpen.getName());
    PlayerShop playerShop = playerShopService.findByUuid(whoOpen.getUniqueId()).orElseThrow();
    configPlayerShopGui.get().open(whoOpen, playerShop);
  }

  public void buyPlayerShop(@NotNull Player player) {
    logger.info("Buy of a playershop for player {}", player.getName());
    // TODO: economy treatment must be done here
    PlayerShop ps = new PlayerShop(player.getUniqueId());
    playerShopService.persist(ps);
    // TODO: and if something went wrong after economy transaction and before or during the shop
    // creation?
    double buyCost = pluginConfig.getPlayerShopConfig().getBuyCost();
    masterController.sendSystemMessage(player, playerShopMessage.buySuccess(buyCost));
  }

  public void defineTeleportPoint(
      @NotNull CommandSender sender, @NotNull PlayerShop playerShop, @NotNull Location location) {

    LocationDto locationDto = locationMapper.toDto(location);

    playerShop.setTpLocation(locationDto);
    playerShopService.update(playerShop); // TODO: error management?

    logger.info(
        "Updated teleport point for playershop: ownerUuid={}, playerShop={}, locationDto={}",
        playerShop.getOwnerUuid(),
        playerShop.getId(),
        locationDto);

    masterController.sendSystemMessage(sender, playerShopMessage.teleportPointDefined(locationDto));
  }

  public void togglePlayerShop(@NotNull CommandSender sender, @NotNull PlayerShop playerShop) {
    // TODO: move business logic to model part
    @Nullable OfflinePlayer owner = server.getOfflinePlayer(playerShop.getOwnerUuid());
    logger.info("Toggle playershop of {}", owner.getName());
    if (playerShop.isActive() || playerShop.getTpLocation() != null) {
      playerShop.setActive(!playerShop.isActive());
      playerShopService.update(playerShop);
      masterController.sendSystemMessage(
          sender, playerShopMessage.toggleShop(playerShop.isActive()));
      return;
    }
    masterController.sendSystemMessage(
        sender, playerShopMessage.shopActivationRequireTeleportPointFirst());
  }

  public void onTogglePlayerShopActivation(@NotNull Player player, @NotNull PlayerShop playerShop) {
    togglePlayerShop(player, playerShop);
    openConfigPlayerShopView(player);
  }

  public void onBuyPlayerShop(@NotNull Player player) {
    double balance = economyService.getBalance(player);
    double buyCost = pluginConfig.getPlayerShopConfig().getBuyCost();

    // TODO: move business logic to model
    // TODO: move some logic into PlayerShopController#buyPlayerShop method

    if (balance < buyCost) {
      masterController.sendSystemMessage(player, playerShopMessage.insufficientFunds());
      return;
    }

    // TODO: fix breaking of MVC rules by managing economy in controllers
    try {
      EconomyResponse economyResponse = economyService.withdraw(player, buyCost);
      // TODO: use EconomyResponse for sending feedback to player
      buyPlayerShop(player);
      openPlayerShopListView(player);
    } catch (EconomyException e) {
      logger.error(
          "Failed to withdraw {} money from the player's balance {}: {}",
          buyCost,
          player.getName(),
          e.getMessage());
      masterController.sendSystemMessage(player, playerShopMessage.transactionFailed());
    }
  }

  public void onTeleportPlayerShop(
      @NotNull Player player, @NotNull PlayerShop playerShopDestination) {

    Location tpLocation = locationMapper.fromDto(playerShopDestination.getTpLocation());

    if (tpLocation == null) {
      logger.warn(
          "Failed to teleport a player to a playershop because no teleport point has been defined."
              + " This may be an error because activated playershops are supposed to have a"
              + " teleport point defined: playerUuid={}, playerShopId={}",
          player.getUniqueId(),
          playerShopDestination.getId());
      masterController.sendSystemMessage(player, playerShopMessage.noTeleportPointDefined());
      return;
    }

    player.teleport(tpLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);

    logger.info(
        "Teleportation of a player to a playershop: playerUuid={}, playerShopId={}",
        player.getUniqueId(),
        playerShopDestination.getId());
  }

  public void onDefiningPlayerShopTeleportPoint(
      @NotNull Player player, @NotNull PlayerShop playerShop) {
    Location location = player.getLocation();
    defineTeleportPoint(player, playerShop, location);
  }
}
