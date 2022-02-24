package fr.voltariuss.diagonia.model.service;

import fr.voltariuss.diagonia.AbstractBaseTest;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import java.util.UUID;
import javax.inject.Inject;
import junit.framework.Assert;
import org.bukkit.Material;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RankChallengeProgression service")
class RankChallengeProgressionServiceTest extends AbstractBaseTest {

  @Inject RankChallengeProgressionService rankChallengeProgressionService;

  @Test
  void givenNewRankChallengeProgression_WhenPersisted_ThenShouldBeRegisteredIntoDatabase() {
    RankChallengeProgression rcp =
        new RankChallengeProgression(UUID.randomUUID(), "test-rank", Material.OAK_LOG, 64);

    rankChallengeProgressionService.persist(rcp);

    RankChallengeProgression retrievedRcp =
        rankChallengeProgressionService.findById(rcp.getId()).orElse(null);
    Assert.assertNotNull(retrievedRcp);
    Assert.assertEquals(rcp, retrievedRcp);
  }
}
