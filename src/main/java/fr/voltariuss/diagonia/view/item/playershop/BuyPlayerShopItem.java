package fr.voltariuss.diagonia.view.item.playershop;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.model.config.PluginConfig;
import fr.voltariuss.diagonia.controller.PlayerShopController;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
  private final Logger logger;
  private final MiniMessage miniMessage;
  private final PlayerShopController playerShopController;
  private final PluginConfig pluginConfig;
  private final ResourceBundle resourceBundle;

  @Inject
  public BuyPlayerShopItem(
      @NotNull Economy economy,
      @NotNull Logger logger,
      @NotNull MiniMessage miniMessage,
      @NotNull PlayerShopController playerShopController,
      @NotNull PluginConfig pluginConfig,
      @NotNull ResourceBundle resourceBundle) {
    this.economy = economy;
    this.logger = logger;
    this.miniMessage = miniMessage;
    this.playerShopController = playerShopController;
    this.pluginConfig = pluginConfig;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem() {
    String name = resourceBundle.getString("diagonia.playershop.buy.name");
    List<Component> lore = new ArrayList<>(2);
    lore.add(
        miniMessage
            .deserialize(
                String.format(
                    resourceBundle.getString("diagonia.playershop.buy.description.1"),
                    economy.format(pluginConfig.getPlayerShopConfig().getBuyCost())))
            .decoration(TextDecoration.ITALIC, false));
    lore.add(Component.empty());
    lore.add(
        miniMessage
            .deserialize(resourceBundle.getString("diagonia.playershop.buy.description.2"))
            .decoration(TextDecoration.ITALIC, false));
    return ItemBuilder.from(BUY_ITEM_MATERIAL)
        .name(MiniMessage.miniMessage().deserialize(name).decoration(TextDecoration.ITALIC, false))
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
            miniMessage.deserialize(resourceBundle.getString("diagonia.playershop.buy.insufficient_funds")));
      } else {
        // TODO: fix breaking of MVC rules by managing economy in controllers
        EconomyResponse economyResponse =
            economy.withdrawPlayer(player, player.getWorld().getName(), buyCost);
        if (economyResponse.transactionSuccess()) {
          playerShopController.buyPlayerShop(player);
          playerShopController.openPlayerShop(player);
        } else {
          logger.error(
              "Failed to withdraw {} money from the player's balance {}: {} (ResponseType={})",
              buyCost,
              player.getName(),
              economyResponse.errorMessage,
              economyResponse.type);
          player.sendMessage(
              miniMessage.deserialize(
                  resourceBundle.getString("diagonia.playershop.buy.transaction_failed")));
        }
      }
    };
  }
}
