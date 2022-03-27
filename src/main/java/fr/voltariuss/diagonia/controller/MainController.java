package fr.voltariuss.diagonia.controller;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class MainController {

  private final Logger logger;

  @Inject
  public MainController(@NotNull Logger logger) {
    this.logger = logger;
  }

  // TODO: redirect all view calls to this controller first
  // TODO: setup centralized error management here

  public void openMainMenu(@NotNull Player whoOpen) {
    logger.debug("Open MainMenu GUI for a player: playerName={}", whoOpen.getName());
    whoOpen.closeInventory(Reason.PLUGIN);
    whoOpen.performCommand("menu");
  }
}
