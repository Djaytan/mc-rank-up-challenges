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

package fr.voltariuss.diagonia.plugin;

import fr.voltariuss.diagonia.DiagoniaRuntimeException;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PrerequisitesValidation {

  private final Server server;

  @Inject
  public PrerequisitesValidation(@NotNull Server server) {
    this.server = server;
  }

  public void validate() {
    // TODO: add other validations (LuckPerms, Jobs, ...)
    if (!server.getPluginManager().isPluginEnabled("Vault")) {
      throw new DiagoniaRuntimeException("Required Vault dependency not found.");
    }
  }
}
