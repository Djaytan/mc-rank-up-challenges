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
    // TODO: create a real event with Observer pattern or Bukkit API
    playerShopController.openPlayerShopListView(player);
  }
}
