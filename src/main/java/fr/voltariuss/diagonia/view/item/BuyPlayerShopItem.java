package fr.voltariuss.diagonia.view.item;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.PluginConfig;
import fr.voltariuss.diagonia.controller.PlayerShopController;
import fr.voltariuss.diagonia.view.ItemUtils;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class BuyPlayerShopItem {

  private static final Material BUY_ITEM_MATERIAL = Material.EMERALD;

  private final Economy economy;
  private final ItemUtils itemUtils;
  private final Logger logger;
  private final PlayerShopController playerShopController;
  private final PluginConfig pluginConfig;
  private final ResourceBundle resourceBundle;

  @Inject
  public BuyPlayerShopItem(
      @NotNull Economy economy,
      @NotNull ItemUtils itemUtils,
      @NotNull Logger logger,
      @NotNull PlayerShopController playerShopController,
      @NotNull PluginConfig pluginConfig,
      @NotNull ResourceBundle resourceBundle) {
    this.economy = economy;
    this.itemUtils = itemUtils;
    this.logger = logger;
    this.playerShopController = playerShopController;
    this.pluginConfig = pluginConfig;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem() {
    String name = resourceBundle.getString("diagonia.playershop.buy.name");
    String description =
        MessageFormat.format(
            resourceBundle.getString("diagonia.playershop.buy.description"),
            economy.format(pluginConfig.getPlayerShopConfig().getBuyCost()),
            economy.currencyNamePlural());
    List<Component> lore = itemUtils.asLore(description);
    return ItemBuilder.from(BUY_ITEM_MATERIAL)
        .name(Component.text(name))
        .lore(lore)
        .asGuiItem(onClick());
  }

  private @NotNull GuiAction<InventoryClickEvent> onClick() {
    return event -> {
      Player player = (Player) event.getWhoClicked();
      double balance = economy.getBalance(player, player.getWorld().getName());
      double buyCost = pluginConfig.getPlayerShopConfig().getBuyCost();

      if (balance < buyCost) {
        player.sendMessage(
            Component.text(resourceBundle.getString("diagonia.playershop.buy.insufficient_funds")));
      } else {
        // TODO: fix breaking of MVC rules by managing economy in controllers
        EconomyResponse economyResponse =
            economy.withdrawPlayer(player, player.getWorld().getName(), buyCost);
        if (economyResponse.transactionSuccess()) {
          playerShopController.buyPlayerShop(player);
        } else {
          logger.error(
              "Failed to withdraw {} money from the player's balance {}: {} (ResponseType={})",
              buyCost,
              player.getName(),
              economyResponse.errorMessage,
              economyResponse.type);
          player.sendMessage(
              Component.text(
                  resourceBundle.getString("diagonia.playershop.buy.transaction_failed")));
        }
      }
    };
  }
}
