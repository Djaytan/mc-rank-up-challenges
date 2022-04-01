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

import co.aikar.commands.PaperCommandManager;
import fr.voltariuss.diagonia.view.command.PlayerShopCommand;
import fr.voltariuss.diagonia.view.command.RanksCommand;
import fr.voltariuss.diagonia.view.command.UuidCommand;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class CommandRegister {

  private final PaperCommandManager paperCommandManager;

  private final PlayerShopCommand playerShopCommand;
  private final RanksCommand ranksCommand;
  private final UuidCommand uuidCommand;

  @Inject
  public CommandRegister(
      @NotNull PaperCommandManager paperCommandManager,
      @NotNull PlayerShopCommand playerShopCommand,
      @NotNull RanksCommand ranksCommand,
      @NotNull UuidCommand uuidCommand) {
    this.paperCommandManager = paperCommandManager;
    this.playerShopCommand = playerShopCommand;
    this.ranksCommand = ranksCommand;
    this.uuidCommand = uuidCommand;
  }

  public void registerCommands() {
    paperCommandManager.registerCommand(playerShopCommand);
    paperCommandManager.registerCommand(ranksCommand);
    paperCommandManager.registerCommand(uuidCommand);
  }
}
