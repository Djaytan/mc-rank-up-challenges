package fr.voltariuss.diagonia.controller;

import fr.voltariuss.diagonia.model.config.RankConfig;
import fr.voltariuss.diagonia.view.gui.RankListGui;
import fr.voltariuss.diagonia.view.gui.RankUpGui;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import net.luckperms.api.LuckPerms;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.UUID;

@Singleton
public class RankUpController {

  private final Logger logger;
  private final LuckPerms luckPerms;

  private final Provider<RankListGui> rankListGuiProvider;
  private final Provider<RankUpGui> rankUpGuiProvider;

  @Inject
  public RankUpController(
      @NotNull Logger logger,
      @NotNull LuckPerms luckPerms,
      @NotNull Provider<RankListGui> rankListGuiProvider,
      @NotNull Provider<RankUpGui> rankUpGuiProvider) {
    this.logger = logger;
    this.luckPerms = luckPerms;
    this.rankListGuiProvider = rankListGuiProvider;
    this.rankUpGuiProvider = rankUpGuiProvider;
  }

  public void openRankListGui(@NotNull Player whoOpen) {
    logger.info("Open the list of ranks GUI for player {}", whoOpen.getName());
    rankListGuiProvider.get().open(whoOpen);
  }

  public void openRankUpGui(@NotNull Player whoOpen, @NotNull RankConfig.RankInfo rankInfo) {
    logger.info("Open the rank up GUI {} for player {}", rankInfo.getId(), whoOpen.getName());
    rankUpGuiProvider.get().open(whoOpen, rankInfo);
  }
}
