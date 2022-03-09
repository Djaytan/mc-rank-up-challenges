package fr.voltariuss.diagonia.view;

import net.milkbowl.vault.economy.Economy;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EconomyFormatter {

  private final Economy economy;

  @Inject
  public EconomyFormatter(@NotNull Economy economy) {
    this.economy = economy;
  }

  public @NotNull String format(double amount) {
    return economy.format(amount);
  }
}
