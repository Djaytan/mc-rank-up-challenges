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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.ConfigController;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.MessageController;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.PlayerController;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.PluginController;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.RankUpController;
import fr.djaytan.minecraft.rank_up_challenges.controller.implementation.PlayerControllerImpl;
import fr.djaytan.minecraft.rank_up_challenges.controller.implementation.PluginControllerImpl;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.EconomyService;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.JobsService;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.RankService;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.RankUpService;
import fr.djaytan.minecraft.rank_up_challenges.model.service.implementation.EconomyVaultServiceImpl;
import fr.djaytan.minecraft.rank_up_challenges.model.service.implementation.JobsRebornServiceImpl;
import fr.djaytan.minecraft.rank_up_challenges.model.service.implementation.RankLuckPermsServiceImpl;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.RankUpChallengesController;
import fr.djaytan.minecraft.rank_up_challenges.controller.implementation.ConfigControllerImpl;
import fr.djaytan.minecraft.rank_up_challenges.controller.implementation.MessageControllerImpl;
import fr.djaytan.minecraft.rank_up_challenges.controller.implementation.RankUpChallengesControllerImpl;
import fr.djaytan.minecraft.rank_up_challenges.controller.implementation.RankUpControllerImpl;
import fr.djaytan.minecraft.rank_up_challenges.model.service.implementation.RankUpServiceImpl;
import java.util.Locale;
import java.util.ResourceBundle;
import org.jetbrains.annotations.NotNull;

/** General Guice module. */
public class GuiceGeneralModule extends AbstractModule {

  @Override
  public void configure() {
    bind(ConfigController.class).to(ConfigControllerImpl.class);
    bind(EconomyService.class).to(EconomyVaultServiceImpl.class);
    bind(JobsService.class).to(JobsRebornServiceImpl.class);
    bind(MessageController.class).to(MessageControllerImpl.class);
    bind(PlayerController.class).to(PlayerControllerImpl.class);
    bind(PluginController.class).to(PluginControllerImpl.class);
    bind(RankService.class).to(RankLuckPermsServiceImpl.class);
    bind(RankUpController.class).to(RankUpControllerImpl.class);
    bind(RankUpChallengesController.class).to(RankUpChallengesControllerImpl.class);
    bind(RankUpService.class).to(RankUpServiceImpl.class);
  }

  @Provides
  @Singleton
  public @NotNull ResourceBundle provideResourceBundle() {
    return ResourceBundle.getBundle("rank_up_challenges", Locale.FRANCE);
  }
}
