package fr.voltariuss.diagonia.controller;

import fr.voltariuss.diagonia.PluginConfig;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.model.service.PlayerShopService;
import fr.voltariuss.diagonia.view.gui.PlayerShopGui;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class PlayerShopController {

  private final Economy economy;
  private final Logger logger;
  private final Provider<PlayerShopGui> playerShopGui;
  private final PlayerShopService playerShopService;
  private final PluginConfig pluginConfig;
  private final ResourceBundle resourceBundle;

  @Inject
  public PlayerShopController(
      @NotNull Economy economy,
      @NotNull Logger logger,
      @NotNull Provider<PlayerShopGui> playerShopGui,
      @NotNull PlayerShopService playerShopService,
      @NotNull PluginConfig pluginConfig,
      @NotNull ResourceBundle resourceBundle) {
    this.economy = economy;
    this.logger = logger;
    this.playerShopGui = playerShopGui;
    this.playerShopService = playerShopService;
    this.pluginConfig = pluginConfig;
    this.resourceBundle = resourceBundle;
  }

  public void openPlayerShop(@NotNull Player player) {
    logger.info("Open playershop for player {}", player.getName());
    List<PlayerShop> playerShopList = playerShopService.findAll();
    playerShopGui.get().open(player, playerShopList);
  }

  public void buyPlayerShop(@NotNull Player player) {
    logger.info("Buy of a playershop for player {}", player.getName());
    PlayerShop ps = new PlayerShop(player.getUniqueId());
    playerShopService.persist(ps);
    player.sendMessage(
        Component.text(
            String.format(
                resourceBundle.getString("diagonia.playershop.buy.successfully_bought"),
                economy.format(pluginConfig.getPlayerShopConfig().getBuyCost()),
                economy.currencyNamePlural())));
  }
}
