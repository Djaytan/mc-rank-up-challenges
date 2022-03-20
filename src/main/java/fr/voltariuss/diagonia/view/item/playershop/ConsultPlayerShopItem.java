package fr.voltariuss.diagonia.view.item.playershop;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.DiagoniaLogger;
import fr.voltariuss.diagonia.controller.PlayerShopController;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class ConsultPlayerShopItem {

  private final DiagoniaLogger logger;
  private final MiniMessage miniMessage;
  private final PlayerShopController playerShopController;
  private final ResourceBundle resourceBundle;
  private final Server server;

  @Inject
  public ConsultPlayerShopItem(
      @NotNull DiagoniaLogger logger,
      @NotNull MiniMessage miniMessage,
      @NotNull PlayerShopController playerShopController,
      @NotNull ResourceBundle resourceBundle,
      @NotNull Server server) {
    this.logger = logger;
    this.miniMessage = miniMessage;
    this.playerShopController = playerShopController;
    this.resourceBundle = resourceBundle;
    this.server = server;
  }

  // TODO: return a wrapper of GuiItem and an active state (better readability)
  public @Nullable GuiItem createItem(@NotNull PlayerShop playerShop) {
    // TODO: remove use of Server instance here
    OfflinePlayer ownerPlayer = server.getOfflinePlayer(playerShop.getOwnerUuid());
    String ownerName = ownerPlayer.getName();

    if (playerShop.isActive()) {
      if (ownerName != null) {
        Component psName =
            miniMessage
                .deserialize(
                    String.format(
                        resourceBundle.getString("diagonia.playershop.consult.name"), ownerName))
                .decoration(TextDecoration.ITALIC, false);
        List<Component> psDescLore =
            Collections.singletonList(
                (playerShop.getDescription() != null
                        ? Component.text(playerShop.getDescription())
                            .color(TextColor.fromCSSHexString("gray"))
                        : miniMessage.deserialize(
                            resourceBundle.getString(
                                "diagonia.playershop.consult.description.no_description_set")))
                    .decoration(TextDecoration.ITALIC, false));

        if (playerShop.hasItemIcon()) {
          return ItemBuilder.from(playerShop.getItemIcon())
              .name(psName)
              .lore(psDescLore)
              .asGuiItem();
        }
        return ItemBuilder.skull()
            .owner(ownerPlayer)
            .name(psName)
            .lore(psDescLore)
            .asGuiItem(getClickEvent(playerShop));
      }
      logger.error(
          "The UUID {} isn't associated to any existing user on the server.",
          playerShop.getOwnerUuid());
    }

    return null;
  }

  public @NotNull GuiAction<InventoryClickEvent> getClickEvent(@NotNull PlayerShop playerShop) {
    return event -> {
      Player player = (Player) event.getWhoClicked();
      playerShopController.teleportToPlayerShop(player, playerShop);
    };
  }
}
