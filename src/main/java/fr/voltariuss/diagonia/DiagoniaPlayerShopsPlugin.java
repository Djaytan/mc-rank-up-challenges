package fr.voltariuss.diagonia;

import org.bukkit.plugin.java.JavaPlugin;

public class DiagoniaPlayerShopsPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    getLogger().info("Plugin successfully enabled!");
  }

  @Override
  public void onDisable() {
    getLogger().info("Plugin successfully disabled!");
  }
}
