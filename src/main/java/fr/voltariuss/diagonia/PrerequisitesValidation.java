package fr.voltariuss.diagonia;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PrerequisitesValidation {

  private final CriticalErrorHandler criticalErrorHandler;
  private final Server server;

  @Inject
  public PrerequisitesValidation(
      @NotNull CriticalErrorHandler criticalErrorHandler, @NotNull Server server) {
    this.criticalErrorHandler = criticalErrorHandler;
    this.server = server;
  }

  public void validate() {
    if (!server.getPluginManager().isPluginEnabled("Vault")) {
      criticalErrorHandler.raiseCriticalError("Required Vault dependency not found.");
    }
  }
}
