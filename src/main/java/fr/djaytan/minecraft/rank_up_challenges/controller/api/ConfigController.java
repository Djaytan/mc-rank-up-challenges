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
