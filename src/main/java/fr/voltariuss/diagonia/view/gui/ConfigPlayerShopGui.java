package fr.voltariuss.diagonia.view.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import fr.voltariuss.diagonia.controller.PlayerShopController;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.view.item.playershop.ActivationPlayerShopItem;
import fr.voltariuss.diagonia.view.item.playershop.DefineTpPlayerShopItem;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ConfigPlayerShopGui {

  private static final Material PREVIOUS_GUI_MATERIAL = Material.ARROW;

  private final ActivationPlayerShopItem activationPlayerShopItem;
  private final DefineTpPlayerShopItem defineTpPlayerShopItem;
  private final MiniMessage miniMessage;
  private final PlayerShopController playerShopController;
  private final ResourceBundle resourceBundle;

  @Inject
  public ConfigPlayerShopGui(
      @NotNull ActivationPlayerShopItem activationPlayerShopItem,
      @NotNull DefineTpPlayerShopItem defineTpPlayerShopItem,
      @NotNull MiniMessage miniMessage,
      @NotNull PlayerShopController playerShopController,
      @NotNull ResourceBundle resourceBundle) {
    this.activationPlayerShopItem = activationPlayerShopItem;
    this.defineTpPlayerShopItem = defineTpPlayerShopItem;
    this.playerShopController = playerShopController;
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

    gui.setItem(2, 4, activationPlayerShopItem.createItem(playerShop));
    gui.setItem(2, 6, defineTpPlayerShopItem.createItem(playerShop));

    gui.setItem(
        3,
        1,
        ItemBuilder.from(PREVIOUS_GUI_MATERIAL)
            .name(
                miniMessage
                    .deserialize(resourceBundle.getString("diagonia.gui.go_to_previous"))
                    .decoration(TextDecoration.ITALIC, false))
            .asGuiItem(
                event -> playerShopController.openPlayerShop((Player) event.getWhoClicked())));

    gui.setDefaultClickAction(event -> event.setCancelled(true));
    gui.open(whoOpen);
  }
}
