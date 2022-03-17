package fr.voltariuss.diagonia.controller;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

// TODO: rename to MainController
@Singleton
public class MasterController {

  @Inject
  public MasterController() {}

  // TODO: redirect all view calls to this controller first

  // TODO: setup centralized error management here

  public void sendSystemMessage(
      @NotNull CommandSender commandSender, @NotNull Component component) {
    Audience.audience(commandSender).sendMessage(component, MessageType.SYSTEM);
  }
}
