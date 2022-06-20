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

package fr.djaytan.minecraft.rank_up_challenges.model.entity;

import fr.djaytan.minecraft.rank_up_challenges.model.config.data.challenge.ChallengeType;
import fr.djaytan.minecraft.rank_up_challenges.model.entity.converter.UUIDConverter;
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
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Material;

@Entity
@Table(name = "diagonia_rankup_challenge_progression_v2")
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

  @Column(name = "rankup_difficulty_tier", nullable = true)
  @Setter
  private int difficultyTier;

  @Column(name = "rankup_challenge_type", nullable = true)
  @Enumerated(EnumType.STRING)
  @Setter
  @NonNull
  private ChallengeType challengeType;

  @Column(name = "rankup_challenge_material", nullable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  @Setter
  @NonNull
  private Material challengeMaterial;

  @Column(name = "rankup_challenge_amount_required", nullable = true)
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
