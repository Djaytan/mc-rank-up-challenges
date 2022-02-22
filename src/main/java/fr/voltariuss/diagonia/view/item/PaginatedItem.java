package fr.voltariuss.diagonia.view.item;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PaginatedItem {

  private static final Material PAGE_ITEM = Material.ARROW;

  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;

  @Inject
  public PaginatedItem(@NotNull MiniMessage miniMessage, @NotNull ResourceBundle resourceBundle) {
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createPreviousPageItem(@NotNull PaginatedGui guiHolder) {
    return ItemBuilder.from(PAGE_ITEM)
        .name(
            miniMessage
                .deserialize(resourceBundle.getString("diagonia.page.previous"))
                .decoration(TextDecoration.ITALIC, false))
        .asGuiItem(inventoryClickEvent -> guiHolder.previous());
  }

  public @NotNull GuiItem createNextPageItem(@NotNull PaginatedGui guiHolder) {
    return ItemBuilder.from(PAGE_ITEM)
        .name(
            miniMessage
                .deserialize(resourceBundle.getString("diagonia.page.next"))
                .decoration(TextDecoration.ITALIC, false))
        .asGuiItem(inventoryClickEvent -> guiHolder.previous());
  }
}
