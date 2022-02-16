package fr.voltariuss.diagonia.controller;

import fr.voltariuss.diagonia.view.gui.PlayerShopGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayerShopController {

  private final Logger logger;
  private final PlayerShopGui playerShopGui;

  @Inject
  public PlayerShopController(@NotNull Logger logger, @NotNull PlayerShopGui playerShopGui) {
    this.logger = logger;
    this.playerShopGui = playerShopGui;
  }

  public void openPlayerShop(@NotNull Player player) {
    logger.info("Open playershop for player {}", player.getName());
    playerShopGui.open(player);
  }
}
