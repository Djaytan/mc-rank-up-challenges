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

package fr.djaytan.minecraft.rank_up_challenges.plugin;

import co.aikar.commands.PaperCommandManager;
import fr.djaytan.minecraft.rank_up_challenges.controller.command.RankUpCommand;
import fr.djaytan.minecraft.rank_up_challenges.controller.command.RanksCommand;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

@Singleton
public class CommandRegister {

  private final PaperCommandManager paperCommandManager;

  private final RanksCommand ranksCommand;
  private final RankUpCommand rankUpCommand;
  private final Server server;

  @Inject
  public CommandRegister(
      @NotNull PaperCommandManager paperCommandManager,
      @NotNull RanksCommand ranksCommand,
      @NotNull RankUpCommand rankUpCommand,
      @NotNull Server server) {
    this.paperCommandManager = paperCommandManager;
    this.ranksCommand = ranksCommand;
    this.rankUpCommand = rankUpCommand;
    this.server = server;
  }

  public void registerCommands() {
    paperCommandManager.registerCommand(ranksCommand);
    paperCommandManager.registerCommand(rankUpCommand);
  }

  public void registerCommandCompletions() {
    paperCommandManager
        .getCommandCompletions()
        .registerAsyncCompletion(
            "allplayers",
            context -> {
              long size = Long.parseLong(context.getConfig("size", "100"));
              return Arrays.stream(server.getOfflinePlayers())
                  .limit(size)
                  .map(OfflinePlayer::getName)
                  .toList();
            });
  }
}
