package fr.voltariuss.diagonia;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import fr.voltariuss.diagonia.model.dao.PlayerShopDao;
import fr.voltariuss.diagonia.model.dao.PlayerShopDaoImpl;
import javax.inject.Named;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Objects;

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
      Configuration configuration = new Configuration().configure();
      configuration.setProperty("connection.url", connectionUrl);
      configuration.setProperty(
          "connection.username", pluginConfig.getDatabaseConfig().getUsername());
      configuration.setProperty(
          "connection.password", pluginConfig.getDatabaseConfig().getPassword());
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
