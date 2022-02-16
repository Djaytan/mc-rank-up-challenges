package fr.voltariuss.diagonia;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import fr.voltariuss.diagonia.model.dao.PlayerShopDao;
import fr.voltariuss.diagonia.model.dao.PlayerShopDaoImpl;
import java.util.Objects;
import javax.inject.Named;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.jetbrains.annotations.NotNull;
import org.mariadb.jdbc.MariaDbDataSource;
import org.slf4j.Logger;

/** General Guice module. */
public class GuiceGeneralModule extends AbstractModule {

  private final Logger logger;
  private final boolean debugMode;
  private final PluginConfig pluginConfig;

  /**
   * Constructor.
   *
   * @param logger The logger.
   * @param debugMode "true" if debug mode is enabled, "false" otherwise.
   * @param pluginConfig The plugin configuration.
   */
  public GuiceGeneralModule(
      @NotNull Logger logger, boolean debugMode, @NotNull PluginConfig pluginConfig) {
    this.logger = logger;
    this.debugMode = debugMode;
    this.pluginConfig = pluginConfig;
  }

  @Override
  public void configure() {
    bind(PlayerShopDao.class).to(PlayerShopDaoImpl.class);
  }

  @Provides
  @Named("debugMode")
  public boolean provideDebugMode() {
    logger.info("§bDebug mode: {}", debugMode ? "§aenabled" : "§7disabled");
    return debugMode;
  }

  @Provides
  @Singleton
  public @NotNull PluginConfig providePluginConfig() {
    return pluginConfig;
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
      configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
      configuration.setProperty(AvailableSettings.DRIVER, "org.mariadb.jdbc.Driver");
      configuration.setProperty(AvailableSettings.DATASOURCE, "org.mariadb.jdbc.MariaDbDataSource");
      configuration.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.MariaDB103Dialect");
      configuration.setProperty(
          AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");
      configuration.setProperty(AvailableSettings.SHOW_SQL, "true"); // TODO: maybe disable it for prod?
      configuration.setProperty(AvailableSettings.FORMAT_SQL, "true");
      configuration.setProperty(
        AvailableSettings.HBM2DDL_AUTO, "create-drop"); // TODO: change it to "validate" for prod!!!
      configuration.setProperty(AvailableSettings.HBM2DDL_CHARSET_NAME, "UTF-8");
      sessionFactory = configuration.buildSessionFactory();
      logger.info("Database connexion established.");
    } catch (HibernateException e) {
      CriticalErrorHandler criticalErrorHandler = new CriticalErrorHandler(logger);
      criticalErrorHandler.raiseCriticalError(
          String.format("Database connection failed: %s", connectionUrl), e);
    }
    return Objects.requireNonNull(sessionFactory);
  }
}
