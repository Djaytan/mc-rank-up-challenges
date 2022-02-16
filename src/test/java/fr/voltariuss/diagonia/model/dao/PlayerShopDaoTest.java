package fr.voltariuss.diagonia.model.dao;

import fr.voltariuss.diagonia.AbstractBaseTest;
import fr.voltariuss.diagonia.model.dto.LocationDto;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import junit.framework.Assert;
import org.bukkit.Material;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

@DisplayName("PlayerShop DAO Test")
class PlayerShopDaoTest extends AbstractBaseTest {

  @Inject private PlayerShopDao playerShopDao;

  @BeforeEach
  void setUp() {
    playerShopDao.openSession();
  }

  @AfterEach
  void tearDown() {
    playerShopDao.destroySession();
  }

  @Test
  void givenDefaultObject_whenPersisted_thenFindable() {
    PlayerShop ps = new PlayerShop(UUID.randomUUID());

    Transaction ts = playerShopDao.beginTransaction();
    playerShopDao.persist(ps);
    List<PlayerShop> playerShopList = playerShopDao.findAll();
    ts.commit();

    Assert.assertEquals(1, playerShopList.size());
  }

  @Test
  void givenDefaultInstance_whenUpdated_thenSucceed() {
    String initialDescription = "one";
    String updatedDescription = "two";

    PlayerShop ps = new PlayerShop(UUID.randomUUID());
    ps.setDescription(initialDescription);

    playerShopDao.persist(ps);

    ps.setDescription(updatedDescription);

    Transaction ts = playerShopDao.beginTransaction();
    playerShopDao.update(ps);
    PlayerShop psBis = playerShopDao.findAll().get(0);
    ts.commit();

    Assert.assertEquals(updatedDescription, psBis.getDescription());
  }

  @Nested
  @DisplayName("FindById method")
  class FindByIdTest {

    @Test
    void givenDefaultObjectPersisted_whenFindById_thenMatchInitialObject() {
      PlayerShop ps = new PlayerShop(UUID.randomUUID());

      Transaction ts = playerShopDao.beginTransaction();
      playerShopDao.persist(ps);
      PlayerShop psBis = playerShopDao.findAll().get(0);
      ts.commit();

      Assert.assertEquals(ps.getOwnerUuid(), psBis.getOwnerUuid());
      Assert.assertEquals(ps.getDescription(), psBis.getDescription());
      Assert.assertEquals(ps.getItemIcon(), psBis.getItemIcon());
      Assert.assertEquals(ps.getTpLocation(), psBis.getTpLocation());
      Assert.assertEquals(ps.isActive(), psBis.isActive());
    }

    @Test
    void givenCustomizedObjectPersisted_whenFindById_thenMatchInitialObject() {
      PlayerShop ps = new PlayerShop(UUID.randomUUID());
      ps.setDescription("one");
      ps.setItemIcon(Material.NAME_TAG);
      ps.setTpLocation(new LocationDto("two", 0.5D, 0.5D, 0.5D, 0.5f, 0.5f));
      ps.setActive(true);

      Transaction ts = playerShopDao.beginTransaction();
      playerShopDao.persist(ps);
      List<PlayerShop> playerShopList = playerShopDao.findAll();
      ts.commit();

      PlayerShop psBis = playerShopList.get(0);

      Assert.assertEquals(ps.getOwnerUuid(), psBis.getOwnerUuid());
      Assert.assertEquals(ps.getDescription(), psBis.getDescription());
      Assert.assertEquals(ps.getItemIcon(), psBis.getItemIcon());
      Assert.assertEquals(ps.getTpLocation(), psBis.getTpLocation());
      Assert.assertEquals(ps.isActive(), psBis.isActive());
    }

    @Test
    void givenNoneObjectPersisted_whenFindById_thenRecoverNullObject() {
      PlayerShop ps = playerShopDao.findById(1L).orElse(null);

      Assert.assertNull(ps);
    }
  }

  @Test
  void givenObjectPersisted_whenDeleted_thenAbsentFromDb() {
    PlayerShop ps = new PlayerShop(UUID.randomUUID());

    Transaction ts = playerShopDao.beginTransaction();
    playerShopDao.persist(ps);
    playerShopDao.delete(ps);
    PlayerShop psBis = playerShopDao.findById(1L).orElse(null);
    ts.commit();

    Assert.assertNull(psBis);
  }

  @Nested
  @DisplayName("FindAll method")
  class FindAllTest {

    @Test
    void givenMultipleObjects_whenFindAll_thenAllRecovered() {
      PlayerShop ps1 = new PlayerShop(UUID.randomUUID());
      PlayerShop ps2 = new PlayerShop(UUID.randomUUID());

      Transaction ts = playerShopDao.beginTransaction();
      playerShopDao.persist(ps1);
      playerShopDao.persist(ps2);
      List<PlayerShop> playerShopList = playerShopDao.findAll();
      ts.commit();

      Assert.assertEquals(2, playerShopList.size());
      Assert.assertEquals(ps1, playerShopList.get(0));
      Assert.assertEquals(ps2, playerShopList.get(1));
    }

    @Test
    void givenNoneObject_whenFindAll_thenRecoverNothing() {
      List<PlayerShop> playerShopList = playerShopDao.findAll();

      Assert.assertEquals(0, playerShopList.size());
    }
  }
}
