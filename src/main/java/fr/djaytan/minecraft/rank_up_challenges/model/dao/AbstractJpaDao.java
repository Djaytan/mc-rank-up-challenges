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

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract JPA DAO class to be inherited by entities' DAO classes.
 *
 * <p>The purpose of this class is to make generic the management of sessions and transactions
 * without the need to set up a utility class or duplicate the logic over each DAO class.
 *
 * <p>The management or sessions and transactions is in charge of the service layer. The DAO layer
 * must only call the {@link #getCurrentSession()} method and that's it.
 *
 * @param <T> The entity type.
 * @param <I> The ID type of the entity.
 */
public abstract class AbstractJpaDao<T, I extends Serializable> implements JpaDao<T, I> {

  private final SessionFactory sessionFactory;
  private Session currentSession = null;
  private final Class<T> persistentClass;

  /**
   * Constructor.
   *
   * @param sessionFactory The session factory.
   */
  @SuppressWarnings({"MoveFieldAssignmentToInitializer", "unchecked"})
  protected AbstractJpaDao(@NotNull SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
    this.persistentClass =
        (Class<T>)
            ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }

  /**
   * Gets the current session recently opened.
   *
   * <p>A session must be opened before calling this method.
   *
   * @return The current session recently opened.
   * @throws JpaDaoException If no session have been opened before recovering one.
   */
  protected @NotNull Session getCurrentSession() throws JpaDaoException {
    if (currentSession == null) {
      throw new JpaDaoException("A session must be opened before querying database.");
    }
    return currentSession;
  }

  @Override
  public @NotNull Transaction beginTransaction() throws JpaDaoException {
    if (currentSession == null) {
      throw new JpaDaoException("A session must be opened before beginning a transaction.");
    }
    if (currentSession.getTransaction().isActive()) {
      throw new JpaDaoException("Only one transaction can be initiated for a given session.");
    }
    return currentSession.beginTransaction();
  }

  @Override
  public void openSession() throws JpaDaoException {
    if (currentSession != null) {
      throw new JpaDaoException(
          "Only one session can be up at the same time. Close the previous session before opening"
              + " another one.");
    }
    currentSession = sessionFactory.openSession();
  }

  @Override
  public void destroySession() throws JpaDaoException {
    if (currentSession == null || !currentSession.isOpen()) {
      throw new JpaDaoException("A session must be opened before destroyed.");
    }
    currentSession.close();
    currentSession = null;
  }

  @Override
  public void persist(@NotNull T entity) {
    Preconditions.checkNotNull(entity);
    getCurrentSession().persist(entity);
  }

  @Override
  public void update(@NotNull T entity) {
    Preconditions.checkNotNull(entity);
    getCurrentSession().merge(entity);
  }

  @Override
  public @NotNull Optional<T> findById(@NotNull I id) {
    Preconditions.checkNotNull(id);
    return Optional.ofNullable(getCurrentSession().get(persistentClass, id));
  }

  @Override
  public void delete(@NotNull T entity) {
    Preconditions.checkNotNull(entity);
    getCurrentSession().delete(entity);
  }

  @Override
  public @NotNull List<T> findAll() {
    return getCurrentSession()
        .createQuery(String.format("FROM %1$s", persistentClass.getSimpleName()), persistentClass)
        .list();
  }
}
