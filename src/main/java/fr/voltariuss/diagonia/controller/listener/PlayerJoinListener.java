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

package fr.voltariuss.diagonia.controller.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.voltariuss.diagonia.controller.api.RankUpChallengesController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlayerJoinListener implements Listener {

  private final RankUpChallengesController rankUpChallengesController;

  @Inject
  public PlayerJoinListener(@NotNull RankUpChallengesController rankUpChallengesController) {
    this.rankUpChallengesController = rankUpChallengesController;
  }

  @EventHandler
  public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
    Player player = event.getPlayer();
    rankUpChallengesController.prepareRankChallenges(player);
  }
}
