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

package fr.djaytan.minecraft.rank_up_challenges.model.service.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface JobsService {

  /**
   * Gets the total levels of all jobs exercised the specified player.
   *
   * @param player The player.
   * @return The total levels of all jobs exercised by the specified player.
   * @apiNote Jobs with levels when there are not actually exercised by the player are not taken
   *     into account.
   */
  int getTotalLevels(@NotNull Player player);
}
