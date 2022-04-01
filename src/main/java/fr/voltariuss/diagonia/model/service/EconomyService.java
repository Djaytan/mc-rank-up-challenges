package fr.voltariuss.diagonia.model.service;

import fr.voltariuss.diagonia.model.dto.response.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

// TODO: doc
public interface EconomyService {

  double getBalance(@NotNull OfflinePlayer offlinePlayer);

  @NotNull
  EconomyResponse withdraw(@NotNull OfflinePlayer offlinePlayer, double amount)
      throws EconomyException;

  boolean isAffordable(@NotNull Player player, double price);
}
