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

package fr.voltariuss.diagonia.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.track.TrackManager;
import org.jetbrains.annotations.NotNull;

public class GuiceLuckPermsModule extends AbstractModule {

  private final LuckPerms luckPerms;

  public GuiceLuckPermsModule() {
    luckPerms = LuckPermsProvider.get();
  }

  @Provides
  @Singleton
  public @NotNull LuckPerms provideLuckPerms() {
    return luckPerms;
  }

  @Provides
  @Singleton
  public @NotNull UserManager provideUserManager() {
    return luckPerms.getUserManager();
  }

  @Provides
  @Singleton
  public @NotNull GroupManager provideGroupManager() {
    return luckPerms.getGroupManager();
  }

  @Provides
  @Singleton
  public @NotNull TrackManager provideTrackManager() {
    return luckPerms.getTrackManager();
  }
}
