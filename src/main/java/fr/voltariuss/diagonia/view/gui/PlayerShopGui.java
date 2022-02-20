package fr.voltariuss.diagonia.view.gui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.view.item.BuyPlayerShopItem;
import fr.voltariuss.diagonia.view.item.ConsultPlayerShopItem;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlayerShopGui {

  private final BuyPlayerShopItem buyPlayerShopItem;
  private final ConsultPlayerShopItem consultPlayerShopItem;

  @Inject
  public PlayerShopGui(
      @NotNull BuyPlayerShopItem buyPlayerShopItem,
      @NotNull ConsultPlayerShopItem consultPlayerShopItem) {
    this.buyPlayerShopItem = buyPlayerShopItem;
    this.consultPlayerShopItem = consultPlayerShopItem;
  }

  public void open(@NotNull Player whoOpen, List<PlayerShop> playerShopList) {
    Gui gui = Gui.gui().title(Component.text("PlayerShop")).rows(6).create();

    gui.addItem(
        playerShopList.stream()
            .map(consultPlayerShopItem::createItem)
            .filter(Objects::nonNull)
            .toArray(GuiItem[]::new));

    gui.setItem(6, 5, buyPlayerShopItem.createItem());

    gui.setDefaultClickAction(event -> event.setCancelled(true));

    gui.open(whoOpen);
  }
}
