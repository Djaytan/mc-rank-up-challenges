package fr.voltariuss.diagonia.model.entity;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RankChallengeProgression entity")
public class RankChallengeProgressionTest {

  @Test
  void equalsAndHashCodeContract() {
    EqualsVerifier.forClass(RankChallengeProgression.class).verify();
  }
}
