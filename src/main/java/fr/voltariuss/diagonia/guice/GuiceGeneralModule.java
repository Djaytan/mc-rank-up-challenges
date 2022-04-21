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

package fr.voltariuss.diagonia.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import fr.voltariuss.diagonia.CriticalErrorHandler;
import fr.voltariuss.diagonia.controller.MessageController;
import fr.voltariuss.diagonia.controller.MessageControllerImpl;
import fr.voltariuss.diagonia.controller.playershop.PlayerShopConfigController;
import fr.voltariuss.diagonia.controller.playershop.PlayerShopConfigControllerImpl;
import fr.voltariuss.diagonia.controller.playershop.PlayerShopController;
import fr.voltariuss.diagonia.controller.playershop.PlayerShopControllerImpl;
import fr.voltariuss.diagonia.controller.playershop.PlayerShopListController;
import fr.voltariuss.diagonia.controller.playershop.PlayerShopListControllerImpl;
import fr.voltariuss.diagonia.controller.rankup.RankUpChallengesController;
import fr.voltariuss.diagonia.controller.rankup.RankUpChallengesControllerImpl;
import fr.voltariuss.diagonia.controller.rankup.RankUpController;
import fr.voltariuss.diagonia.controller.rankup.RankUpControllerImpl;
import fr.voltariuss.diagonia.model.config.PluginConfig;
import fr.voltariuss.diagonia.model.config.rank.RankConfig;
import fr.voltariuss.diagonia.model.dao.PlayerShopDao;
import fr.voltariuss.diagonia.model.dao.PlayerShopDaoImpl;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import fr.voltariuss.diagonia.model.service.EconomyService;
import fr.voltariuss.diagonia.model.service.EconomyVaultService;
import fr.voltariuss.diagonia.model.service.JobsRebornService;
import fr.voltariuss.diagonia.model.service.JobsService;
import fr.voltariuss.diagonia.model.service.RankLuckPermsService;
import fr.voltariuss.diagonia.model.service.RankService;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.inject.Named;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/** General Guice module. */
public class GuiceGeneralModule extends AbstractModule {

  private final Logger logger;
  private final JavaPlugin javaPlugin;
  private final PluginConfig pluginConfig;
  private final RankConfig rankConfig;

  /**
   * Constructor.
   *
   * @param logger The logger.
   * @param javaPlugin The Java Plugin.
   * @param pluginConfig The plugin configuration.
   */
  public GuiceGeneralModule(
      @NotNull Logger logger,
      @NotNull JavaPlugin javaPlugin,
      @NotNull PluginConfig pluginConfig,
      @NotNull RankConfig rankConfig) {
    this.logger = logger;
    this.javaPlugin = javaPlugin;
    this.pluginConfig = pluginConfig;
    this.rankConfig = rankConfig;
  }

  @Override
  public void configure() {
    bind(EconomyService.class).to(EconomyVaultService.class);
    bind(JobsService.class).to(JobsRebornService.class);
    bind(MessageController.class).to(MessageControllerImpl.class);
    bind(PlayerShopController.class).to(PlayerShopControllerImpl.class);
    bind(PlayerShopConfigController.class).to(PlayerShopConfigControllerImpl.class);
    bind(PlayerShopDao.class).to(PlayerShopDaoImpl.class);
    bind(PlayerShopListController.class).to(PlayerShopListControllerImpl.class);
    bind(RankService.class).to(RankLuckPermsService.class);
    bind(RankUpController.class).to(RankUpControllerImpl.class);
    bind(RankUpChallengesController.class).to(RankUpChallengesControllerImpl.class);
  }

  @Provides
  @Named("debugMode")
  public boolean provideDebugMode() {
    logger.info("§bDebug mode: {}", pluginConfig.isDebugMode() ? "§aenabled" : "§7disabled");
    return pluginConfig.isDebugMode();
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
  public @NotNull SessionFactory provideSessionFactory() {
    SessionFactory sessionFactory = null;
    String connectionUrl =
        String.format(
            "jdbc:mariadb://%s:%d/%s",
            pluginConfig.getDatabaseConfig().getHost(),
            pluginConfig.getDatabaseConfig().getPort(),
            pluginConfig.getDatabaseConfig().getDatabase());
    try {
      // The SessionFactory must be built only once for application lifecycle
      Configuration configuration = new Configuration();

      configuration.setProperty(AvailableSettings.URL, connectionUrl);
      configuration.setProperty(
          AvailableSettings.USER, pluginConfig.getDatabaseConfig().getUsername());
      configuration.setProperty(
          AvailableSettings.PASS, pluginConfig.getDatabaseConfig().getPassword());
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

      configuration.addAnnotatedClass(PlayerShop.class);
      configuration.addAnnotatedClass(RankChallengeProgression.class);

      sessionFactory = configuration.buildSessionFactory();
      logger.info("Database connexion established.");
    } catch (HibernateException e) {
      CriticalErrorHandler criticalErrorHandler =
          new CriticalErrorHandler(logger, javaPlugin.getServer().getPluginManager(), javaPlugin);
      criticalErrorHandler.raiseCriticalError(
          String.format("Database connection failed: %s", connectionUrl), e);
    }
    return Objects.requireNonNull(sessionFactory);
  }

  @Provides
  @Singleton
  public @NotNull ResourceBundle provideResourceBundle() {
    return ResourceBundle.getBundle("diagonia", Locale.FRANCE);
  }
}
