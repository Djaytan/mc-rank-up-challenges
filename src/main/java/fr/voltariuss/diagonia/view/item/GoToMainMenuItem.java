package fr.voltariuss.diagonia.view.item;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.MainController;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class GoToMainMenuItem {

  private static final Material GO_TO_MAIN_MENU_MATERIAL = Material.ARROW;

  private final MainController mainController;
  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;

  @Inject
  public GoToMainMenuItem(
      @NotNull MainController mainController,
      @NotNull MiniMessage miniMessage,
      @NotNull ResourceBundle resourceBundle) {
    this.mainController = mainController;
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem() {
    return ItemBuilder.from(GO_TO_MAIN_MENU_MATERIAL)
        .name(
            miniMessage
                .deserialize(resourceBundle.getString("diagonia.gui.go_to_main_menu"))
                .decoration(TextDecoration.ITALIC, false))
        .asGuiItem(onClick());
  }

  public @NotNull GuiAction<InventoryClickEvent> onClick() {
    return event -> {
      Player player = (Player) event.getWhoClicked();
      mainController.openMainMenu(player);
    };
  }
}
