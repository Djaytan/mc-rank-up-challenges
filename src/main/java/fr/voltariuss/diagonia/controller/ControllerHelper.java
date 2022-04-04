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

import fr.voltariuss.diagonia.DiagoniaLogger;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

// TODO: rename it MessageController + split interface and implementation
@Singleton
public class ControllerHelper {

  private final DiagoniaLogger logger;
  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;
  private final Server server;

  @Inject
  public ControllerHelper(
      @NotNull DiagoniaLogger logger,
      @NotNull MiniMessage miniMessage,
      @NotNull ResourceBundle resourceBundle,
      @NotNull Server server) {
    this.logger = logger;
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
    this.server = server;
  }

  public void sendSystemMessage(@NotNull CommandSender commandSender, @NotNull Component message) {
    sendMessage(commandSender, Prefix.DEFAULT, message);
  }

  public void sendWarningMessage(@NotNull CommandSender commandSender, @NotNull Component message) {
    sendMessage(commandSender, Prefix.WARNING, message);
  }

  public void sendErrorMessage(@NotNull CommandSender commandSender, @NotNull Component message) {
    sendMessage(commandSender, Prefix.ERROR, message);
  }

  public void broadcastMessage(@NotNull Component component) {
    sendMessage(Audience.audience(server.getOnlinePlayers()), Prefix.BROADCAST, component);
  }

  public @NotNull String getOfflinePlayerName(@NotNull OfflinePlayer offlinePlayer) {
    // TODO: move to PlayerUtils class
    String offlinePlayerName = offlinePlayer.getName();

    if (offlinePlayerName == null) {
      logger.warn("The UUID {} isn't associated to any player name.", offlinePlayer.getUniqueId());
      offlinePlayerName = resourceBundle.getString("diagonia.common.default_player_name");
    }

    return offlinePlayerName;
  }

  private void sendMessage(
      @NotNull Audience audience, @NotNull Prefix prefix, @NotNull Component message) {
    Component prefixCpnt = getPrefix(prefix);
    Component messageFormat = getMessageFormat(prefixCpnt, message);
    Audience.audience(audience).sendMessage(messageFormat, MessageType.SYSTEM);
  }

  private @NotNull Component getMessageFormat(
      @NotNull Component prefixCpnt, @NotNull Component message) {
    return miniMessage
        .deserialize(
            resourceBundle.getString("diagonia.common.message.format"),
            TemplateResolver.templates(
                Template.template("diag_message_prefix", prefixCpnt),
                Template.template("diag_message_content", message)))
        .decoration(TextDecoration.ITALIC, false);
  }

  private @NotNull Component getPrefix(@NotNull Prefix prefix) {
    String prefixStrKey =
        switch (prefix) {
          case BROADCAST -> "diagonia.common.prefix.broadcast";
          case WARNING -> "diagonia.common.prefix.warning";
          case ERROR -> "diagonia.common.prefix.error";
          default -> "diagonia.common.prefix.default";
        };

    return miniMessage
        .deserialize(resourceBundle.getString(prefixStrKey))
        .decoration(TextDecoration.ITALIC, false);
  }
}
