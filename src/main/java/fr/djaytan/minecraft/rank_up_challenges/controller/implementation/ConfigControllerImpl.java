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

package fr.djaytan.minecraft.rank_up_challenges.controller.implementation;

import com.google.common.base.Preconditions;
import fr.djaytan.minecraft.rank_up_challenges.RankUpChallengesRuntimeException;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.ConfigController;
import fr.djaytan.minecraft.rank_up_challenges.model.config.ConfigFile;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.PluginConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.challenge.ChallengeConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.RankConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.serializers.PluginConfigSerializers;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

@Singleton
public class ConfigControllerImpl implements ConfigController {

  private final Path dataFolder;
  private final PluginConfigSerializers pluginConfigSerializers;
  private final Plugin plugin;

  @Inject
  public ConfigControllerImpl(
      @NotNull PluginConfigSerializers pluginConfigSerializers, @NotNull Plugin plugin) {
    this.dataFolder = plugin.getDataFolder().toPath();
    this.pluginConfigSerializers = pluginConfigSerializers;
    this.plugin = plugin;
  }

  @Override
  public <T> @NotNull T loadConfig(@NotNull ConfigFile configFile, @NotNull Class<T> clazz) {
    Preconditions.checkNotNull(configFile);
    Preconditions.checkNotNull(clazz);

    String configFileName = configFile.getConfigFileName();

    HoconConfigurationLoader loader =
        HoconConfigurationLoader.builder()
            .defaultOptions(
                configurationOptions ->
                    configurationOptions.serializers(
                        builder -> builder.registerAll(pluginConfigSerializers.collection())))
            .path(dataFolder.resolve(configFileName))
            .build();

    try {
      ConfigurationNode rootNode = loader.load();
      @Nullable T config = rootNode.get(clazz);

      if (config == null) {
        throw new RankUpChallengesRuntimeException(
            String.format(
                "Content of the config '%s' seems to be empty or wrong.", configFileName));
      }

      return config;
    } catch (ConfigurateException e) {
      throw new RankUpChallengesRuntimeException(
          String.format("Failed to load plugin config '%s'.", configFileName), e);
      // TODO: centralized error management (e.g. ExceptionHandler class)
    }
  }

  @Override
  public @NotNull ChallengeConfig loadChallengeConfig() {
    return loadConfig(ConfigFile.CHALLENGES, ChallengeConfig.class);
  }

  @Override
  public @NotNull PluginConfig loadPluginConfig() {
    PluginConfig pluginConfig = loadConfig(ConfigFile.PLUGIN, PluginConfig.class);

    if (!pluginConfig.getDatabase().isEnabled()) {
      throw new RankUpChallengesRuntimeException(
          "Database disabled. Please configure and activate it through config.yml file.");
    }

    return pluginConfig;
  }

  @Override
  public @NotNull RankConfig loadRankConfig() {
    return loadConfig(ConfigFile.RANKS, RankConfig.class);
  }

  @Override
  public void saveDefaultConfigs() {
    if (Files.notExists(dataFolder)) {
      try {
        Files.createDirectory(dataFolder);
      } catch (IOException e) {
        throw new RankUpChallengesRuntimeException("Failed to create plugin's data folder.", e);
      }
    }

    for (ConfigFile configFile : ConfigFile.values()) {
      String configFileName = configFile.getConfigFileName();
      Path configFilePath = dataFolder.resolve(configFileName);

      if (Files.exists(configFilePath)) {
        continue;
      }

      try (InputStream inputStream = plugin.getClass().getResourceAsStream("/" + configFileName)) {
        if (inputStream == null) {
          throw new RankUpChallengesRuntimeException(
              String.format("No default config exists for '%s' file.", configFileName));
        }

        Files.copy(inputStream, configFilePath);
      } catch (IOException e) {
        throw new RankUpChallengesRuntimeException(
            String.format(
                "Failed to save default config file '%s' in the plugin's data folder",
                configFileName),
            e);
      }
    }
  }
}
