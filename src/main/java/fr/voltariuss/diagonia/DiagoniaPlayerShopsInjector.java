package fr.voltariuss.diagonia;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/** Guice injector for Bukkit plugin. */
public final class DiagoniaPlayerShopsInjector {

  /** Private constructor. */
  private DiagoniaPlayerShopsInjector() {}

  /**
   * Inject already instantiated stuff into Guice (e.g. {@link JavaPlugin}).
   *
   * @param plugin The plugin to inject into Guice.
   * @param debugMode "true" if the debug mode for the plugin is enabled, "false" otherwise.
   */
  public static void inject(@NotNull JavaPlugin plugin, boolean debugMode) {
    Injector injector =
        Guice.createInjector(
            new GuiceGeneralModule(plugin.getSLF4JLogger(), debugMode),
            new GuiceBukkitModule(plugin, plugin.getSLF4JLogger()));
    injector.injectMembers(plugin);
  }
}
