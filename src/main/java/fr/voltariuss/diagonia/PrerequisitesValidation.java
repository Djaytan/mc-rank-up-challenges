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

package fr.voltariuss.diagonia;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PrerequisitesValidation {

  private final CriticalErrorHandler criticalErrorHandler;
  private final Server server;

  @Inject
  public PrerequisitesValidation(
      @NotNull CriticalErrorHandler criticalErrorHandler, @NotNull Server server) {
    this.criticalErrorHandler = criticalErrorHandler;
    this.server = server;
  }

  public void validate() {
    if (!server.getPluginManager().isPluginEnabled("Vault")) {
      criticalErrorHandler.raiseCriticalError("Required Vault dependency not found.");
    }
  }
}
