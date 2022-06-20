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

package fr.djaytan.minecraft.rank_up_challenges.controller.api;

import fr.djaytan.minecraft.rank_up_challenges.model.config.ConfigFile;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.PluginConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.challenge.ChallengeConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.RankConfig;
import org.jetbrains.annotations.NotNull;

// TODO: violation of open-closed principle
public interface ConfigController {

  <T> @NotNull T loadConfig(@NotNull ConfigFile configFile, @NotNull Class<T> clazz);

  @NotNull
  ChallengeConfig loadChallengeConfig();

  @NotNull
  PluginConfig loadPluginConfig();

  @NotNull
  RankConfig loadRankConfig();

  void saveDefaultConfigs();
}
