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

package fr.voltariuss.diagonia.plugin.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import fr.voltariuss.diagonia.controller.api.ConfigController;
import fr.voltariuss.diagonia.controller.api.EnchantmentController;
import fr.voltariuss.diagonia.controller.api.JobsController;
import fr.voltariuss.diagonia.controller.api.MessageController;
import fr.voltariuss.diagonia.controller.api.PlayerController;
import fr.voltariuss.diagonia.controller.api.PlayerShopConfigController;
import fr.voltariuss.diagonia.controller.api.PlayerShopController;
import fr.voltariuss.diagonia.controller.api.PlayerShopListController;
import fr.voltariuss.diagonia.controller.api.PluginController;
import fr.voltariuss.diagonia.controller.api.RankUpChallengesController;
import fr.voltariuss.diagonia.controller.api.RankUpController;
import fr.voltariuss.diagonia.controller.implementation.ConfigControllerImpl;
import fr.voltariuss.diagonia.controller.implementation.EnchantmentControllerImpl;
import fr.voltariuss.diagonia.controller.implementation.JobsControllerImpl;
import fr.voltariuss.diagonia.controller.implementation.MessageControllerImpl;
import fr.voltariuss.diagonia.controller.implementation.PlayerControllerImpl;
import fr.voltariuss.diagonia.controller.implementation.PlayerShopConfigControllerImpl;
import fr.voltariuss.diagonia.controller.implementation.PlayerShopControllerImpl;
import fr.voltariuss.diagonia.controller.implementation.PlayerShopListControllerImpl;
import fr.voltariuss.diagonia.controller.implementation.PluginControllerImpl;
import fr.voltariuss.diagonia.controller.implementation.RankUpChallengesControllerImpl;
import fr.voltariuss.diagonia.controller.implementation.RankUpControllerImpl;
import fr.voltariuss.diagonia.model.dao.PlayerShopDao;
import fr.voltariuss.diagonia.model.dao.PlayerShopDaoImpl;
import fr.voltariuss.diagonia.model.service.api.EconomyService;
import fr.voltariuss.diagonia.model.service.api.JobsService;
import fr.voltariuss.diagonia.model.service.api.PlayerShopService;
import fr.voltariuss.diagonia.model.service.api.RankService;
import fr.voltariuss.diagonia.model.service.api.RankUpService;
import fr.voltariuss.diagonia.model.service.implementation.EconomyVaultServiceImpl;
import fr.voltariuss.diagonia.model.service.implementation.JobsRebornServiceImpl;
import fr.voltariuss.diagonia.model.service.implementation.PlayerShopServiceImpl;
import fr.voltariuss.diagonia.model.service.implementation.RankLuckPermsServiceImpl;
import fr.voltariuss.diagonia.model.service.implementation.RankUpServiceImpl;
import java.util.Locale;
import java.util.ResourceBundle;
import org.jetbrains.annotations.NotNull;

/** General Guice module. */
public class GuiceGeneralModule extends AbstractModule {

  @Override
  public void configure() {
    bind(ConfigController.class).to(ConfigControllerImpl.class);
    bind(EconomyService.class).to(EconomyVaultServiceImpl.class);
    bind(EnchantmentController.class).to(EnchantmentControllerImpl.class);
    bind(JobsController.class).to(JobsControllerImpl.class);
    bind(JobsService.class).to(JobsRebornServiceImpl.class);
    bind(MessageController.class).to(MessageControllerImpl.class);
    bind(PlayerController.class).to(PlayerControllerImpl.class);
    bind(PlayerShopController.class).to(PlayerShopControllerImpl.class);
    bind(PlayerShopConfigController.class).to(PlayerShopConfigControllerImpl.class);
    bind(PlayerShopDao.class).to(PlayerShopDaoImpl.class);
    bind(PlayerShopListController.class).to(PlayerShopListControllerImpl.class);
    bind(PlayerShopService.class).to(PlayerShopServiceImpl.class);
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
