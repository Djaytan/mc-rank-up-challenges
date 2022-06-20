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

package fr.djaytan.minecraft.rank_up_challenges.plugin.guice;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import fr.djaytan.minecraft.rank_up_challenges.RankUpChallengesException;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.track.TrackManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class GuiceBukkitLibsModule extends AbstractModule {

  private final LuckPerms luckPerms;
  private final Plugin plugin;

  public GuiceBukkitLibsModule(@NotNull LuckPerms luckPerms, @NotNull Plugin plugin) {
    this.luckPerms = luckPerms;
    this.plugin = plugin;
  }

  @Provides
  @Singleton
  public @NotNull MiniMessage provideMiniMessage() {
    return MiniMessage.miniMessage();
  }

  @Provides
  @Singleton
  public @NotNull PaperCommandManager provideAcfPaperCommandManager() {
    return new PaperCommandManager(plugin);
  }

  @Provides
  @Singleton
  public @NotNull Economy provideVaultEconomy() throws RankUpChallengesException {
    RegisteredServiceProvider<Economy> rsp =
        plugin.getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      throw new RankUpChallengesException("Failed to found Economy service of Vault dependency.");
    }
    return rsp.getProvider();
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
