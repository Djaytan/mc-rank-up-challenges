package fr.voltariuss.diagonia.view.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerShopGui implements InventoryGui {

  @Override
  public void open(@NotNull Player player) {
    Gui gui = Gui.gui().title(Component.text("PlayerShop")).rows(6).create();
    gui.addItem(ItemBuilder.firework().asGuiItem());

    gui.setDefaultClickAction(event -> event.setCancelled(true));
  }
}
