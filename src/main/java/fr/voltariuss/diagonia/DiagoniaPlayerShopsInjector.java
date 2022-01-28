package fr.voltariuss.diagonia;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class DiagoniaPlayerShopsInjector {

  private DiagoniaPlayerShopsInjector() {}

  public static void inject(@NotNull JavaPlugin plugin) {
    Injector injector = Guice.createInjector(new GuiceBukkitModule(plugin));
    injector.injectMembers(plugin);
  }
}
