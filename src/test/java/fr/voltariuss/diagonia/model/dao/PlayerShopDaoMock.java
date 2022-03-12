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
