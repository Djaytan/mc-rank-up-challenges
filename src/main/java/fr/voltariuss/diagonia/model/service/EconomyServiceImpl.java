package fr.voltariuss.diagonia.model.service;

import com.google.common.base.Preconditions;
import fr.voltariuss.diagonia.model.dto.response.EconomyResponse;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

@Singleton
public class EconomyServiceImpl implements EconomyService {

  private final Economy economy;

  @Inject
  public EconomyServiceImpl(@NotNull Economy economy) {
    this.economy = economy;
  }

  @Override
  public double getBalance(@NotNull OfflinePlayer offlinePlayer) {
    return economy.getBalance(offlinePlayer);
  }

  @Override
  public @NotNull EconomyResponse withdraw(@NotNull OfflinePlayer offlinePlayer, double amount)
      throws EconomyException {
    Preconditions.checkArgument(amount >= 0, "The amount to withdraw must be a positive number");

    net.milkbowl.vault.economy.EconomyResponse vaultEconomyResponse =
        economy.withdrawPlayer(offlinePlayer, amount);

    boolean isTransactionFailed =
        vaultEconomyResponse.type.equals(
            net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE);

    if (isTransactionFailed) {
      throw new EconomyException(
          String.format(
              "[Vault] Failed to withdraw money to player %s: %s",
              offlinePlayer.getName(), vaultEconomyResponse.errorMessage));
    }

    boolean isMethodNotImplemented =
        vaultEconomyResponse.type.equals(
            net.milkbowl.vault.economy.EconomyResponse.ResponseType.NOT_IMPLEMENTED);

    if (isMethodNotImplemented) {
      throw new EconomyException("[Vault] Method not implemented");
    }

    return EconomyResponse.builder()
        .modifiedAmount(vaultEconomyResponse.amount)
        .newBalance(vaultEconomyResponse.balance)
        .build();
  }
}
