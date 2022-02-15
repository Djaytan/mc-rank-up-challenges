package fr.voltariuss.diagonia.model.entity;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PlayerShop entity")
class PlayerShopTest {

  @Test
  void equalsAndHashCodeContract() {
    EqualsVerifier.forClass(PlayerShop.class).verify();
  }
}
