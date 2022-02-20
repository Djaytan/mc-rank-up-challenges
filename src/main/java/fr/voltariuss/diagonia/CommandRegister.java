package fr.voltariuss.diagonia;

import co.aikar.commands.PaperCommandManager;
import fr.voltariuss.diagonia.controller.PlayerShopCommand;
import fr.voltariuss.diagonia.controller.UuidCommand;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class CommandRegister {

  private final PaperCommandManager paperCommandManager;

  private final PlayerShopCommand playerShopCommand;
  private final UuidCommand uuidCommand;

  @Inject
  public CommandRegister(
      @NotNull PaperCommandManager paperCommandManager,
      @NotNull PlayerShopCommand playerShopCommand,
      @NotNull UuidCommand uuidCommand) {
    this.paperCommandManager = paperCommandManager;
    this.playerShopCommand = playerShopCommand;
    this.uuidCommand = uuidCommand;
  }

  public void registerCommands() {
    paperCommandManager.registerCommand(playerShopCommand);
    paperCommandManager.registerCommand(uuidCommand);
  }
}
