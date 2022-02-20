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
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class PlayerShopItem {

  private final LocationMapper locationMapper;
  private final ResourceBundle resourceBundle;
  private final Server server;

  @Inject
  public PlayerShopItem(
      @NotNull LocationMapper locationMapper,
      @NotNull ResourceBundle resourceBundle,
      @NotNull Server server) {
    this.locationMapper = locationMapper;
    this.resourceBundle = resourceBundle;
    this.server = server;
  }

  public @NotNull GuiItem createItem(@NotNull PlayerShop playerShop) {
    OfflinePlayer ownerPlayer = server.getOfflinePlayer(playerShop.getOwnerUuid());
    String ownerName = Objects.requireNonNull(ownerPlayer.getName());

    if (playerShop.hasItemIcon()) {
      ItemBuilder.from(playerShop.getItemIcon());
      return ItemBuilder.from(Material.AIR).asGuiItem();
      // TODO
    } else {
      Component playerShopName = getNameComponent(ownerName);
      List<Component> descLore =
          Collections.singletonList(Component.text(playerShop.getDescription()));
      Location tpLocation = locationMapper.fromDto(playerShop.getTpLocation());
      return ItemBuilder.skull()
          .owner(ownerPlayer)
          .name(playerShopName)
          .lore(descLore)
          .asGuiItem(getClickEvent(tpLocation));
    }
  }

  public @NotNull Component getNameComponent(@NotNull String ownerName) {
    return MiniMessage.miniMessage()
        .deserialize(
            String.format(resourceBundle.getString("diagonia.playershop.consult.name"), ownerName));
  }

  public @NotNull GuiAction<InventoryClickEvent> getClickEvent(@Nullable Location tpLocation) {
    return event -> {
      if (tpLocation != null) {
        Player player = (Player) event.getWhoClicked();
        player.teleport(tpLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
      }
    };
  }
}
