package fr.voltariuss.diagonia.model;

import java.io.Serializable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: Doc
 *
 * @param <T> The entity type.
 * @param <I> The ID type of the entity.
 */
public abstract class AbstractJpaDao<T, I extends Serializable> implements JpaDaoInterface<T, I> {

  private final SessionFactory sessionFactory;
  private Session currentSession = null;

  /**
   * Constructor.
   *
   * @param sessionFactory The session factory.
   */
  protected AbstractJpaDao(@NotNull SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  /**
   * Gets the current session recently opened.
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

  /**
   * Begins a transaction for the current session.
   *
   * @return The began transaction for the current session.
   * @throws JpaDaoException If no session have been opened before beginning a transaction.
   * @throws JpaDaoException If another transaction is active yet.
   */
  public @NotNull Transaction beginTransaction() throws JpaDaoException {
    if (currentSession == null) {
      throw new JpaDaoException("A session must be opened before beginning a transaction.");
    }
    if (currentSession.getTransaction().isActive()) {
      throw new JpaDaoException("Only one transaction can be initiated for a given session.");
    }
    return currentSession.beginTransaction();
  }

  /**
   * Opens a new session.
   *
   * @throws JpaDaoException If a session still opened at creation time of another one.
   */
  public void openSession() throws JpaDaoException {
    if (currentSession != null) {
      throw new JpaDaoException(
          "Only one session can be up at the same time. Close the previous session before opening"
              + " another one.");
    }
    currentSession = sessionFactory.openSession();
  }

  /**
   * Destroys the current session.
   *
   * @throws JpaDaoException If no session still opened at destroy time.
   */
  public void destroySession() throws JpaDaoException {
    if (currentSession == null || !currentSession.isOpen()) {
      throw new JpaDaoException("A session must be opened before destroyed.");
    }
    currentSession.close();
    currentSession = null;
  }
}
