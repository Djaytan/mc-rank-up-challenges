package fr.voltariuss.diagonia;

import javax.inject.Inject;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public class DiagoniaPlayerShopsPlugin extends JavaPlugin {

  @Inject private Logger slf4jLogger;

  @Override
  public void onEnable() {
    // Guice setup
    DiagoniaPlayerShopsInjector.inject(this);
    slf4jLogger.info("§aPlugin successfully enabled!");
  }
}
