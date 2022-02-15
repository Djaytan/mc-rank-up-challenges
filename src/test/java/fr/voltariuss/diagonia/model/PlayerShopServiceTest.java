package fr.voltariuss.diagonia.model;

import fr.voltariuss.diagonia.DiagoniaPlayerShopsTestInjector;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import fr.voltariuss.diagonia.model.service.PlayerShopService;
import java.util.UUID;
import javax.inject.Inject;
import junit.framework.Assert;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PlayerShop service")
class PlayerShopServiceTest {

  @Inject private PlayerShopService playerShopService;

  @BeforeEach
  void setUp() {
    DiagoniaPlayerShopsTestInjector.inject(this);
  }

  @Test
  void givenNewPlayerShop_WhenPersisted_ThenShouldBeRegisteredIntoDatabase() {
    PlayerShop ps = new PlayerShop(UUID.randomUUID());
    ps.setDescription("Test description");
    ps.setItemIcon(Material.NAME_TAG);
    ps.setTpLocation(null);
    ps.setActive(true);

    playerShopService.persist(ps);

    PlayerShop retrievedPs = playerShopService.findById(ps.getId()).orElse(null);
    Assert.assertNotNull(retrievedPs);
    Assert.assertEquals(ps, retrievedPs);
  }
}
