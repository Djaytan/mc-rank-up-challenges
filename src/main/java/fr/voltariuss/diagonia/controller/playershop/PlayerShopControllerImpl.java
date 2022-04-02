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

package fr.voltariuss.diagonia.controller.playershop;

import fr.voltariuss.diagonia.DiagoniaLogger;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.model.service.PlayerShopService;
import fr.voltariuss.diagonia.view.gui.PlayerShopConfigGui;
import fr.voltariuss.diagonia.view.gui.PlayerShopListGui;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlayerShopControllerImpl implements PlayerShopController {

  private final DiagoniaLogger logger;
  private final PlayerShopService playerShopService;

  private final Provider<PlayerShopConfigGui> playerShopConfigGui;
  private final Provider<PlayerShopListGui> playerShopListGui;

  @Inject
  public PlayerShopControllerImpl(
      @NotNull DiagoniaLogger logger,
      @NotNull PlayerShopService playerShopService,
      @NotNull Provider<PlayerShopConfigGui> playerShopConfigGui,
      @NotNull Provider<PlayerShopListGui> playerShopListGui) {
    this.logger = logger;
    this.playerShopService = playerShopService;
    this.playerShopConfigGui = playerShopConfigGui;
    this.playerShopListGui = playerShopListGui;
  }

  @Override
  public void openPlayerShopListGui(@NotNull Player whoOpen) {
    logger.debug("Open PlayerShopList GUI for a player: playerName={}", whoOpen.getName());
    Optional<PlayerShop> playerShopOwned = playerShopService.findByUuid(whoOpen.getUniqueId());
    List<PlayerShop> playerShopList = playerShopService.findAll();
    playerShopListGui.get().open(whoOpen, playerShopList, playerShopOwned.isPresent());
  }

  @Override
  public void openPlayerShopConfigGui(@NotNull Player whoOpen) {
    logger.debug("Open PlayerShopConfig GUI for player {}", whoOpen.getName());
    PlayerShop playerShop = playerShopService.findByUuid(whoOpen.getUniqueId()).orElseThrow();
    playerShopConfigGui.get().open(whoOpen, playerShop);
  }
}
