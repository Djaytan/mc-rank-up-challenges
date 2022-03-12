package fr.voltariuss.diagonia.model.service;

import fr.voltariuss.diagonia.Debugger;
import fr.voltariuss.diagonia.model.dao.PlayerShopDaoImpl;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.UUID;
import javax.inject.Inject;
import junit.framework.Assert;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

// TODO: use Docker to restore this test with CircleCI
//@DisplayName("MariaDB integration test")
//class MariaDBIntegrationTest {
//
//  private final PlayerShopService playerShopService;
//
//  @Inject
//  public MariaDBIntegrationTest() {
//    SessionFactory sessionFactory = setupHibernate();
//    playerShopService =
//      new PlayerShopService(
//        new Debugger(LoggerFactory.getLogger(MariaDBIntegrationTest.class), false),
//        new PlayerShopDaoImpl(sessionFactory));
//  }
//
//  private @NotNull SessionFactory setupHibernate() {
//    Configuration configuration = new Configuration().configure();
//    configuration.setProperty(AvailableSettings.URL, "jdbc:mariadb://localhost:3306/test");
//    configuration.setProperty(AvailableSettings.USER, "user");
//    configuration.setProperty(AvailableSettings.PASS, "user");
//    configuration.setProperty(AvailableSettings.DRIVER, "org.mariadb.jdbc.Driver");
//    configuration.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.MariaDBDialect");
//    configuration.setProperty(AvailableSettings.DATASOURCE, "org.mariadb.jdbc.MariaDbDataSource");
//    configuration.setProperty(
//      AvailableSettings.CONNECTION_PROVIDER,
//      "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
//    return configuration.buildSessionFactory();
//  }
//
//  @Test
//  void persistPlayerShop() {
//    PlayerShop ps = new PlayerShop(UUID.randomUUID());
//    playerShopService.persist(ps);
//    Assert.assertEquals(1, playerShopService.findAll().size());
//  }
//}
