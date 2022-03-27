package fr.voltariuss.diagonia;

import fr.voltariuss.diagonia.guice.DiagoniaPlayerShopsInjector;
import fr.voltariuss.diagonia.model.RankConfigDeserializer;
import fr.voltariuss.diagonia.model.RankConfigInitializer;
import fr.voltariuss.diagonia.model.config.PluginConfig;
import fr.voltariuss.diagonia.model.config.rank.RankConfig;
import fr.voltariuss.diagonia.model.service.PluginConfigService;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;

/** Diagonia plugin */
public class DiagoniaPlugin extends JavaPlugin {

  @Inject private SessionFactory sessionFactory;
  @Inject private CommandRegister commandRegister;
  @Inject private PrerequisitesValidation prerequisitesValidation;

  @SneakyThrows
  @Override
  public void onEnable() {
    getSLF4JLogger().info("This plugin has been developed by Voltariuss");

    // Configuration initialization
    PluginConfigService.init(getConfig());
    getConfig().options().copyDefaults(true);
    saveConfig();
    PluginConfig pluginConfig = PluginConfigService.loadConfig(getConfig());
    getSLF4JLogger().info("Configuration loaded");

    RankConfigInitializer rankConfigInitializer =
        new RankConfigInitializer(
            getDataFolder(), this, getSLF4JLogger(), new RankConfigDeserializer());
    rankConfigInitializer.init();
    RankConfig rankConfig = rankConfigInitializer.readRankConfig();

    if (pluginConfig.getDatabaseConfig().isEnabled()) {
      // Guice setup
      DiagoniaPlayerShopsInjector.inject(this, pluginConfig, rankConfig);

      // Additional setup
      prerequisitesValidation.validate();
      commandRegister.registerCommands();

      getSLF4JLogger().info("Plugin successfully enabled");
      return;
    }
    getSLF4JLogger()
        .error("Database disabled. Please configure and activate it through config.yml file");
    getServer().getPluginManager().disablePlugin(this);
  }

  @Override
  public void onDisable() {
    if (sessionFactory != null) {
      sessionFactory.close();
    }
    getSLF4JLogger().info("Database connection closed");
    getSLF4JLogger().info("Plugin successfully disabled");
  }
}
