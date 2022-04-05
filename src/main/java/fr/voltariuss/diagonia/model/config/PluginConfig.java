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

package fr.voltariuss.diagonia.model.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PluginConfig {

  private final DatabaseConfig databaseConfig;
  private final PlayerShopConfig playerShopConfig;
  private final RankUpConfig rankUpConfig;
  private final boolean debugMode;

  @Data
  @Builder
  public static class DatabaseConfig {

    private final boolean isEnabled;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
  }

  @Data
  @Builder
  public static class PlayerShopConfig {

    private final double buyCost;
    private final int descriptionMaxSize;
    private final int inactivityDelay;
    private final int premiumSlotDuration;
    private final double premiumSlotBuyCost;
    private final String tpCreationAllowedWorld;
  }

  @Data
  @Builder
  public static class RankUpConfig {

    private final String luckPermsTrackName;
  }
}
