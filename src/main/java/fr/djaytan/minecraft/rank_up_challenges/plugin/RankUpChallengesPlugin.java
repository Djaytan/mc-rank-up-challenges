/*
 * Rank-Up challenges plugin for Minecraft (Bukkit servers)
 * Copyright (C) 2022 - Loïc DUBOIS-TERMOZ
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.djaytan.minecraft.rank_up_challenges.plugin;

import fr.djaytan.minecraft.rank_up_challenges.controller.api.ConfigController;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.PluginController;
import fr.djaytan.minecraft.rank_up_challenges.controller.implementation.ConfigControllerImpl;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.PluginConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.challenge.ChallengeConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.RankConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.serializers.PluginConfigSerializersFactory;
import fr.djaytan.minecraft.rank_up_challenges.plugin.guice.GuiceInjector;
import javax.inject.Inject;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/** This class represents the rank-Up challenges plugin. */
public class RankUpChallengesPlugin extends JavaPlugin {

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
      getSLF4JLogger()
          .error("An exception occurs preventing RankUpChallenges plugin to be enabled.", e);
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
    return new ConfigControllerImpl(new PluginConfigSerializersFactory().factory(), this);
  }
}
