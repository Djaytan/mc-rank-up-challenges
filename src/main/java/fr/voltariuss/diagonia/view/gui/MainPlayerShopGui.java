package fr.voltariuss.diagonia.view.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import fr.voltariuss.diagonia.controller.PlayerShopController;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.view.item.PaginatedItem;
import fr.voltariuss.diagonia.view.item.playershop.BuyPlayerShopItem;
import fr.voltariuss.diagonia.view.item.playershop.ConfigPlayerShopItem;
import fr.voltariuss.diagonia.view.item.playershop.ConsultPlayerShopItem;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class MainPlayerShopGui {

  // TODO: make it configurable
  private static final int NB_COLUMNS_PER_LINE = 9;
  private static final int NB_ROW_PER_PAGE = 4;
  private static final Material DECORATION_MATERIAL = Material.GRAY_STAINED_GLASS_PANE;

  private final BuyPlayerShopItem buyPlayerShopItem;
  private final ConfigPlayerShopItem configPlayerShopItem;
  private final ConsultPlayerShopItem consultPlayerShopItem;
  private final PaginatedItem paginatedItem;
  private final PlayerShopController playerShopController;
  private final ResourceBundle resourceBundle;

  @Inject
  public MainPlayerShopGui(
      @NotNull BuyPlayerShopItem buyPlayerShopItem,
      @NotNull ConfigPlayerShopItem configPlayerShopItem,
      @NotNull ConsultPlayerShopItem consultPlayerShopItem,
      @NotNull PaginatedItem paginatedItem,
      @NotNull PlayerShopController playerShopController,
      @NotNull ResourceBundle resourceBundle) {
    this.buyPlayerShopItem = buyPlayerShopItem;
    this.configPlayerShopItem = configPlayerShopItem;
    this.consultPlayerShopItem = consultPlayerShopItem;
    this.paginatedItem = paginatedItem;
    this.playerShopController = playerShopController;
    this.resourceBundle = resourceBundle;
  }

  public void open(
      @NotNull Player whoOpen,
      @Nullable PlayerShop playerShopOwned,
      @NotNull List<PlayerShop> playerShopList) {
    int pageSize = NB_ROW_PER_PAGE * NB_COLUMNS_PER_LINE;
    PaginatedGui gui =
        Gui.paginated()
            .title(
                Component.text(
                    resourceBundle.getString(
                        "diagonia.playershop.consult.gui.title"))) // TODO: use MiniMessage here
            .rows(6)
            .pageSize(pageSize)
            .create();

    GuiItem decorationItem =
        ItemBuilder.from(DECORATION_MATERIAL).name(Component.empty()).asGuiItem();

    for (int i = 1; i <= NB_COLUMNS_PER_LINE; i++) {
      gui.setItem(5, i, decorationItem);
    }

    gui.addItem(
        playerShopList.stream()
            .map(consultPlayerShopItem::createItem)
            .filter(Objects::nonNull)
            .toArray(GuiItem[]::new));

    GuiItem configItem;
    if (playerShopOwned == null) {
      configItem = buyPlayerShopItem.createItem();
    } else {
      configItem = configPlayerShopItem.createItem(playerShopOwned);
    }
    gui.setItem(6, 5, configItem);

    gui.setItem(5, 3, paginatedItem.createPreviousPageItem(gui));
    gui.setItem(5, 7, paginatedItem.createNextPageItem(gui));

    gui.setDefaultClickAction(event -> event.setCancelled(true));

    // TODO: move open actions to controller view
    gui.open(whoOpen);
  }
}
