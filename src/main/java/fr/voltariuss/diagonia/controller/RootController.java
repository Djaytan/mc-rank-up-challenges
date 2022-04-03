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

import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class RootController {

  private final Logger logger;

  @Inject
  public RootController(@NotNull Logger logger) {
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
