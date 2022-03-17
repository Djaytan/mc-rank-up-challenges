package fr.voltariuss.diagonia.view.item.playershop;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.PlayerShopController;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.Collections;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ActivationPlayerShopItem {

  private final MiniMessage miniMessage;
  private final PlayerShopController playerShopController;
  private final ResourceBundle resourceBundle;

  @Inject
  public ActivationPlayerShopItem(
      @NotNull MiniMessage miniMessage,
      @NotNull PlayerShopController playerShopController,
      @NotNull ResourceBundle resourceBundle) {
    this.miniMessage = miniMessage;
    this.playerShopController = playerShopController;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem(@NotNull PlayerShop playerShop) {
    Component name =
        miniMessage
            .deserialize(
                playerShop.isActive()
                    ? resourceBundle.getString(
                        "diagonia.playershop.config.activation.disabling.name")
                    : resourceBundle.getString(
                        "diagonia.playershop.config.activation.enabling.name"))
            .decoration(TextDecoration.ITALIC, false);
    Component description =
        miniMessage
            .deserialize(
                playerShop.isActive()
                    ? resourceBundle.getString(
                        "diagonia.playershop.config.activation.disabling.description")
                    : resourceBundle.getString(
                        "diagonia.playershop.config.activation.enabling.description"))
            .decoration(TextDecoration.ITALIC, false);

    return ItemBuilder.from(playerShop.isActive() ? Material.LIME_DYE : Material.GRAY_DYE)
        .name(name)
        .lore(Collections.singletonList(description))
        .asGuiItem(onClick(playerShop));
  }

  public @NotNull GuiAction<InventoryClickEvent> onClick(@NotNull PlayerShop playerShop) {
    return event -> {
      Player player = (Player) event.getWhoClicked();
      // TODO: create a real event with Observer pattern or Bukkit API
      playerShopController.onTogglePlayerShopActivation(player, playerShop);
    };
  }
}
