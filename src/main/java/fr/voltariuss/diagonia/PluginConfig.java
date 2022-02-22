package fr.voltariuss.diagonia;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PluginConfig {

  private final DatabaseConfig databaseConfig;
  private final PlayerShopConfig playerShopConfig;
  private final boolean debugMode;

  @Data
  @Builder
  public static class DatabaseConfig {
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
  }
}
