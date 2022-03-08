package fr.voltariuss.diagonia.view.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import fr.voltariuss.diagonia.Debugger;
import javax.inject.Inject;

import fr.voltariuss.diagonia.controller.PlayerShopController;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@CommandAlias("playershop|ps")
public class PlayerShopCommand extends BaseCommand {

  private final Debugger debugger;
  private final Logger logger;
  private final PlayerShopController playerShopController;

  @Inject
  public PlayerShopCommand(
      @NotNull Debugger debugger,
      @NotNull Logger logger,
      @NotNull PlayerShopController playerShopController) {
    this.debugger = debugger;
    this.logger = logger;
    this.playerShopController = playerShopController;
  }

  @Default
  public void onOpen(@NotNull Player player) {
    debugger.debug("/playershop command executed by {}", player.getName());
    playerShopController.openPlayerShop(player);
  }
}
