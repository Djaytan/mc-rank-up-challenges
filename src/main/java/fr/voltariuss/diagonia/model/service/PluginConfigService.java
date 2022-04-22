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

package fr.voltariuss.diagonia.model.service;

import fr.voltariuss.diagonia.model.config.PluginConfig;
import java.util.Arrays;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
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
  public static final String PLAYERSHOP_TP_CREATION_ALLOWED_WORLD =
      "playershop.tp.creation.allowed_world";
  public static final String RANKUP_LUCKPERMS_TRACK_NAME = "rankup.luckperms.track.name";

  public static void init(@NotNull FileConfiguration config) {
    config.addDefault(DEBUG_MODE, false);

    // Database information
    config.addDefault(DATABASE_IS_ENABLED, false);
    config.addDefault(DATABASE_HOST, "localhost");
    config.addDefault(DATABASE_PORT, 3306);
    config.addDefault(DATABASE_DATABASE, "database");
    config.addDefault(DATABASE_USERNAME, "username");
    config.addDefault(DATABASE_PASSWORD, "password");

    // Playershop feature
    config.addDefault(PLAYERSHOP_BUY_COST, 20000D);
    config.addDefault(PLAYERSHOP_DESCRIPTION_MAX_SIZE, 150);
    config.addDefault(PLAYERSHOP_INACTIVITY_DELAY, 30);
    config.addDefault(PLAYERSHOP_PREMIUM_SLOT_DURATION, 72);
    config.addDefault(PLAYERSHOP_PREMIUM_SLOT_BUY_COST, 10000D);
    config.addDefault(PLAYERSHOP_TP_CREATION_ALLOWED_WORLD, "world");

    // Rankup feature
    config.addDefault(RANKUP_LUCKPERMS_TRACK_NAME, "ranks");
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
                .tpCreationAllowedWorld(config.getString(PLAYERSHOP_TP_CREATION_ALLOWED_WORLD))
                .build())
        .rankUpConfig(
            PluginConfig.RankUpConfig.builder()
                .luckPermsTrackName(config.getString(RANKUP_LUCKPERMS_TRACK_NAME))
                .build())
        .blacklistedEnchantments(Arrays.asList(Enchantment.MENDING, Enchantment.ARROW_INFINITE))
        .debugMode(config.getBoolean(DEBUG_MODE))
        .build();
  }

  private PluginConfigService() {}
}
