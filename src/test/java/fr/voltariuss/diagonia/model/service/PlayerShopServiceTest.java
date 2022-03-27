package fr.voltariuss.diagonia.model.service;

import fr.voltariuss.diagonia.AbstractBaseTest;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.UUID;
import javax.inject.Inject;
import junit.framework.Assert;
import org.bukkit.Material;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PlayerShop service")
class PlayerShopServiceTest extends AbstractBaseTest {

  @Inject private PlayerShopService playerShopService;

  @Test
  void givenNewPlayerShop_WhenPersisted_ThenShouldBeRegisteredIntoDatabase() {
    PlayerShop ps = new PlayerShop(UUID.randomUUID());
    ps.setDescription("Test description");
    ps.setItemIcon(Material.NAME_TAG);
    ps.setTpLocationDto(null);
    ps.setActive(true);

    playerShopService.persist(ps);

    PlayerShop retrievedPs = playerShopService.findById(ps.getId()).orElse(null);
    Assert.assertNotNull(retrievedPs);
    Assert.assertEquals(ps, retrievedPs);
  }
}
