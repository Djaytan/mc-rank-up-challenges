package fr.voltariuss.diagonia.controller;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ControllerHelper {

  private final Server server;

  @Inject
  public ControllerHelper(@NotNull Server server) {
    this.server = server;
  }

  public void sendSystemMessage(
      @NotNull CommandSender commandSender, @NotNull Component component) {
    Audience.audience(commandSender).sendMessage(component, MessageType.SYSTEM);
  }

  public void broadcastMessage(@NotNull Component component) {
    Audience.audience(server.getOnlinePlayers()).sendMessage(component);
  }
}
