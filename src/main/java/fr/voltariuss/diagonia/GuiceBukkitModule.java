package fr.voltariuss.diagonia;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import javax.inject.Named;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.structure.StructureManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/** Guice module for Bukkit plugin. */
public class GuiceBukkitModule extends AbstractModule {

  private final JavaPlugin plugin;
  private final Logger logger;
  private final boolean debugMode;

  /**
   * Constructor.
   *
   * @param plugin The Bukkit plugin.
   * @param debugMode "true" if debug mode is enabled, "false" otherwise.
   */
  public GuiceBukkitModule(JavaPlugin plugin, Logger logger, boolean debugMode) {
    this.plugin = plugin;
    this.logger = logger;
    this.debugMode = debugMode;
  }

  @Provides
  @Named("debugMode")
  public boolean provideDebugMode() {
    logger.info("§bDebug mode: {}", debugMode ? "§aenabled" : "§7disabled");
    return debugMode;
  }

  @Provides
  @Singleton
  public @NotNull Logger provideLogger() {
    return logger;
  }

  @Provides
  @Singleton
  public @NotNull PluginManager providePluginManager() {
    return plugin.getServer().getPluginManager();
  }

  @Provides
  @Singleton
  public @NotNull ItemFactory provideItemFactory() {
    return plugin.getServer().getItemFactory();
  }

  @Provides
  @Singleton
  public @NotNull ConsoleCommandSender provideConsoleCommandSender() {
    return plugin.getServer().getConsoleSender();
  }

  @Provides
  @Singleton
  public @NotNull BukkitScheduler provideBukkitScheduler() {
    return plugin.getServer().getScheduler();
  }

  @Provides
  @Singleton
  public @NotNull ScoreboardManager provideScoreboardManager() {
    return plugin.getServer().getScoreboardManager();
  }

  @Provides
  @Singleton
  public @NotNull StructureManager provideStructureManager() {
    return plugin.getServer().getStructureManager();
  }

  @Provides
  @Singleton
  public @NotNull PaperCommandManager providePaperCommandManager() {
    return new PaperCommandManager(plugin);
  }
}
