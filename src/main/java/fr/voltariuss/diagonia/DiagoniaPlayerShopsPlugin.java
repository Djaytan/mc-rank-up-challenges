package fr.voltariuss.diagonia;

import javax.inject.Inject;

import fr.voltariuss.diagonia.model.service.PluginConfigService;
import fr.voltariuss.diagonia.model.config.PluginConfig;
import fr.voltariuss.diagonia.model.service.RankService;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;

import java.io.File;

/** Diagonia-PlayerShops plugin */
public class DiagoniaPlayerShopsPlugin extends JavaPlugin {

  @Inject private SessionFactory sessionFactory;
  @Inject private CommandRegister commandRegister;
  @Inject private PrerequisitesValidation prerequisitesValidation;
  @Inject private RankService rankService;

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

    if (pluginConfig.getDatabaseConfig().isEnabled()) {
      // Guice setup
      DiagoniaPlayerShopsInjector.inject(this, pluginConfig);

      // Additional setup
      rankService.prepareDataFile();
      prerequisitesValidation.validate();
      commandRegister.registerCommands();

      getSLF4JLogger().info("Plugin successfully enabled");
    } else {
      getSLF4JLogger()
          .error("Database disabled. Please configure and activate it through config.yml file");
      getServer().getPluginManager().disablePlugin(this);
    }
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
