package fr.voltariuss.diagonia;

import javax.inject.Inject;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;

/** Diagonia-PlayerShops plugin */
public class DiagoniaPlayerShopsPlugin extends JavaPlugin {

  @Inject private SessionFactory sessionFactory;
  @Inject private CommandRegister commandRegister;
  @Inject private PrerequisitesValidation prerequisitesValidation;

  @Override
  public void onEnable() {
    getSLF4JLogger().info("This plugin has been developed by Voltariuss");

    // Configuration initialization
    ConfigurationManager.init(getConfig());
    getConfig().options().copyDefaults(true);
    saveConfig();
    PluginConfig pluginConfig = ConfigurationManager.loadConfig(getConfig());
    getSLF4JLogger().info("Configuration loaded");

    // Guice setup
    DiagoniaPlayerShopsInjector.inject(this, pluginConfig);

    // Additional setup
    prerequisitesValidation.validate();
    commandRegister.registerCommands();

    getSLF4JLogger().info("Plugin successfully enabled");
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
