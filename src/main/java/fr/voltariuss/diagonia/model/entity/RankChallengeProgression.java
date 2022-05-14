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

package fr.voltariuss.diagonia.model.entity;

import fr.voltariuss.diagonia.model.config.data.challenge.ChallengeType;
import fr.voltariuss.diagonia.model.entity.converter.UUIDConverter;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Material;

@Entity
@Table(name = "diagonia_rankup_challenge_progression")
@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

  @Column(name = "rankup_difficulty_tier", nullable = false)
  @Setter
  private int difficultyTier;

  @Column(name = "rankup_challenge_type", nullable = false)
  @Enumerated(EnumType.STRING)
  @Setter
  @NonNull
  private ChallengeType challengeType;

  @Column(name = "rankup_challenge_material", nullable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  @Setter
  @NonNull
  private Material challengeMaterial;

  @Column(name = "rankup_challenge_amount_required", nullable = false)
  @Setter
  private int challengeAmountRequired;

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
        .append(difficultyTier, that.difficultyTier)
        .append(challengeType, that.challengeType)
        .append(challengeMaterial, that.challengeMaterial)
        .append(challengeAmountRequired, that.challengeAmountRequired)
        .append(challengeAmountGiven, that.challengeAmountGiven)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(playerUuid)
        .append(rankId)
        .append(difficultyTier)
        .append(challengeType)
        .append(challengeMaterial)
        .append(challengeAmountRequired)
        .append(challengeAmountGiven)
        .toHashCode();
  }
}
