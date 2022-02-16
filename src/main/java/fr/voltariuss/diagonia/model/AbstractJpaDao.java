package fr.voltariuss.diagonia.model;

import java.io.Serializable;
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
 * The management or sessions and transactions is in charge of the service layer. The DAO layer must
 * only call the {@link #getCurrentSession()} method and that's it.
 *
 * @param <T> The entity type.
 * @param <I> The ID type of the entity.
 */
public abstract class AbstractJpaDao<T, I extends Serializable> implements JpaDao<T, I> {

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
}
