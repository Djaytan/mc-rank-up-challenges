package fr.voltariuss.diagonia;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.inject.Singleton;

@Singleton
public class CriticalErrorHandler {

  private final Logger logger;

  public CriticalErrorHandler(@NotNull Logger logger) {
    this.logger = logger;
  }

  public void raiseCriticalError(@NotNull String message, @Nullable Throwable cause) {
    logger.error(message, cause);
    logger.error("A critical error has been raised! Disabling plugin...");
    Bukkit.getServer().getPluginManager().disablePlugin(JavaPlugin.getPlugin(DiagoniaPlayerShopsPlugin.class));
  }
}
