package fr.voltariuss.diagonia.view.item;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.model.LocationMapper;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.WordUtils;
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

  private final Server server;
  private final LocationMapper locationMapper;

  @Inject
  public PlayerShopItem(@NotNull Server server, @NotNull LocationMapper locationMapper) {
    this.server = server;
    this.locationMapper = locationMapper;
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
      Component descriptionComponent = getDescriptionComponent(playerShop.getDescription());
      List<Component> lore = new ArrayList<>();
      lore.add(descriptionComponent);
      Location tpLocation = locationMapper.fromDto(playerShop.getTpLocation());
      return ItemBuilder.skull()
          .owner(ownerPlayer)
          .name(playerShopName)
          .lore(lore)
          .asGuiItem(getClickEvent(tpLocation));
    }
  }

  public @NotNull Component getNameComponent(@NotNull String ownerName) {
    return Component.text(String.format("Shop de %s", ownerName));
  }

  public @NotNull Component getDescriptionComponent(@Nullable String playerShopDescription) {
    Component descriptionComponent = Component.empty();
    if (playerShopDescription != null) {
      String splitDescription = WordUtils.wrap(playerShopDescription, 30);
      descriptionComponent = Component.text(splitDescription);
    }
    return descriptionComponent;
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
