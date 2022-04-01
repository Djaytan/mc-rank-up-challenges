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

package fr.voltariuss.diagonia;

import fr.voltariuss.diagonia.guice.DiagoniaPlayerShopsInjector;
import fr.voltariuss.diagonia.model.RankConfigDeserializer;
import fr.voltariuss.diagonia.model.RankConfigInitializer;
import fr.voltariuss.diagonia.model.config.PluginConfig;
import fr.voltariuss.diagonia.model.config.rank.RankConfig;
import fr.voltariuss.diagonia.model.service.PluginConfigService;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;

/** Diagonia plugin */
public class DiagoniaPlugin extends JavaPlugin {

  @Inject private SessionFactory sessionFactory;
  @Inject private CommandRegister commandRegister;
  @Inject private PrerequisitesValidation prerequisitesValidation;

  @SneakyThrows
  @Override
  public void onEnable() {
    getSLF4JLogger().info("This plugin has been developed by Voltariuss");

    // General plugin config init
    PluginConfigService.init(getConfig());
    getConfig().options().copyDefaults(true);
    saveConfig();
    PluginConfig pluginConfig = PluginConfigService.loadConfig(getConfig());
    getSLF4JLogger().info("Configuration loaded");

    // Rank config init
    RankConfigInitializer rankConfigInitializer =
        new RankConfigInitializer(
            getDataFolder(), this, getSLF4JLogger(), new RankConfigDeserializer());
    rankConfigInitializer.init();
    RankConfig rankConfig = rankConfigInitializer.readRankConfig();

    if (!pluginConfig.getDatabaseConfig().isEnabled()) {
      getSLF4JLogger()
          .error("Database disabled. Please configure and activate it through config.yml file");
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    // Guice setup
    DiagoniaPlayerShopsInjector.inject(this, pluginConfig, rankConfig);

    // Additional setup
    prerequisitesValidation.validate();
    commandRegister.registerCommands();

    getSLF4JLogger().info("Plugin successfully enabled");
  }

  @Override
  public void onDisable() {
    if (sessionFactory != null) {
      sessionFactory.close();
      getSLF4JLogger().info("Database connection closed");
    }
    getSLF4JLogger().info("Plugin successfully disabled");
  }
}
