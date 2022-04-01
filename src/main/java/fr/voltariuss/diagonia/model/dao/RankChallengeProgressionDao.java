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

package fr.voltariuss.diagonia.model.dao;

import com.google.common.base.Preconditions;
import fr.voltariuss.diagonia.model.AbstractJpaDao;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
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
            "FROM RankChallengeProgression rcp WHERE rcp.playerUuid = :playerUuid AND rcp.rankId ="
                + " :rankId AND rcp.challengeMaterial = :challengeMaterial",
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
            "FROM RankChallengeProgression rcp WHERE rcp.playerUuid = :playerUuid AND rcp.rankId ="
                + " :rankId",
            RankChallengeProgression.class)
        .setParameter("playerUuid", playerUuid)
        .setParameter("rankId", rankId)
        .list();
  }
}
