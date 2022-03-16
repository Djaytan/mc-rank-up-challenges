package fr.voltariuss.diagonia.controller;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Singleton
public class MasterController {

  @Inject
  public MasterController() {}

  public void sendSystemMessage(
      @NotNull CommandSender commandSender, @NotNull Component component) {
    Audience.audience(commandSender).sendMessage(component, MessageType.SYSTEM);
  }
}
