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

package fr.voltariuss.diagonia.model.service;

import com.google.common.base.Preconditions;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.config.rank.RankConfig;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankConfigService {

  private final RankConfig rankConfig;

  @Inject
  public RankConfigService(@NotNull RankConfig rankConfig) {
    this.rankConfig = rankConfig;
  }

  public @NotNull Optional<Rank> findById(@NotNull String id) {
    Preconditions.checkNotNull(id);

    return rankConfig.getRanks().stream().filter(rank -> rank.getId().equals(id)).findFirst();
  }
}
