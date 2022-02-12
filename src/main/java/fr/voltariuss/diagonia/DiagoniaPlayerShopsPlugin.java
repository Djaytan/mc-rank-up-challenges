package fr.voltariuss.diagonia;

import org.bukkit.plugin.java.JavaPlugin;

/** Diagonia-PlayerShops plugin */
public class DiagoniaPlayerShopsPlugin extends JavaPlugin {

  // TODO: tmp, use config file instead
  public static final boolean IS_DEBUG_MODE = true;

  @Override
  public void onEnable() {
    getSLF4JLogger().info("§bThis plugin has been developed by §6Voltariuss");
    
    // Guice setup
    DiagoniaPlayerShopsInjector.inject(this, IS_DEBUG_MODE);
    getSLF4JLogger().info("§aPlugin successfully enabled");
  }

  @Override
  public void onDisable() {
    getSLF4JLogger().info("§aPlugin successfully disabled");
  }
}
