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

package fr.voltariuss.diagonia.model.service;

import fr.voltariuss.diagonia.AbstractBaseTest;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import fr.voltariuss.diagonia.model.service.api.RankUpService;
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
        new RankChallengeProgression(UUID.randomUUID(), "test-rank", Material.OAK_LOG);

    rankUpService.persistChallengeProgression(rcp);

    RankChallengeProgression retrievedRcp =
        rankUpService.findChallengeProgressionById(rcp.getId()).orElse(null);
    Assert.assertNotNull(retrievedRcp);
    Assert.assertEquals(rcp, retrievedRcp);
  }
}
