package fr.voltariuss.diagonia;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import fr.voltariuss.diagonia.model.dao.PlayerShopDao;
import fr.voltariuss.diagonia.model.dao.PlayerShopDaoMock;
import javax.inject.Named;
import javax.inject.Singleton;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiceGeneralTestModule extends AbstractModule {

  @Override
  public void configure() {
    bind(PlayerShopDao.class).to(PlayerShopDaoMock.class);
  }

  @Provides
  @Singleton
  @Named("debugMode")
  public boolean provideDebugMode() {
    return false;
  }

  @Provides
  @Singleton
  public @NotNull Logger provideSlf4jLogger() {
    return LoggerFactory.getLogger(GuiceGeneralTestModule.class);
  }

  @Provides
  @Singleton
  public @NotNull SessionFactory provideSessionFactory() {
    // The SessionFactory must be built only once for application lifecycle
    return new Configuration().configure().buildSessionFactory();
  }
}
