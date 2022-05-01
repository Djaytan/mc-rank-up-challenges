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

import fr.voltariuss.diagonia.DiagoniaRuntimeException;
import fr.voltariuss.diagonia.model.config.DiagoniaConfig;
import fr.voltariuss.diagonia.model.config.PluginConfig;
import fr.voltariuss.diagonia.model.config.RankConfig;
import fr.voltariuss.diagonia.model.config.serializers.DiagoniaConfigSerializers;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

@Singleton
public class ConfigControllerImpl implements ConfigController {

  private final Path dataFolder;
  private final DiagoniaConfigSerializers diagoniaConfigSerializers;
  private final Plugin plugin;

  @Inject
  public ConfigControllerImpl(
      @NotNull DiagoniaConfigSerializers diagoniaConfigSerializers, @NotNull Plugin plugin) {
    this.dataFolder = plugin.getDataFolder().toPath();
    this.diagoniaConfigSerializers = diagoniaConfigSerializers;
    this.plugin = plugin;
  }

  @Override
  public <T> @NotNull T loadConfig(@NotNull DiagoniaConfig diagoniaConfig, Class<T> clazz) {
    String configFileName = diagoniaConfig.getConfigFileName();

    HoconConfigurationLoader loader =
        HoconConfigurationLoader.builder()
            .defaultOptions(
                configurationOptions ->
                    configurationOptions.serializers(
                        TypeSerializerCollection.defaults()
                            .childBuilder()
                            .registerAll(diagoniaConfigSerializers.collection())
                            .build()))
            .path(dataFolder.resolve(configFileName))
            .build();

    try {
      CommentedConfigurationNode rootNode = loader.load();
      @Nullable T config = rootNode.get(clazz);

      if (config == null) {
        throw new DiagoniaRuntimeException(
            String.format(
                "Content of the config '%s' seems to be empty or wrong.", configFileName));
      }

      System.out.println(config);

      return config;
    } catch (ConfigurateException e) {
      throw new DiagoniaRuntimeException(
          String.format("Failed to load plugin config '%s'.", configFileName), e);
      // TODO: centralized error management (e.g. ExceptionHandler class)
    }
  }

  @Override
  public @NotNull PluginConfig loadPluginConfig() {
    PluginConfig pluginConfig = loadConfig(DiagoniaConfig.PLUGIN, PluginConfig.class);

    if (!pluginConfig.getDatabase().isEnabled()) {
      throw new DiagoniaRuntimeException(
          "Database disabled. Please configure and activate it through config.yml file.");
    }

    return pluginConfig;
  }

  @Override
  public @NotNull RankConfig loadRankConfig() {
    return loadConfig(DiagoniaConfig.RANKS, RankConfig.class);
  }

  @Override
  public void saveDefaultConfigs() {
    if (Files.notExists(dataFolder)) {
      try {
        Files.createDirectory(dataFolder);
      } catch (IOException e) {
        throw new DiagoniaRuntimeException("Failed to create plugin's data folder.", e);
      }
    }

    for (DiagoniaConfig diagoniaConfig : DiagoniaConfig.values()) {
      String configFileName = diagoniaConfig.getConfigFileName();
      Path configFilePath = dataFolder.resolve(configFileName);

      if (Files.exists(configFilePath)) {
        continue;
      }

      try (InputStream inputStream = plugin.getClass().getResourceAsStream("/" + configFileName)) {
        if (inputStream == null) {
          throw new DiagoniaRuntimeException(
              String.format("No default config exists for '%s' file.", configFileName));
        }

        Files.copy(inputStream, configFilePath);
      } catch (IOException e) {
        throw new DiagoniaRuntimeException(
            String.format(
                "Failed to save default config file '%s' in the plugin's data folder",
                configFileName),
            e);
      }
    }
  }
}
