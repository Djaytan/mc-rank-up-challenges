package fr.voltariuss.diagonia.view.gui;

import dev.triumphteam.gui.guis.Gui;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;

import fr.voltariuss.diagonia.model.entity.PlayerShop;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ConfigPlayerShopGui {

  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;

  @Inject
  public ConfigPlayerShopGui(
      @NotNull MiniMessage miniMessage, @NotNull ResourceBundle resourceBundle) {
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

    gui.setDefaultClickAction(event -> event.setCancelled(true));
    gui.open(whoOpen);
  }
}
