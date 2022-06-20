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

package fr.djaytan.minecraft.rank_up_challenges.model.service;

import fr.djaytan.minecraft.rank_up_challenges.model.service.api.RankUpService;
import fr.djaytan.minecraft.rank_up_challenges.AbstractBaseTest;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.challenge.ChallengeType;
import fr.djaytan.minecraft.rank_up_challenges.model.entity.RankChallengeProgression;
import java.util.UUID;
import javax.inject.Inject;
import junit.framework.Assert;
import org.bukkit.Material;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Rank up service")
class RankUpServiceTest extends AbstractBaseTest {

  @Inject
  RankUpService rankUpService;

  @Test
  void givenNewRankChallengeProgression_WhenPersisted_ThenShouldBeRegisteredIntoDatabase() {
    RankChallengeProgression rcp =
        RankChallengeProgression.builder()
            .playerUuid(UUID.randomUUID())
            .rankId("test-rank")
            .difficultyTier(1)
            .challengeType(ChallengeType.COOK)
            .challengeMaterial(Material.OAK_LOG)
            .challengeAmountRequired(100)
            .challengeAmountGiven(10)
            .build();

    rankUpService.persistChallengeProgression(rcp);

    RankChallengeProgression retrievedRcp =
        rankUpService.findChallengeProgressionById(rcp.getId()).orElse(null);
    Assert.assertNotNull(retrievedRcp);
    Assert.assertEquals(rcp, retrievedRcp);
  }
}
