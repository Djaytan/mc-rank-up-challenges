package fr.voltariuss.diagonia.view.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import fr.voltariuss.diagonia.controller.RankUpController;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@CommandAlias("ranks")
@Singleton
public class RanksCommand extends BaseCommand {

  private final RankUpController rankUpController;

  @Inject
  public RanksCommand(@NotNull RankUpController rankUpController) {
    this.rankUpController = rankUpController;
  }

  @Default
  public void onExec(@NotNull Player player) {
    rankUpController.openRankListGui(player);
  }
}

