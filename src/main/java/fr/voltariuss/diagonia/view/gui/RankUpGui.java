package fr.voltariuss.diagonia.view.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import fr.voltariuss.diagonia.model.config.RankConfig;
import fr.voltariuss.diagonia.view.item.PaginatedItem;
import fr.voltariuss.diagonia.view.item.rankup.RankChallengeItem;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class RankUpGui {

  private static final Material DECORATION_MATERIAL = Material.GRAY_STAINED_GLASS_PANE;

  private final Logger logger;
  private final MiniMessage miniMessage;
  private final PaginatedItem paginatedItem;
  private final RankChallengeItem rankChallengeItem;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankUpGui(
      @NotNull Logger logger,
      @NotNull MiniMessage miniMessage,
      @NotNull PaginatedItem paginatedItem,
      @NotNull RankChallengeItem rankChallengeItem,
      @NotNull ResourceBundle resourceBundle) {
    this.logger = logger;
    this.miniMessage = miniMessage;
    this.paginatedItem = paginatedItem;
    this.rankChallengeItem = rankChallengeItem;
    this.resourceBundle = resourceBundle;
  }

  public void open(@NotNull Player whoOpen, @NotNull RankConfig.RankInfo rankInfo) {
    if (rankInfo.getRankUpChallenges() != null) {
      PaginatedGui gui =
          Gui.paginated()
              .pageSize(36)
              .rows(6)
              .title(
                  miniMessage.deserialize(
                      String.format(
                          resourceBundle.getString("diagonia.rankup.rankup.challenge.title"),
                          rankInfo.getName())))
              .create();

      GuiItem decorationItem =
          ItemBuilder.from(DECORATION_MATERIAL).name(Component.empty()).asGuiItem();

      for (int i = 1; i <= 9; i++) {
        gui.setItem(5, i, decorationItem);
      }

      rankInfo
          .getRankUpChallenges()
          .forEach(rankChallenge -> gui.addItem(rankChallengeItem.createItem(rankChallenge, 0)));

      gui.setItem(5, 3, paginatedItem.createPreviousPageItem(gui));
      gui.setItem(5, 7, paginatedItem.createNextPageItem(gui));

      gui.setDefaultClickAction(event -> event.setCancelled(true));

      gui.open(whoOpen);
    } else {
      logger.error("No challenge is associated with the rank {}", rankInfo.getId());
      // TODO: feedback player
    }
  }
}
