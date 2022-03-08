package fr.voltariuss.diagonia.model.service;

import com.google.common.base.Preconditions;
import fr.voltariuss.diagonia.model.EconomyFormatter;
import fr.voltariuss.diagonia.model.dto.EconomyBalanceDto;
import fr.voltariuss.diagonia.model.dto.response.EconomyResponse;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

@Singleton
public class EconomyVaultService implements EconomyService {

  private final Economy economy;
  private final EconomyFormatter economyFormatter;

  @Inject
  public EconomyVaultService(@NotNull Economy economy, @NotNull EconomyFormatter economyFormatter) {
    this.economy = economy;
    this.economyFormatter = economyFormatter;
  }

  @Override
  public @NotNull EconomyBalanceDto getBalance(@NotNull OfflinePlayer offlinePlayer) {
    double amount = economy.getBalance(offlinePlayer);
    String formattedAmount = economyFormatter.format(amount);
    return EconomyBalanceDto.builder().amount(amount).formattedAmount(formattedAmount).build();
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
