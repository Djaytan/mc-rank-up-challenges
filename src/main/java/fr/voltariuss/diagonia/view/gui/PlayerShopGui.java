package fr.voltariuss.diagonia.view.gui;

import dev.triumphteam.gui.guis.Gui;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.view.item.PlayerShopItem;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlayerShopGui {

  private final PlayerShopItem playerShopItemFactory;

  @Inject
  public PlayerShopGui(@NotNull PlayerShopItem playerShopItemFactory) {
    this.playerShopItemFactory = playerShopItemFactory;
  }

  public void open(@NotNull Player whoOpen, List<PlayerShop> playerShopList) {
    Gui gui = Gui.gui().title(Component.text("PlayerShop")).rows(6).create();

    playerShopList.forEach(playerShopItemFactory::createItem);

    gui.setDefaultClickAction(event -> event.setCancelled(true));

    gui.open(whoOpen);
  }
}
