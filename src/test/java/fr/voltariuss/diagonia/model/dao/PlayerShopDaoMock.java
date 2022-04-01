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

package fr.voltariuss.diagonia.model.dao;

import fr.voltariuss.diagonia.model.JpaDaoException;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mockito.Mockito;

public class PlayerShopDaoMock implements PlayerShopDao {

  @Override
  public void openSession() throws JpaDaoException {
    // do nothing
  }

  @Override
  public @NotNull Transaction beginTransaction() throws JpaDaoException {
    return Mockito.mock(Transaction.class);
  }

  @Override
  public void destroySession() throws JpaDaoException {
    // do nothing
  }

  @Override
  public void persist(@NotNull PlayerShop entity) {
    // do nothing
  }

  @Override
  public void update(@NotNull PlayerShop entity) {
    // do nothing
  }

  @Override
  public @NotNull Optional<PlayerShop> findById(@Nullable Long id) {
    return Optional.of(Mockito.mock(PlayerShop.class));
  }

  @Override
  public void delete(@NotNull PlayerShop entity) {
    // do nothing
  }

  @Override
  public @NotNull List<PlayerShop> findAll() {
    return new ArrayList<>();
  }

  @Override
  public Optional<PlayerShop> findByUuid(@NotNull UUID uuid) {
    return Optional.of(Mockito.mock(PlayerShop.class));
  }
}
