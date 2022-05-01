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

import lombok.Builder;
import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@Data
@Builder
public final class RankUpPrerequisites {

  private final double moneyCost;
  private final int enchantingLevelsCost;
  private final int jobsLevels;
}
