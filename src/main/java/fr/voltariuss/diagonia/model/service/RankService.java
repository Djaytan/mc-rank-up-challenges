package fr.voltariuss.diagonia.model.service;

import fr.voltariuss.diagonia.DiagoniaException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.logging.log4j.core.util.IOUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class RankService {

  public static final String RANK_JSON_FILE_NAME = "ranks.json";

  private final File dataFolder;
  private final JavaPlugin plugin;
  private final Logger logger;

  @Inject
  public RankService(
      @Named("dataFolder") @NotNull File dataFolder,
      @NotNull JavaPlugin plugin,
      @NotNull Logger logger) {
    this.dataFolder = dataFolder;
    this.plugin = plugin;
    this.logger = logger;
  }

  public void prepareDataFile() throws IOException, DiagoniaException {
    File jsonFile = new File(dataFolder, RANK_JSON_FILE_NAME);
    if (!jsonFile.exists()) {
      try (InputStream defaultJsonFile = plugin.getResource(RANK_JSON_FILE_NAME)) {
        if (defaultJsonFile != null) {
          try (InputStreamReader defaultJsonFileReader = new InputStreamReader(defaultJsonFile, StandardCharsets.UTF_8); FileWriter jsonFileWriter = new FileWriter(jsonFile, StandardCharsets.UTF_8)) {
            IOUtils.copyLarge(defaultJsonFileReader, jsonFileWriter);
            logger.info("Default {} file copied in data folder.", RANK_JSON_FILE_NAME);
          }
        } else {
          throw new DiagoniaException(
            String.format("Failed to copy default %1$s file in data folder.", RANK_JSON_FILE_NAME));
        }
      }
    }
  }
}
