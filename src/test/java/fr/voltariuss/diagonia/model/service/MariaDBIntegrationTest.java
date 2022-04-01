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

package fr.voltariuss.diagonia.model.service;

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
