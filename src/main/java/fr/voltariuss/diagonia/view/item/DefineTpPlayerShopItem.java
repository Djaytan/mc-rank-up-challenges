package fr.voltariuss.diagonia.view.item;

import com.google.common.collect.Lists;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.PlayerShopController;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class DefineTpPlayerShopItem {

  private static final Material DEFINE_TP_ITEM_MATERIAL = Material.ENDER_PEARL;

  private final MiniMessage miniMessage;
  private final PlayerShopController playerShopController;
  private final ResourceBundle resourceBundle;

  @Inject
  public DefineTpPlayerShopItem(
      @NotNull MiniMessage miniMessage,
      @NotNull PlayerShopController playerShopController,
      @NotNull ResourceBundle resourceBundle) {
    this.miniMessage = miniMessage;
    this.playerShopController = playerShopController;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem(@NotNull PlayerShop playerShop) {
    return ItemBuilder.from(DEFINE_TP_ITEM_MATERIAL)
        .name(
            miniMessage
                .deserialize(
                    resourceBundle.getString("diagonia.playershop.config.define_teleport.name"))
                .decoration(TextDecoration.ITALIC, false))
        .lore(
            Lists.newArrayList(
                miniMessage
                    .deserialize(
                        resourceBundle.getString(
                            "diagonia.playershop.config.define_teleport.description.1"))
                    .decoration(TextDecoration.ITALIC, false),
                miniMessage
                    .deserialize(
                        resourceBundle.getString(
                            "diagonia.playershop.config.define_teleport.description.2"))
                    .decoration(TextDecoration.ITALIC, false)))
        .asGuiItem(onClick(playerShop));
  }

  public @NotNull GuiAction<InventoryClickEvent> onClick(@NotNull PlayerShop playerShop) {
    return event -> {
      Player player = (Player) event.getWhoClicked();
      Location location = player.getLocation();
      playerShopController.defineTeleportPoint(player, playerShop, location);
    };
  }
}
