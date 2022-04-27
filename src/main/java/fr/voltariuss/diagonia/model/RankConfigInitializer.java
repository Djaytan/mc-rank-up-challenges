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

package fr.voltariuss.diagonia.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.voltariuss.diagonia.DiagoniaException;
import fr.voltariuss.diagonia.model.config.rank.RankConfig;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.logging.log4j.core.util.IOUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class RankConfigInitializer {

  public static final String RANK_JSON_FILE_NAME = "ranks.json";

  /*
   * Used instead of RemakeBukkitLogger because this class is instantiated before Guice injection...
   * But since we don't need of debug logs here it's "ok".
   */
  private final Logger logger;
  private final Plugin plugin;
  private final RankConfigDeserializer rankConfigDeserializer;

  @Inject
  public RankConfigInitializer(
      @NotNull Logger logger,
      @NotNull Plugin plugin,
      @NotNull RankConfigDeserializer rankConfigDeserializer) {
    this.logger = logger;
    this.plugin = plugin;
    this.rankConfigDeserializer = rankConfigDeserializer;
  }

  public void init() throws IOException, DiagoniaException {
    File jsonFile = getRankConfigFile();
    if (!jsonFile.exists()) {
      try (InputStream defaultJsonFile = plugin.getResource(RANK_JSON_FILE_NAME)) {
        if (defaultJsonFile != null) {
          try (InputStreamReader defaultJsonFileReader =
                  new InputStreamReader(defaultJsonFile, StandardCharsets.UTF_8);
              FileWriter jsonFileWriter = new FileWriter(jsonFile, StandardCharsets.UTF_8)) {
            IOUtils.copyLarge(defaultJsonFileReader, jsonFileWriter);
            logger.info("Default {} file copied in data folder.", RANK_JSON_FILE_NAME);
          }
          return;
        }
        throw new DiagoniaException(
            String.format("Failed to copy default %1$s file in data folder.", RANK_JSON_FILE_NAME));
      }
    }
  }

  public @NotNull RankConfig readRankConfig() throws IOException {
    File jsonFile = getRankConfigFile();
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(RankConfig.class, rankConfigDeserializer);
    Gson gson = gsonBuilder.create();
    try (FileReader jsonReader = new FileReader(jsonFile, StandardCharsets.UTF_8)) {
      return gson.fromJson(jsonReader, RankConfig.class);
    }
  }

  private @NotNull File getRankConfigFile() {
    return new File(plugin.getDataFolder(), RANK_JSON_FILE_NAME);
  }
}
