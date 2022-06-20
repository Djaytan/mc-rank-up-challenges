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
    return ResourceBundle.getBundle("diagonia", Locale.FRANCE);
  }
}
