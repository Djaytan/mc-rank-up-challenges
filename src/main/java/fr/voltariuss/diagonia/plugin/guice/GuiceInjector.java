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

package fr.voltariuss.diagonia.plugin.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.voltariuss.diagonia.model.config.data.PluginConfig;
import fr.voltariuss.diagonia.model.config.data.challenge.ChallengeConfig;
import fr.voltariuss.diagonia.model.config.data.rank.RankConfig;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/** Guice injector for Bukkit plugin. */
public final class GuiceInjector {

  private GuiceInjector() {}

  public static void inject(
      @NotNull JavaPlugin plugin,
      @NotNull ChallengeConfig challengeConfig,
      @NotNull PluginConfig pluginConfig,
      @NotNull RankConfig rankConfig) {
    LuckPerms luckPerms = LuckPermsProvider.get();
    Injector injector =
        Guice.createInjector(
            new GuiceBukkitModule(plugin),
            new GuiceBukkitLibsModule(luckPerms, plugin),
            new GuiceGeneralModule(),
            new GuiceDiagoniaModule(
                challengeConfig, plugin.getSLF4JLogger(), luckPerms, pluginConfig, rankConfig));
    injector.injectMembers(plugin);
  }
}
