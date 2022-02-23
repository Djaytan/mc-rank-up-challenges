package fr.voltariuss.diagonia.model.service;

import fr.voltariuss.diagonia.model.config.PluginConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public final class PluginConfigService {

  public static final String DEBUG_MODE = "debug";
  public static final String DATABASE_IS_ENABLED = "database.is_enabled";
  public static final String DATABASE_HOST = "database.host";
  public static final String DATABASE_PORT = "database.port";
  public static final String DATABASE_DATABASE = "database.database";
  public static final String DATABASE_USERNAME = "database.username";
  public static final String DATABASE_PASSWORD = "database.password";
  public static final String PLAYERSHOP_BUY_COST = "playershop.buy_cost";
  public static final String PLAYERSHOP_DESCRIPTION_MAX_SIZE = "playershop.description_max_size";
  public static final String PLAYERSHOP_INACTIVITY_DELAY = "playershop.inactivity_delay";
  public static final String PLAYERSHOP_PREMIUM_SLOT_DURATION = "playershop.premium_slot.duration";
  public static final String PLAYERSHOP_PREMIUM_SLOT_BUY_COST = "playershop.premium_slot.buy_cost";

  public static void init(@NotNull FileConfiguration config) {
    // Database information
    config.addDefault(DEBUG_MODE, false);

    config.addDefault(DATABASE_IS_ENABLED, false);
    config.addDefault(DATABASE_HOST, "localhost");
    config.addDefault(DATABASE_PORT, 3306);
    config.addDefault(DATABASE_DATABASE, "database");
    config.addDefault(DATABASE_USERNAME, "username");
    config.addDefault(DATABASE_PASSWORD, "password");

    config.addDefault(PLAYERSHOP_BUY_COST, 20000D);
    config.addDefault(PLAYERSHOP_DESCRIPTION_MAX_SIZE, 150);
    config.addDefault(PLAYERSHOP_INACTIVITY_DELAY, 30);
    config.addDefault(PLAYERSHOP_PREMIUM_SLOT_DURATION, 72);
    config.addDefault(PLAYERSHOP_PREMIUM_SLOT_BUY_COST, 10000D);
  }

  public static @NotNull PluginConfig loadConfig(@NotNull FileConfiguration config) {
    return PluginConfig.builder()
        .databaseConfig(
            PluginConfig.DatabaseConfig.builder()
                .isEnabled(config.getBoolean(DATABASE_IS_ENABLED))
                .host(config.getString(DATABASE_HOST))
                .port(config.getInt(DATABASE_PORT))
                .database(config.getString(DATABASE_DATABASE))
                .username(config.getString(DATABASE_USERNAME))
                .password(config.getString(DATABASE_PASSWORD))
                .build())
        .playerShopConfig(
            PluginConfig.PlayerShopConfig.builder()
                .buyCost(config.getLong(PLAYERSHOP_BUY_COST))
                .descriptionMaxSize(config.getInt(PLAYERSHOP_DESCRIPTION_MAX_SIZE))
                .inactivityDelay(config.getInt(PLAYERSHOP_INACTIVITY_DELAY))
                .premiumSlotDuration(config.getInt(PLAYERSHOP_PREMIUM_SLOT_DURATION))
                .premiumSlotBuyCost(config.getLong(PLAYERSHOP_PREMIUM_SLOT_BUY_COST))
                .build())
        .debugMode(config.getBoolean(DEBUG_MODE))
        .build();
  }

  private PluginConfigService() {}
}
