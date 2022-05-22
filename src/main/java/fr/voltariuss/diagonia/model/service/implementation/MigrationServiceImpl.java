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

package fr.voltariuss.diagonia.model.service.implementation;

import fr.voltariuss.diagonia.model.dao.JpaDaoException;
import fr.voltariuss.diagonia.model.dao.MigrationDao;
import fr.voltariuss.diagonia.model.entity.Migration;
import fr.voltariuss.diagonia.model.service.api.MigrationService;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

public class MigrationServiceImpl implements MigrationService {

  private static final String TRANSACTION_ROLLBACK_FAIL_MESSAGE = "Failed to rollback transaction";

  private final MigrationDao migrationDao;

  @Inject
  public MigrationServiceImpl(@NotNull MigrationDao migrationDao) {
    this.migrationDao = migrationDao;
  }

  @Override
  public @NotNull List<Migration> retrieveItems(@NotNull UUID playerUuid, int amount) {
    migrationDao.openSession();
    Transaction tx = migrationDao.beginTransaction();
    try {
      List<Migration> migrationsToRetrieve = migrationDao.findAll().stream().limit(amount).toList();

      for (Migration migration : migrationsToRetrieve) {
        migration.setRetrieved(true);
        migrationDao.persist(migration);
      }
      tx.commit();
      return migrationsToRetrieve;
    } catch (HibernateException e) {
      try {
        tx.rollback();
      } catch (RuntimeException re) {
        throw new JpaDaoException(TRANSACTION_ROLLBACK_FAIL_MESSAGE, re);
      }
      throw e;
    } finally {
      migrationDao.destroySession();
    }
  }
}
