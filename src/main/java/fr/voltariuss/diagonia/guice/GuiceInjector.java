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
import java.util.ResourceBundle;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/** Guice injector for Bukkit plugin. */
public final class GuiceInjector {

  private GuiceInjector() {}

  public static void inject(
      @NotNull MiniMessage miniMessage,
      @NotNull JavaPlugin plugin,
      @NotNull PluginConfig pluginConfig,
      @NotNull RankConfig rankConfig,
      @NotNull ResourceBundle resourceBundle) {
    LuckPerms luckPerms = LuckPermsProvider.get();
    Injector injector =
        Guice.createInjector(
            new GuiceBukkitModule(plugin),
            new GuiceBukkitLibsModule(luckPerms, miniMessage, plugin),
            new GuiceGeneralModule(resourceBundle),
            new GuiceDiagoniaModule(
                plugin.getSLF4JLogger(), luckPerms, plugin, pluginConfig, rankConfig));
    injector.injectMembers(plugin);
  }
}