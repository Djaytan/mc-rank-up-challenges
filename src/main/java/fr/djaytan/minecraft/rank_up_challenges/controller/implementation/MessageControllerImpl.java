/*
 * Rank-Up challenges plugin for Minecraft (Bukkit servers)
 * Copyright (C) 2022 - Loïc DUBOIS-TERMOZ
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.djaytan.minecraft.rank_up_challenges.controller.implementation;

import fr.djaytan.minecraft.rank_up_challenges.controller.api.MessageController;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.dto.MessageType;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

@Singleton
public class MessageControllerImpl implements MessageController {

  private final ConsoleCommandSender consoleCommandSender;
  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;
  private final Server server;

  @Inject
  public MessageControllerImpl(
      @NotNull ConsoleCommandSender consoleCommandSender,
      @NotNull MiniMessage miniMessage,
      @NotNull ResourceBundle resourceBundle,
      @NotNull Server server) {
    this.consoleCommandSender = consoleCommandSender;
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
    this.server = server;
  }

  @Override
  public void sendInfoMessage(@NotNull Audience audience, @NotNull Component message) {
    sendMessage(audience, MessageType.INFO, message);
  }

  @Override
  public void sendSuccessMessage(@NotNull Audience audience, @NotNull Component message) {
    sendMessage(audience, MessageType.SUCCESS, message);
  }

  @Override
  public void sendFailureMessage(@NotNull Audience audience, @NotNull Component message) {
    sendMessage(audience, MessageType.FAILURE, message);
  }

  @Override
  public void sendWarningMessage(@NotNull Audience audience, @NotNull Component message) {
    sendMessage(audience, MessageType.WARNING, message);
  }

  @Override
  public void sendErrorMessage(@NotNull Audience audience, @NotNull Component message) {
    sendMessage(audience, MessageType.ERROR, message);
  }

  @Override
  public void sendRawMessage(@NotNull Audience audience, @NotNull Component message) {
    audience.sendMessage(message, net.kyori.adventure.audience.MessageType.SYSTEM);
  }

  public void sendConsoleMessage(@NotNull Component message) {
    consoleCommandSender.sendMessage(message, net.kyori.adventure.audience.MessageType.SYSTEM);
  }

  @Override
  public void broadcastMessage(@NotNull Component message) {
    sendMessage(Audience.audience(server.getOnlinePlayers()), MessageType.BROADCAST, message);
  }

  private void sendMessage(
      @NotNull Audience audience, @NotNull MessageType messageType, @NotNull Component message) {
    Component formattedMessage = formatMessage(messageType, message);
    audience.sendMessage(formattedMessage, net.kyori.adventure.audience.MessageType.SYSTEM);
  }

  private @NotNull Component formatMessage(
      @NotNull MessageType messageType, @NotNull Component message) {
    String messageFormatKey =
        switch (messageType) {
          case INFO -> "rank_up_challenges.common.message.format.info";
          case SUCCESS -> "rank_up_challenges.common.message.format.success";
          case FAILURE -> "rank_up_challenges.common.message.format.failure";
          case WARNING -> "rank_up_challenges.common.message.format.warning";
          case ERROR -> "rank_up_challenges.common.message.format.error";
          case BROADCAST -> "rank_up_challenges.common.message.format.broadcast";
          case DEBUG -> "rank_up_challenges.common.message.format.debug";
        };

    return miniMessage
        .deserialize(
            resourceBundle.getString(messageFormatKey),
            TagResolver.resolver(Placeholder.component("ruc_message_content", message)))
        .decoration(TextDecoration.ITALIC, false);
  }
}
