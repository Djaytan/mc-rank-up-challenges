package fr.voltariuss.diagonia.model.entity;

import fr.voltariuss.diagonia.model.UUIDConverter;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Material;

@Entity
@Table(name = "diagonia_rankup_challenge_progression")
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class RankChallengeProgression {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "rankup_challenge_id", nullable = false, updatable = false)
  private long id;

  @Column(name = "rankup_player_uuid", nullable = false, updatable = false)
  @Convert(converter = UUIDConverter.class)
  @Setter
  @NonNull
  private UUID playerUuid;

  @Column(name = "rankup_rank_id", nullable = false, updatable = false)
  @Setter
  @NonNull
  private String rankId;

  @Column(name = "rankup_challenge_material", nullable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  @Setter
  @NonNull
  private Material challengeMaterial;

  @Column(name = "rankup_challenge_amount_given", nullable = false)
  @Setter
  private int challengeAmountGiven;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RankChallengeProgression that)) {
      return false;
    }
    return new EqualsBuilder()
        .append(playerUuid, that.playerUuid)
        .append(rankId, that.rankId)
        .append(challengeMaterial, that.challengeMaterial)
        .append(challengeAmountGiven, that.challengeAmountGiven)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(playerUuid)
        .append(rankId)
        .append(challengeMaterial)
        .append(challengeAmountGiven)
        .toHashCode();
  }
}
