package fr.voltariuss.diagonia;

import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import javax.inject.Inject;

public class DiagoniaPlayerShopsPlugin extends JavaPlugin {

  @Inject private Logger logger;

  @Override
  public void onEnable() {
    // Guice setup
    DiagoniaPlayerShopsInjector.inject(this);
    logger.info("$aPlugin successfully enabled!");
  }
}
