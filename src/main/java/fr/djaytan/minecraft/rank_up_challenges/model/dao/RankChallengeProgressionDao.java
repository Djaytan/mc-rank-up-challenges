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

package fr.djaytan.minecraft.rank_up_challenges.model.dao;

import com.google.common.base.Preconditions;
import fr.djaytan.minecraft.rank_up_challenges.model.entity.RankChallengeProgression;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.Material;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankChallengeProgressionDao extends AbstractJpaDao<RankChallengeProgression, Long> {

  @Inject
  public RankChallengeProgressionDao(@NotNull SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public @NotNull Optional<RankChallengeProgression> find(
      @NotNull UUID playerUuid, @NotNull String rankId, @NotNull Material material) {
    Preconditions.checkNotNull(playerUuid);
    Preconditions.checkNotNull(rankId);
    Preconditions.checkNotNull(material);

    return getCurrentSession()
        .createQuery(
            "SELECT rcp FROM RankChallengeProgression rcp WHERE rcp.playerUuid = :playerUuid AND"
                + " rcp.rankId = :rankId AND rcp.challengeMaterial = :challengeMaterial",
            RankChallengeProgression.class)
        .setParameter("playerUuid", playerUuid)
        .setParameter("rankId", rankId)
        .setParameter("challengeMaterial", material)
        .uniqueResultOptional();
  }

  public @NotNull List<RankChallengeProgression> find(
      @NotNull UUID playerUuid, @NotNull String rankId) {
    Preconditions.checkNotNull(playerUuid);
    Preconditions.checkNotNull(rankId);

    return getCurrentSession()
        .createQuery(
            "SELECT rcp FROM RankChallengeProgression rcp WHERE rcp.playerUuid = :playerUuid AND"
                + " rcp.rankId = :rankId",
            RankChallengeProgression.class)
        .setParameter("playerUuid", playerUuid)
        .setParameter("rankId", rankId)
        .list();
  }
}
