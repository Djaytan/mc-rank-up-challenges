/*
 * Rank-Up challenges plugin for Minecraft (Bukkit servers)
 * Copyright (C) 2022 - Loïc DUBOIS-TERMOZ
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.djaytan.minecraft.rank_up_challenges.plugin.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.challenge.ChallengeConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.PluginConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.RankConfig;
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
            new GuicePluginModule(
                challengeConfig, plugin.getSLF4JLogger(), luckPerms, pluginConfig, rankConfig));
    injector.injectMembers(plugin);
  }
}
