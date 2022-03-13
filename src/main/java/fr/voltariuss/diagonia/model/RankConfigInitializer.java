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
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.logging.log4j.core.util.IOUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class RankConfigInitializer {

  public static final String RANK_JSON_FILE_NAME = "ranks.json";

  private final File dataFolder;
  private final JavaPlugin plugin;
  // used instead of DiagoniaLogger because of instanciation before Guice injection
  private final Logger logger;
  private final RankConfigDeserializer rankConfigDeserializer;

  @Inject
  public RankConfigInitializer(
      @Named("dataFolder") @NotNull File dataFolder,
      @NotNull JavaPlugin plugin,
      @NotNull Logger logger,
      @NotNull RankConfigDeserializer rankConfigDeserializer) {
    this.dataFolder = dataFolder;
    this.plugin = plugin;
    this.logger = logger;
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
        } else {
          throw new DiagoniaException(
              String.format(
                  "Failed to copy default %1$s file in data folder.", RANK_JSON_FILE_NAME));
        }
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
    return new File(dataFolder, RANK_JSON_FILE_NAME);
  }
}
