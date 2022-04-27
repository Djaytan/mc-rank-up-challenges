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

package fr.voltariuss.diagonia.controller;

import com.gamingmesh.jobs.container.ActionType;
import com.google.common.base.Preconditions;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class JobsControllerImpl implements JobsController {

  private final Plugin plugin;

  @Inject
  public JobsControllerImpl(@NotNull Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean isPlaceAndBreakAction(@NotNull ActionType actionType, @Nullable Block block) {
    Preconditions.checkNotNull(actionType);

    if (block == null
        || !actionType.equals(ActionType.BREAK) && !actionType.equals(ActionType.TNTBREAK)) {
      return false;
    }

    return block.hasMetadata(PLAYER_BLOCK_PLACED_METADATA_KEY);
  }

  @Override
  public void setPlayerBlockPlacedMetadata(@NotNull Block block) {
    Preconditions.checkNotNull(block);

    block.setMetadata(PLAYER_BLOCK_PLACED_METADATA_KEY, new FixedMetadataValue(plugin, true));
  }
}
