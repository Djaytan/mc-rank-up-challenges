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

package fr.djaytan.minecraft.rank_up_challenges.plugin.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import fr.djaytan.minecraft.rank_up_challenges.RankUpChallengesException;
import fr.djaytan.minecraft.rank_up_challenges.RankUpChallengesRuntimeException;
import fr.djaytan.minecraft.rank_up_challenges.JdbcUrl;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.challenge.ChallengeConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.PluginConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.RankConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.entity.RankChallengeProgression;
import javax.inject.Named;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.track.Track;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class GuicePluginModule extends AbstractModule {

  private final ChallengeConfig challengeConfig;
  private final JdbcUrl jdbcUrl;
  private final Logger logger;
  private final LuckPerms luckPerms;
  private final PluginConfig pluginConfig;
  private final RankConfig rankConfig;

  public GuicePluginModule(
      @NotNull ChallengeConfig challengeConfig,
      @NotNull Logger logger,
      @NotNull LuckPerms luckPerms,
      @NotNull PluginConfig pluginConfig,
      @NotNull RankConfig rankConfig) {
    this.challengeConfig = challengeConfig;
    this.jdbcUrl =
        new JdbcUrl(
            pluginConfig.getDatabase().getHost(),
            pluginConfig.getDatabase().getPort(),
            pluginConfig.getDatabase().getDatabase());
    this.logger = logger;
    this.luckPerms = luckPerms;
    this.pluginConfig = pluginConfig;
    this.rankConfig = rankConfig;
  }

  @Provides
  @Named("debugMode")
  public Boolean provideDebugMode() {
    logger.info("Debug mode: {}", pluginConfig.isDebug());
    return pluginConfig.isDebug();
  }

  @Provides
  @Singleton
  public @NotNull ChallengeConfig provideChallengeConfig() {
    return challengeConfig;
  }

  @Provides
  @Singleton
  public @NotNull PluginConfig providePluginConfig() {
    return pluginConfig;
  }

  @Provides
  @Singleton
  public @NotNull RankConfig provideRankConfig() {
    return rankConfig;
  }

  @Provides
  @Singleton
  public @NotNull JdbcUrl provideJdbcUrl() {
    return jdbcUrl;
  }

  @Provides
  @Singleton
  public @NotNull SessionFactory provideSessionFactory() {
    try {
      // The SessionFactory must be built only once for application lifecycle
      Configuration configuration = new Configuration();

      configuration.setProperty(AvailableSettings.URL, jdbcUrl.asStringUrl());
      configuration.setProperty(AvailableSettings.USER, pluginConfig.getDatabase().getUsername());
      configuration.setProperty(AvailableSettings.PASS, pluginConfig.getDatabase().getPassword());
      configuration.setProperty(
          AvailableSettings.CONNECTION_PROVIDER,
          "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
      configuration.setProperty(AvailableSettings.DRIVER, "org.mariadb.jdbc.Driver");
      configuration.setProperty(AvailableSettings.DATASOURCE, "org.mariadb.jdbc.MariaDbDataSource");
      configuration.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.MariaDBDialect");
      configuration.setProperty(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");
      configuration.setProperty(AvailableSettings.SHOW_SQL, "false");
      configuration.setProperty(AvailableSettings.FORMAT_SQL, "false");
      configuration.setProperty(AvailableSettings.HBM2DDL_AUTO, "update");
      configuration.setProperty(AvailableSettings.HBM2DDL_CHARSET_NAME, "UTF-8");
      // TODO: cache properties definition

      configuration.setProperty("hibernate.hikari.maximumPoolSize", "10");
      configuration.setProperty("hibernate.hikari.minimumIdle", "5");
      // Because plugin is mono-thread only one SQL request is dispatched at the same time, so there
      // isn't any concurrency with the database. It's why serializable transaction isolation is
      // actually the preference to ensure the best isolation as possible.
      configuration.setProperty(
          "hibernate.hikari.transactionIsolation", "TRANSACTION_SERIALIZABLE");

      configuration.addAnnotatedClass(RankChallengeProgression.class);

      logger.info("Database connexion established.");
      return configuration.buildSessionFactory();
    } catch (HibernateException e) {
      throw new RankUpChallengesRuntimeException(
          String.format("Database connection failed: %s", jdbcUrl.asStringUrl()), e);
    }
  }

  @Provides
  @Singleton
  public @NotNull Track provideTrack() throws RankUpChallengesException {
    String trackName = pluginConfig.getRankUp().getLuckPermsTrackName();
    Track track = luckPerms.getTrackManager().getTrack(trackName);

    if (track == null) {
      throw new RankUpChallengesException(
          String.format(
              "Failed to found the LuckPerms' track '%1$s': is it the right name?", trackName));
    }

    return track;
  }
}
