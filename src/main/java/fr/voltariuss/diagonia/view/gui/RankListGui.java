package fr.voltariuss.diagonia.view.gui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.RankUpController;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.config.rank.RankConfig;
import fr.voltariuss.diagonia.view.item.GoToMainMenuItem;
import fr.voltariuss.diagonia.view.item.rankup.RankItem;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankListGui {

  private final GoToMainMenuItem goToMainMenuItem;
  private final MiniMessage miniMessage;
  private final RankConfig rankConfig;
  private final RankItem rankItem;
  private final RankUpController rankUpController;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankListGui(
      @NotNull GoToMainMenuItem goToMainMenuItem,
      @NotNull MiniMessage miniMessage,
      @NotNull RankConfig rankConfig,
      @NotNull RankItem rankItem,
      @NotNull RankUpController rankUpController,
      @NotNull ResourceBundle resourceBundle) {
    this.goToMainMenuItem = goToMainMenuItem;
    this.miniMessage = miniMessage;
    this.rankConfig = rankConfig;
    this.rankItem = rankItem;
    this.rankUpController = rankUpController;
    this.resourceBundle = resourceBundle;
  }

  public void open(@NotNull Player whoOpen) {
    int nbRows = (int) Math.ceil(rankConfig.getRanks().size() / 9.0D) + 2;

    Gui gui =
        Gui.gui()
            .title(
                miniMessage.deserialize(
                    resourceBundle.getString("diagonia.rankup.rank_list.title")))
            .rows(nbRows)
            .create();

    for (Rank rank : rankConfig.getRanks()) {
      // TODO: remove use of controller in view
      boolean isRankOwned = rankUpController.isRankOwned(whoOpen, rank.getId());
      boolean isCurrentRank = rankUpController.isCurrentRank(whoOpen, rank.getId());
      boolean isUnlockableRank = rankUpController.isUnlockableRank(whoOpen, rank.getId());

      GuiItem rankGuiItem = rankItem.createItem(rank, isRankOwned, isCurrentRank, isUnlockableRank);
      gui.addItem(rankGuiItem);
    }

    gui.setItem(nbRows, 1, goToMainMenuItem.createItem());

    gui.setDefaultClickAction(event -> event.setCancelled(true));

    // TODO: move open actions to controller view
    gui.open(whoOpen);
  }
}
