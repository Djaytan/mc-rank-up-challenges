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
import fr.voltariuss.diagonia.controller.EnchantmentController;
import fr.voltariuss.diagonia.controller.EnchantmentControllerImpl;
import fr.voltariuss.diagonia.controller.JobsController;
import fr.voltariuss.diagonia.controller.JobsControllerImpl;
import fr.voltariuss.diagonia.controller.MessageController;
import fr.voltariuss.diagonia.controller.MessageControllerImpl;
import fr.voltariuss.diagonia.controller.playershop.PlayerShopConfigController;
import fr.voltariuss.diagonia.controller.playershop.PlayerShopConfigControllerImpl;
import fr.voltariuss.diagonia.controller.playershop.PlayerShopController;
import fr.voltariuss.diagonia.controller.playershop.PlayerShopControllerImpl;
import fr.voltariuss.diagonia.controller.playershop.PlayerShopListController;
import fr.voltariuss.diagonia.controller.playershop.PlayerShopListControllerImpl;
import fr.voltariuss.diagonia.controller.rankup.RankUpChallengesController;
import fr.voltariuss.diagonia.controller.rankup.RankUpChallengesControllerImpl;
import fr.voltariuss.diagonia.controller.rankup.RankUpController;
import fr.voltariuss.diagonia.controller.rankup.RankUpControllerImpl;
import fr.voltariuss.diagonia.model.dao.PlayerShopDao;
import fr.voltariuss.diagonia.model.dao.PlayerShopDaoImpl;
import fr.voltariuss.diagonia.model.service.EconomyService;
import fr.voltariuss.diagonia.model.service.EconomyVaultService;
import fr.voltariuss.diagonia.model.service.JobsRebornService;
import fr.voltariuss.diagonia.model.service.JobsService;
import fr.voltariuss.diagonia.model.service.RankLuckPermsService;
import fr.voltariuss.diagonia.model.service.RankService;
import java.util.ResourceBundle;
import org.jetbrains.annotations.NotNull;

/** General Guice module. */
public class GuiceGeneralModule extends AbstractModule {

  private final ResourceBundle resourceBundle;

  public GuiceGeneralModule(@NotNull ResourceBundle resourceBundle) {
    this.resourceBundle = resourceBundle;
  }

  @Override
  public void configure() {
    bind(EconomyService.class).to(EconomyVaultService.class);
    bind(EnchantmentController.class).to(EnchantmentControllerImpl.class);
    bind(JobsController.class).to(JobsControllerImpl.class);
    bind(JobsService.class).to(JobsRebornService.class);
    bind(MessageController.class).to(MessageControllerImpl.class);
    bind(PlayerShopController.class).to(PlayerShopControllerImpl.class);
    bind(PlayerShopConfigController.class).to(PlayerShopConfigControllerImpl.class);
    bind(PlayerShopDao.class).to(PlayerShopDaoImpl.class);
    bind(PlayerShopListController.class).to(PlayerShopListControllerImpl.class);
    bind(RankService.class).to(RankLuckPermsService.class);
    bind(RankUpController.class).to(RankUpControllerImpl.class);
    bind(RankUpChallengesController.class).to(RankUpChallengesControllerImpl.class);
  }

  @Provides
  @Singleton
  public @NotNull ResourceBundle provideResourceBundle() {
    return resourceBundle;
  }
}
