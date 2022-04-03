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
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

// TODO: maybe the name and place in code is not the best
@Singleton
public class ControllerHelper {

  private final DiagoniaLogger logger;
  private final ResourceBundle resourceBundle;
  private final Server server;

  @Inject
  public ControllerHelper(
      @NotNull DiagoniaLogger logger,
      @NotNull ResourceBundle resourceBundle,
      @NotNull Server server) {
    this.logger = logger;
    this.resourceBundle = resourceBundle;
    this.server = server;
  }

  public void sendSystemMessage(
      @NotNull CommandSender commandSender, @NotNull Component component) {
    Audience.audience(commandSender).sendMessage(component, MessageType.SYSTEM);
  }

  public void broadcastMessage(@NotNull Component component) {
    Audience.audience(server.getOnlinePlayers()).sendMessage(component);
  }

  public @NotNull String getOfflinePlayerName(@NotNull OfflinePlayer offlinePlayer) {
    String offlinePlayerName = offlinePlayer.getName();

    if (offlinePlayerName == null) {
      logger.warn("The UUID {} isn't associated to any player name.", offlinePlayer.getUniqueId());
      offlinePlayerName = resourceBundle.getString("diagonia.common.default_player_name");
    }

    return offlinePlayerName;
  }
}
