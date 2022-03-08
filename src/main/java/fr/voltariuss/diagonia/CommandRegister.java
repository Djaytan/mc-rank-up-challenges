package fr.voltariuss.diagonia;

import co.aikar.commands.PaperCommandManager;
import fr.voltariuss.diagonia.view.command.PlayerShopCommand;
import fr.voltariuss.diagonia.view.command.RanksCommand;
import fr.voltariuss.diagonia.view.command.UuidCommand;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class CommandRegister {

  private final PaperCommandManager paperCommandManager;

  private final PlayerShopCommand playerShopCommand;
  private final RanksCommand ranksCommand;
  private final UuidCommand uuidCommand;

  @Inject
  public CommandRegister(
      @NotNull PaperCommandManager paperCommandManager,
      @NotNull PlayerShopCommand playerShopCommand,
      @NotNull RanksCommand ranksCommand,
      @NotNull UuidCommand uuidCommand) {
    this.paperCommandManager = paperCommandManager;
    this.playerShopCommand = playerShopCommand;
    this.ranksCommand = ranksCommand;
    this.uuidCommand = uuidCommand;
  }

  public void registerCommands() {
    paperCommandManager.registerCommand(playerShopCommand);
    paperCommandManager.registerCommand(ranksCommand);
    paperCommandManager.registerCommand(uuidCommand);
  }
}
