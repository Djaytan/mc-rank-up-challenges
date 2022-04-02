/*
 * Copyright (c) 2022 - Loïc DUBOIS-TERMOZ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.voltariuss.diagonia.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.voltariuss.diagonia.model.config.PluginConfig;
import fr.voltariuss.diagonia.model.config.rank.RankConfig;
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
            new GuiceLuckPermsModule(pluginConfig));
    injector.injectMembers(plugin);
  }
}
