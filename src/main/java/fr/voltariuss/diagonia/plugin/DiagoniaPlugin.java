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

import fr.voltariuss.diagonia.controller.api.ConfigController;
import fr.voltariuss.diagonia.controller.api.PluginController;
import fr.voltariuss.diagonia.controller.implementation.ConfigControllerImpl;
import fr.voltariuss.diagonia.model.config.data.PluginConfig;
import fr.voltariuss.diagonia.model.config.data.challenge.ChallengeConfig;
import fr.voltariuss.diagonia.model.config.data.rank.RankConfig;
import fr.voltariuss.diagonia.model.config.serializers.DiagoniaConfigSerializersFactory;
import fr.voltariuss.diagonia.plugin.guice.GuiceInjector;
import javax.inject.Inject;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/** Diagonia plugin */
public class DiagoniaPlugin extends JavaPlugin {

  @Inject private PluginController pluginController;

  @Override
  public void onEnable() {
    try {
      // This is tricky at startup, but don't found better way than that...
      // Perfect startup would inject Guice immediately, but some injections need config values
      ConfigController configController = createConfigController();

      configController.saveDefaultConfigs();
      ChallengeConfig challengeConfig = configController.loadChallengeConfig();
      PluginConfig pluginConfig = configController.loadPluginConfig();
      RankConfig rankConfig = configController.loadRankConfig();

      GuiceInjector.inject(this, challengeConfig, pluginConfig, rankConfig);

      // The core start of the plugin happens here
      pluginController.enablePlugin();
    } catch (Exception e) {
      getSLF4JLogger().error("An exception occurs preventing Diagonia plugin to be enabled.", e);
      setEnabled(false);
    }
  }

  @Override
  public void onDisable() {
    if (pluginController != null) {
      pluginController.disablePlugin();
    }
  }

  private @NotNull ConfigController createConfigController() {
    return new ConfigControllerImpl(new DiagoniaConfigSerializersFactory().factory(), this);
  }
}
