package fr.voltariuss.diagonia;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import javax.inject.Named;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/** General Guice module. */
public class GuiceGeneralModule extends AbstractModule {

  private final Logger logger;
  private final boolean debugMode;

  /**
   * Constructor.
   *
   * @param logger The logger.
   * @param debugMode "true" if debug mode is enabled, "false" otherwise.
   */
  public GuiceGeneralModule(@NotNull Logger logger, boolean debugMode) {
    this.logger = logger;
    this.debugMode = debugMode;
  }

  @Provides
  @Named("debugMode")
  public boolean provideDebugMode() {
    logger.info("§bDebug mode: {}", debugMode ? "§aenabled" : "§7disabled");
    return debugMode;
  }

  @Provides
  @Singleton
  public @NotNull SessionFactory provideSessionFactory() {
    // The SessionFactory must be built only once for application lifecycle
    SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
    logger.info("Database connexion established.");
    return sessionFactory;
  }
}
