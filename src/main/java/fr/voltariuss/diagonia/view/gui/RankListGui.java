package fr.voltariuss.diagonia.view.gui;

import dev.triumphteam.gui.guis.Gui;
import fr.voltariuss.diagonia.model.config.RankConfig;
import fr.voltariuss.diagonia.view.item.rankup.RankItem;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class RankListGui {

  private final Logger logger;
  private final LuckPerms luckPerms;
  private final MiniMessage miniMessage;
  private final RankConfig rankConfig;
  private final RankItem rankItem;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankListGui(
      @NotNull Logger logger,
      @NotNull LuckPerms luckPerms,
      @NotNull MiniMessage miniMessage,
      @NotNull RankConfig rankConfig,
      @NotNull RankItem rankItem,
      @NotNull ResourceBundle resourceBundle) {
    this.logger = logger;
    this.luckPerms = luckPerms;
    this.miniMessage = miniMessage;
    this.rankConfig = rankConfig;
    this.rankItem = rankItem;
    this.resourceBundle = resourceBundle;
  }

  public void open(@NotNull Player whoOpen) {
    Gui gui =
        Gui.gui()
            .title(
                miniMessage.deserialize(
                    resourceBundle.getString("diagonia.rankup.rank_list.title")))
            .rows((int) Math.ceil(rankConfig.getRanks().size() / 9.0D))
            .create();

    String currentRankName =
        Objects.requireNonNull(luckPerms.getUserManager().getUser(whoOpen.getUniqueId()))
            .getPrimaryGroup();

    rankConfig
        .getRanks()
        .forEach(
            rankInfo ->
                gui.addItem(
                    rankItem.createItem(
                        whoOpen, rankInfo, rankInfo.getId().equals(currentRankName))));

    gui.setDefaultClickAction(event -> event.setCancelled(true));

    gui.open(whoOpen);
  }
}
