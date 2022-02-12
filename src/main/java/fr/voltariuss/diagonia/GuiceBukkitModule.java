package fr.voltariuss.diagonia;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.structure.StructureManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.inject.Named;

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
  @NotNull
  public Logger provideLogger() {
    return logger;
  }

  @Provides
  @Singleton
  @NotNull
  public PluginManager providePluginManager() {
    return plugin.getServer().getPluginManager();
  }

  @Provides
  @Singleton
  @NotNull
  public ItemFactory provideItemFactory() {
    return plugin.getServer().getItemFactory();
  }

  @Provides
  @Singleton
  @NotNull
  public ConsoleCommandSender provideConsoleCommandSender() {
    return plugin.getServer().getConsoleSender();
  }

  @Provides
  @Singleton
  @NotNull
  public BukkitScheduler provideBukkitScheduler() {
    return plugin.getServer().getScheduler();
  }

  @Provides
  @Singleton
  @NotNull
  public ScoreboardManager provideScoreboardManager() {
    return plugin.getServer().getScoreboardManager();
  }

  @Provides
  @Singleton
  @NotNull
  public StructureManager provideStructureManager() {
    return plugin.getServer().getStructureManager();
  }

  @Provides
  @Singleton
  @NotNull
  public PaperCommandManager providePaperCommandManager() {
    return new PaperCommandManager(plugin);
  }
}
