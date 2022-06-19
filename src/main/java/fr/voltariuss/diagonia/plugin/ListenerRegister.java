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

package fr.voltariuss.diagonia.plugin;

import fr.voltariuss.diagonia.controller.listener.bukkit.BlockPlaceListener;
import fr.voltariuss.diagonia.controller.listener.bukkit.PlayerJoinListener;
import fr.voltariuss.diagonia.controller.listener.jobs.JobsExpGainListener;
import fr.voltariuss.diagonia.controller.listener.jobs.JobsPrePaymentListener;
import javax.inject.Inject;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public class ListenerRegister {

  private final Plugin plugin;
  private final PluginManager pluginManager;

  private final BlockPlaceListener blockPlaceListener;
  private final JobsExpGainListener jobsExpGainListener;
  private final JobsPrePaymentListener jobsPrePaymentListener;
  private final PlayerJoinListener playerJoinListener;

  @Inject
  public ListenerRegister(
      @NotNull Plugin plugin,
      @NotNull PluginManager pluginManager,
      @NotNull BlockPlaceListener blockPlaceListener,
      @NotNull JobsExpGainListener jobsExpGainListener,
      @NotNull JobsPrePaymentListener jobsPrePaymentListener,
      @NotNull PlayerJoinListener playerJoinListener) {
    this.plugin = plugin;
    this.pluginManager = pluginManager;

    this.blockPlaceListener = blockPlaceListener;
    this.jobsExpGainListener = jobsExpGainListener;
    this.jobsPrePaymentListener = jobsPrePaymentListener;
    this.playerJoinListener = playerJoinListener;
  }

  public void registerListeners() {
    pluginManager.registerEvents(blockPlaceListener, plugin);
    pluginManager.registerEvents(jobsExpGainListener, plugin);
    pluginManager.registerEvents(jobsPrePaymentListener, plugin);
    pluginManager.registerEvents(playerJoinListener, plugin);
  }
}
