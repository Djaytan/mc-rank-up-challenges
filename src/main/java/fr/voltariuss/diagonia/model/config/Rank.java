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

package fr.voltariuss.diagonia.model.config;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@Data
@Builder
public final class Rank {

  private final String id;
  private final String name;
  private final List<String> description;
  private final TextColor color;
  private final List<String> profits;
  private final boolean rankUpActivated;
  @Nullable private final List<RankChallenge> rankUpChallenges;
  // TODO: separate costs from prerequisites (currently not clear for players)
  @Nullable private final RankUpPrerequisites rankUpPrerequisites;
  @Nullable private final String rankUpTarget; // TODO: deprecated
}
