package fr.voltariuss.diagonia.model.dao;

import fr.voltariuss.diagonia.model.JpaDao;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public interface PlayerShopDao extends JpaDao<PlayerShop, Long> {

  Optional<PlayerShop> findByUuid(@NotNull UUID uuid);
}
