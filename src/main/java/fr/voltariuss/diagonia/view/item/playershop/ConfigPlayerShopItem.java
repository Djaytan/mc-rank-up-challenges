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
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ConfigPlayerShopItem {

  private static final Material MANAGE_ITEM_MATERIAL = Material.EMERALD;

  private final MiniMessage miniMessage;
  private final PlayerShopController playerShopController;
  private final ResourceBundle resourceBundle;

  @Inject
  public ConfigPlayerShopItem(
      @NotNull MiniMessage miniMessage,
      @NotNull PlayerShopController playerShopController,
      @NotNull ResourceBundle resourceBundle) {
    this.miniMessage = miniMessage;
    this.playerShopController = playerShopController;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem(@NotNull PlayerShop playerShop) {
    return ItemBuilder.from(MANAGE_ITEM_MATERIAL)
        .name(
            miniMessage
                .deserialize(resourceBundle.getString("diagonia.playershop.config.name"))
                .decoration(TextDecoration.ITALIC, false))
        .lore(
            Collections.singletonList(
                miniMessage
                    .deserialize(resourceBundle.getString("diagonia.playershop.config.description"))
                    .decoration(TextDecoration.ITALIC, false)))
        .asGuiItem(onClick(playerShop));
  }

  public @NotNull GuiAction<InventoryClickEvent> onClick(@NotNull PlayerShop playerShop) {
    return event ->
        playerShopController.openConfigPlayerShop((Player) event.getWhoClicked(), playerShop);
  }
}
