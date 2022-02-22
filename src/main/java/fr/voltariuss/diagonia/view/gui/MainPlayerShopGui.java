package fr.voltariuss.diagonia.view.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import fr.voltariuss.diagonia.controller.PlayerShopController;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.view.item.BuyPlayerShopItem;
import fr.voltariuss.diagonia.view.item.ConfigPlayerShopItem;
import fr.voltariuss.diagonia.view.item.ConsultPlayerShopItem;
import fr.voltariuss.diagonia.view.item.PaginatedItem;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class MainPlayerShopGui {

  private static final int NB_COLUMNS_PER_LINE = 9;
  private static final int NB_ROW_PER_PAGE = 4;
  private static final Material DECORATION_MATERIAL = Material.GRAY_STAINED_GLASS_PANE;

  private final BuyPlayerShopItem buyPlayerShopItem;
  private final ConfigPlayerShopItem configPlayerShopItem;
  private final ConsultPlayerShopItem consultPlayerShopItem;
  private final PaginatedItem paginatedItem;
  private final PlayerShopController playerShopController;

  @Inject
  public MainPlayerShopGui(
      @NotNull BuyPlayerShopItem buyPlayerShopItem,
      @NotNull ConfigPlayerShopItem configPlayerShopItem,
      @NotNull ConsultPlayerShopItem consultPlayerShopItem,
      @NotNull PaginatedItem paginatedItem,
      @NotNull PlayerShopController playerShopController) {
    this.buyPlayerShopItem = buyPlayerShopItem;
    this.configPlayerShopItem = configPlayerShopItem;
    this.consultPlayerShopItem = consultPlayerShopItem;
    this.paginatedItem = paginatedItem;
    this.playerShopController = playerShopController;
  }

  public void open(@NotNull Player whoOpen, List<PlayerShop> playerShopList) {
    int pageSize = NB_ROW_PER_PAGE * NB_COLUMNS_PER_LINE;
    PaginatedGui gui =
        Gui.paginated().title(Component.text("PlayerShop")).rows(6).pageSize(pageSize).create();

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

    gui.setItem(
        6,
        5,
        playerShopController
            .getFromUuid(whoOpen.getUniqueId())
            .map(configPlayerShopItem::createItem)
            .orElseGet(buyPlayerShopItem::createItem));

    gui.setItem(5, 3, paginatedItem.createPreviousPageItem(gui));
    gui.setItem(5, 7, paginatedItem.createNextPageItem(gui));

    gui.setDefaultClickAction(event -> event.setCancelled(true));

    gui.open(whoOpen);
  }
}
