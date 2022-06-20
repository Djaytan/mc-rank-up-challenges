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

package fr.djaytan.minecraft.rank_up_challenges.plugin;

import fr.djaytan.minecraft.rank_up_challenges.controller.listener.PlayerJoinListener;
import javax.inject.Inject;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public class ListenerRegister {

  private final Plugin plugin;
  private final PluginManager pluginManager;

  private final PlayerJoinListener playerJoinListener;

  @Inject
  public ListenerRegister(
      @NotNull Plugin plugin,
      @NotNull PluginManager pluginManager,
      @NotNull PlayerJoinListener playerJoinListener) {
    this.plugin = plugin;
    this.pluginManager = pluginManager;

    this.playerJoinListener = playerJoinListener;
  }

  public void registerListeners() {
    pluginManager.registerEvents(playerJoinListener, plugin);
  }
}
