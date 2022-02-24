package fr.voltariuss.diagonia;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.io.File;
import java.util.Objects;
import javax.inject.Named;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
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

  /**
   * Constructor.
   *
   * @param plugin The Bukkit plugin.
   */
  public GuiceBukkitModule(@NotNull JavaPlugin plugin, @NotNull Logger logger) {
    this.plugin = plugin;
    this.logger = logger;
  }

  @Provides
  @Singleton
  public @NotNull JavaPlugin providePlugin() {
    return plugin;
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
  public @NotNull ServicesManager provideServicesManager() {
    return plugin.getServer().getServicesManager();
  }

  @Provides
  @Singleton
  public @NotNull Server provideServer() {
    return plugin.getServer();
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

  @Provides
  @Singleton
  public @NotNull Economy provideVaultEconomy() {
    RegisteredServiceProvider<Economy> rsp =
        plugin.getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      CriticalErrorHandler criticalErrorHandler =
          new CriticalErrorHandler(logger, plugin.getServer().getPluginManager(), plugin);
      criticalErrorHandler.raiseCriticalError(
          "Failed to found Economy service of Vault dependency.");
    }
    return Objects.requireNonNull(rsp).getProvider();
  }

  @Provides
  @Singleton
  public @NotNull MiniMessage provideMiniMessage() {
    return MiniMessage.miniMessage();
  }

  @Provides
  @Named("dataFolder")
  @Singleton
  public @NotNull File provideDataFolder() {
    return plugin.getDataFolder();
  }

  @Provides
  @Singleton
  public @NotNull LuckPerms provideLuckPerms() {
    return LuckPermsProvider.get();
  }
}
