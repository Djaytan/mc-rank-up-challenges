package fr.voltariuss.diagonia.view.item;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.model.LocationMapper;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Singleton
public class ConsultPlayerShopItem {

  private final LocationMapper locationMapper;
  private final Logger logger;
  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;
  private final Server server;

  @Inject
  public ConsultPlayerShopItem(
      @NotNull LocationMapper locationMapper,
      @NotNull Logger logger,
      @NotNull MiniMessage miniMessage,
      @NotNull ResourceBundle resourceBundle,
      @NotNull Server server) {
    this.locationMapper = locationMapper;
    this.logger = logger;
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
    this.server = server;
  }

  public @Nullable GuiItem createItem(@NotNull PlayerShop playerShop) {
    GuiItem item = null;
    OfflinePlayer ownerPlayer = server.getOfflinePlayer(playerShop.getOwnerUuid());
    String ownerName = ownerPlayer.getName();

    if (playerShop.isActive()) {
      if (ownerName != null) {
        Component psName =
          miniMessage.deserialize(
            String.format(
              resourceBundle.getString("diagonia.playershop.consult.name"), ownerName));
        List<Component> psDescLore =
          Collections.singletonList(Component.text(playerShop.getDescription()));

        if (playerShop.hasItemIcon()) {
          return ItemBuilder.from(playerShop.getItemIcon()).name(psName).lore(psDescLore).asGuiItem();
        } else {
          item =
            ItemBuilder.skull()
              .owner(ownerPlayer)
              .name(psName)
              .lore(psDescLore)
              .asGuiItem(getClickEvent(playerShop));
        }
      } else {
        logger.error(
          "The UUID {} isn't associated to any existing user on the server.",
          playerShop.getOwnerUuid());
      }
    }
    return item;
  }

  public @NotNull GuiAction<InventoryClickEvent> getClickEvent(@NotNull PlayerShop playerShop) {
    return event -> {
      Player player = (Player) event.getWhoClicked();
      Location tpLocation = locationMapper.fromDto(playerShop.getTpLocation());
      if (tpLocation != null) {
        player.teleport(tpLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
      } else {
        player.sendMessage(Component.text(resourceBundle.getString("diagonia.playershop.teleport.no_tp_defined_error")));
      }
    };
  }
}
