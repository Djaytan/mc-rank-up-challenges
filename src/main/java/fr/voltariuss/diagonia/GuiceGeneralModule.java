package fr.voltariuss.diagonia;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/** General Guice module. */
public class GuiceGeneralModule extends AbstractModule {

  @Provides
  @Singleton
  public SessionFactory provideSessionFactory() {
    // The SessionFactory must be built only once for application lifecycle
    return new Configuration().configure().buildSessionFactory();
  }
}
