/*
 * Rank-Up challenges plugin for Minecraft (Bukkit servers)
 * Copyright (C) 2022 - Loïc DUBOIS-TERMOZ
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.djaytan.minecraft.rank_up_challenges.model.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

public interface JpaDao<T, I extends Serializable> {

  /**
   * Opens a new session.
   *
   * <p>Only one session can be up at the same time.
   *
   * @throws JpaDaoException If a session still opened at creation time of another one.
   */
  void openSession() throws JpaDaoException;

  /**
   * Begins a transaction for the current session.
   *
   * <p>Must be used after a call to {@link #openSession()} and only one transaction can up at the
   * same time for a session.
   *
   * @return The began transaction for the current session.
   * @throws JpaDaoException If no session have been opened before beginning a transaction.
   * @throws JpaDaoException If another transaction is active yet.
   */
  @NotNull
  Transaction beginTransaction() throws JpaDaoException;

  /**
   * Destroys the current session.
   *
   * <p>A session must be opened before destroying it, otherwise it is considered as an error.
   *
   * @throws JpaDaoException If no session still opened at destroy time.
   */
  void destroySession() throws JpaDaoException;

  void persist(@NotNull T entity);

  void update(@NotNull T entity);

  @NotNull
  Optional<T> findById(@NotNull I id);

  void delete(@NotNull T entity);

  @NotNull
  List<T> findAll();
}
