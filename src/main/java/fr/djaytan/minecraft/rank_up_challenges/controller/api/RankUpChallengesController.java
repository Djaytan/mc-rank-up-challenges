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

import fr.djaytan.minecraft.rank_up_challenges.model.service.api.dto.GiveActionType;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.dto.RankUpProgression;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.Rank;
import fr.djaytan.minecraft.rank_up_challenges.model.entity.RankChallengeProgression;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface RankUpChallengesController {

  void giveItemChallenge(
      @NotNull Player targetPlayer,
      @NotNull Rank rank,
      @NotNull Material challengeMaterial,
      @NotNull GiveActionType giveActionType,
      int nbItemsInInventory);

  @NotNull
  Optional<RankChallengeProgression> findChallenge(
      @NotNull UUID playerUuid, @NotNull String rankId, @NotNull Material material);

  void onRankUpRequested(
      @NotNull Player player, @NotNull Rank rank, @NotNull RankUpProgression rankUpProgression);

  void prepareRankChallenges(@NotNull Player player);
}
