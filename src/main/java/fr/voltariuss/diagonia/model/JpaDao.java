package fr.voltariuss.diagonia.model;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
  Optional<T> findById(@Nullable I id);

  void delete(@NotNull T entity);

  @NotNull
  List<T> findAll();
}
