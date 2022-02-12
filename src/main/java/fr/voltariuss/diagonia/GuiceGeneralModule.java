package fr.voltariuss.diagonia;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/** General Guice module. */
public class GuiceGeneralModule extends AbstractModule {

  private final Logger logger;

  public GuiceGeneralModule(@NotNull Logger logger) {
    this.logger = logger;
  }

  @Provides
  @Singleton
  public SessionFactory provideSessionFactory() {
    // The SessionFactory must be built only once for application lifecycle
    SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
    logger.info("Database connexion established.");
    return sessionFactory;
  }
}
