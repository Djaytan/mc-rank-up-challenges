package fr.voltariuss.diagonia.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.voltariuss.diagonia.model.config.PluginConfig;
import fr.voltariuss.diagonia.model.config.RankConfig;
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
   * @param pluginConfig The plugin configuration.
   */
  public static void inject(
      @NotNull JavaPlugin plugin,
      @NotNull PluginConfig pluginConfig,
      @NotNull RankConfig rankConfig) {
    Injector injector =
        Guice.createInjector(
            new GuiceGeneralModule(plugin.getSLF4JLogger(), plugin, pluginConfig, rankConfig),
            new GuiceBukkitModule(plugin),
            new GuiceBukkitLibsModule(plugin),
            new GuiceLuckPermsModule());
    injector.injectMembers(plugin);
  }
}
