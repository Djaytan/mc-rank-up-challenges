/*
 * Copyright (c) 2022 - Loïc DUBOIS-TERMOZ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.voltariuss.diagonia.controller;

import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Singleton
public class MessageControllerImpl implements MessageController {

  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;
  private final Server server;

  @Inject
  public MessageControllerImpl(
      @NotNull MiniMessage miniMessage,
      @NotNull ResourceBundle resourceBundle,
      @NotNull Server server) {
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
    this.server = server;
  }

  @Override
  public void sendInfoMessage(@NotNull CommandSender commandSender, @NotNull Component message) {
    sendMessage(commandSender, MessageType.INFO, message);
  }

  @Override
  public void sendSuccessMessage(@NotNull CommandSender commandSender, @NotNull Component message) {
    sendMessage(commandSender, MessageType.SUCCESS, message);
  }

  @Override
  public void sendFailureMessage(@NotNull CommandSender commandSender, @NotNull Component message) {
    sendMessage(commandSender, MessageType.FAILURE, message);
  }

  @Override
  public void sendWarningMessage(@NotNull CommandSender commandSender, @NotNull Component message) {
    sendMessage(commandSender, MessageType.WARNING, message);
  }

  @Override
  public void sendErrorMessage(@NotNull CommandSender commandSender, @NotNull Component message) {
    sendMessage(commandSender, MessageType.ERROR, message);
  }

  @Override
  public void broadcastMessage(@NotNull Component component) {
    sendMessage(Audience.audience(server.getOnlinePlayers()), MessageType.BROADCAST, component);
  }

  private void sendMessage(
      @NotNull Audience audience, @NotNull MessageType messageType, @NotNull Component message) {
    Component messageFormat = getMessageFormat(messageType, message);
    Audience.audience(audience)
        .sendMessage(messageFormat, net.kyori.adventure.audience.MessageType.SYSTEM);
  }

  private @NotNull Component getMessageFormat(
      @NotNull MessageType messageType, @NotNull Component message) {
    String messageFormatKey =
        switch (messageType) {
          case INFO -> "diagonia.common.message.format.info";
          case SUCCESS -> "diagonia.common.message.format.success";
          case FAILURE -> "diagonia.common.message.format.failure";
          case WARNING -> "diagonia.common.message.format.warning";
          case ERROR -> "diagonia.common.message.format.error";
          case BROADCAST -> "diagonia.common.message.format.broadcast";
          case DEBUG -> "diagonia.common.message.format.debug";
        };

    return miniMessage
        .deserialize(
            resourceBundle.getString(messageFormatKey),
            TemplateResolver.templates(Template.template("diag_message_content", message)))
        .decoration(TextDecoration.ITALIC, false);
  }
}
