package fr.voltariuss.diagonia.model.dao;

import fr.voltariuss.diagonia.model.JpaDao;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface PlayerShopDao extends JpaDao<PlayerShop, Long> {

  Optional<PlayerShop> findByUuid(@NotNull UUID uuid);
}
