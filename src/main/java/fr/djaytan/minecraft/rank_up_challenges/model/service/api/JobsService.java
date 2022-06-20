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
