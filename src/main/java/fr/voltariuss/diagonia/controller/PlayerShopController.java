package fr.voltariuss.diagonia.controller;

import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.model.service.PlayerShopService;
import fr.voltariuss.diagonia.view.gui.PlayerShopGui;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class PlayerShopController {

  private final Logger logger;
  private final PlayerShopGui playerShopGui;
  private final PlayerShopService playerShopService;

  @Inject
  public PlayerShopController(
      @NotNull Logger logger,
      @NotNull PlayerShopGui playerShopGui,
      @NotNull PlayerShopService playerShopService) {
    this.logger = logger;
    this.playerShopGui = playerShopGui;
    this.playerShopService = playerShopService;
  }

  public void openPlayerShop(@NotNull Player player) {
    logger.info("Open playershop for player {}", player.getName());
    List<PlayerShop> playerShopList = playerShopService.findAll();
    playerShopGui.open(player, playerShopList);
  }

  public void buyPlayerShop(@NotNull Player player) {
    logger.info("Buy of a playershop for player {}", player.getName());
    PlayerShop ps = new PlayerShop(player.getUniqueId());
    playerShopService.persist(ps);
  }
}
