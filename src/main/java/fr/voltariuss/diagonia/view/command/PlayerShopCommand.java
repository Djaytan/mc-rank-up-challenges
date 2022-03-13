package fr.voltariuss.diagonia.view.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import fr.voltariuss.diagonia.DiagoniaLogger;
import fr.voltariuss.diagonia.controller.PlayerShopController;
import javax.inject.Inject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("playershop|ps")
public class PlayerShopCommand extends BaseCommand {

  private final DiagoniaLogger logger;
  private final PlayerShopController playerShopController;

  @Inject
  public PlayerShopCommand(
      @NotNull DiagoniaLogger logger, @NotNull PlayerShopController playerShopController) {
    this.logger = logger;
    this.playerShopController = playerShopController;
  }

  @Default
  public void onOpen(@NotNull Player player) {
    logger.debug("/playershop command executed by {}", player.getName());
    playerShopController.openPlayerShop(player);
  }
}
