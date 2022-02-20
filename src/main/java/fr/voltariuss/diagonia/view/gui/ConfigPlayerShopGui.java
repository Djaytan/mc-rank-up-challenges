package fr.voltariuss.diagonia.view.gui;

import dev.triumphteam.gui.guis.Gui;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.view.item.ActivationPlayerShopItem;
import fr.voltariuss.diagonia.view.item.DefineTpPlayerShopItem;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ConfigPlayerShopGui {

  private final ActivationPlayerShopItem activationPlayerShopItem;
  private final DefineTpPlayerShopItem defineTpPlayerShopItem;
  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;

  @Inject
  public ConfigPlayerShopGui(
      @NotNull ActivationPlayerShopItem activationPlayerShopItem,
      @NotNull DefineTpPlayerShopItem defineTpPlayerShopItem,
      @NotNull MiniMessage miniMessage,
      @NotNull ResourceBundle resourceBundle) {
    this.activationPlayerShopItem = activationPlayerShopItem;
    this.defineTpPlayerShopItem = defineTpPlayerShopItem;
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
  }

  public void open(@NotNull Player whoOpen, @NotNull PlayerShop playerShop) {
    Gui gui =
        Gui.gui()
            .rows(3)
            .title(
                miniMessage.deserialize(
                    resourceBundle.getString("diagonia.playershop.config.gui.title")))
            .create();

    // TODO

    gui.setItem(2, 5, activationPlayerShopItem.createItem(playerShop));
    gui.setItem(2, 7, defineTpPlayerShopItem.createItem(playerShop));

    gui.setDefaultClickAction(event -> event.setCancelled(true));
    gui.open(whoOpen);
  }
}
