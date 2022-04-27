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

import fr.voltariuss.diagonia.DiagoniaException;
import fr.voltariuss.diagonia.DiagoniaRuntimeException;
import fr.voltariuss.diagonia.model.RankConfigInitializer;
import fr.voltariuss.diagonia.model.config.PluginConfig;
import fr.voltariuss.diagonia.model.config.rank.RankConfig;
import fr.voltariuss.diagonia.model.service.PluginConfigService;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ConfigControllerImpl implements ConfigController {

  private final Plugin plugin;
  private final PluginConfigService pluginConfigService;
  private final RankConfigInitializer rankConfigInitializer;

  @Inject
  public ConfigControllerImpl(
      @NotNull Plugin plugin,
      @NotNull PluginConfigService pluginConfigService,
      @NotNull RankConfigInitializer rankConfigInitializer) {
    this.plugin = plugin;
    this.pluginConfigService = pluginConfigService;
    this.rankConfigInitializer = rankConfigInitializer;
  }

  @Override
  public @NotNull PluginConfig loadPluginConfig() {
    pluginConfigService.init(plugin.getConfig());
    plugin.getConfig().options().copyDefaults(true);
    plugin.saveConfig();
    PluginConfig pluginConfig = pluginConfigService.loadConfig(plugin.getConfig());

    if (!pluginConfig.getDatabaseConfig().isEnabled()) {
      throw new DiagoniaRuntimeException(
          "Database disabled. Please configure and activate it through config.yml file.");
    }

    return pluginConfig;
  }

  @Override
  public @NotNull RankConfig loadRankConfig() {
    try {
      rankConfigInitializer.init();
      return rankConfigInitializer.readRankConfig();
    } catch (IOException | DiagoniaException e) {
      throw new DiagoniaRuntimeException("Failed to load rank config.", e);
    }
  }
}
