package fr.voltariuss.diagonia;

import co.aikar.commands.PaperCommandManager;
import fr.voltariuss.diagonia.controller.PlayerShopCommand;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CommandRegister {

  private final PaperCommandManager paperCommandManager;
  private final PlayerShopCommand playerShopCommand;

  @Inject
  public CommandRegister(@NotNull PaperCommandManager paperCommandManager, @NotNull PlayerShopCommand playerShopCommand) {
    this.paperCommandManager = paperCommandManager;
    this.playerShopCommand = playerShopCommand;
  }

  public void registerCommands() {
    paperCommandManager.registerCommand(playerShopCommand);
  }
}
