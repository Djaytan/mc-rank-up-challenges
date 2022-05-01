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

package fr.voltariuss.diagonia.controller;

import fr.voltariuss.diagonia.CommandRegister;
import fr.voltariuss.diagonia.ListenerRegister;
import fr.voltariuss.diagonia.PrerequisitesValidation;
import fr.voltariuss.diagonia.model.config.PluginConfig;
import fr.voltariuss.diagonia.model.config.Rank;
import fr.voltariuss.diagonia.model.config.RankConfig;
import fr.voltariuss.diagonia.utils.UrlUtils;
import fr.voltariuss.diagonia.view.message.CommonMessage;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.plugin.Plugin;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class PluginControllerImpl implements PluginController {

  private final CommandRegister commandRegister;
  private final CommonMessage commonMessage;
  private final ListenerRegister listenerRegister;
  private final Logger logger;
  private final MessageController messageController;
  private final Plugin plugin;
  private final PluginConfig pluginConfig;
  private final PrerequisitesValidation prerequisitesValidation;
  private final RankConfig rankConfig;
  private final SessionFactory sessionFactory;

  @Inject
  public PluginControllerImpl(
      @NotNull CommandRegister commandRegister,
      @NotNull CommonMessage commonMessage,
      @NotNull ListenerRegister listenerRegister,
      @NotNull Logger logger,
      @NotNull MessageController messageController,
      @NotNull Plugin plugin,
      @NotNull PluginConfig pluginConfig,
      @NotNull PrerequisitesValidation prerequisitesValidation,
      @NotNull RankConfig rankConfig,
      @NotNull SessionFactory sessionFactory) {
    this.commandRegister = commandRegister;
    this.commonMessage = commonMessage;
    this.listenerRegister = listenerRegister;
    this.logger = logger;
    this.messageController = messageController;
    this.plugin = plugin;
    this.pluginConfig = pluginConfig;
    this.prerequisitesValidation = prerequisitesValidation;
    this.rankConfig = rankConfig;
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void disablePlugin() {
    sessionFactory.close();
    logger.info("Database connections closed");
    logger.info("Plugin successfully disabled");
  }

  @Override
  public void enablePlugin() {
    try {
      // TODO: startup banner in separated class in view
      messageController.sendConsoleMessage(commonMessage.startupBanner());
      messageController.sendConsoleMessage(
          commonMessage.startupBannerVersionLine(plugin.getDescription()));

      messageController.sendConsoleMessage(
          commonMessage.startupBannerProgressionLine("General config file loading"));
      messageController.sendConsoleMessage(
          commonMessage.startupBannerStateLine(
              "Debug Mode", Boolean.toString(pluginConfig.isDebug())));
      messageController.sendConsoleMessage(
          commonMessage.startupBannerStateLine(
              "Database connection URL",
              UrlUtils.getDatabaseUrl(
                  pluginConfig.getDatabase().getHost(),
                  pluginConfig.getDatabase().getPort(),
                  pluginConfig.getDatabase().getDatabase())));
      messageController.sendConsoleMessage(
          commonMessage.startupBannerStateLine(
              "Database username", pluginConfig.getDatabase().getUsername()));
      messageController.sendConsoleMessage(
          commonMessage.startupBannerStateLine(
              "LuckPerms rank track name", pluginConfig.getRankUp().getLuckPermsTrackName()));

      messageController.sendConsoleMessage(
          commonMessage.startupBannerProgressionLine("Rank config file loading"));
      messageController.sendConsoleMessage(
          commonMessage.startupBannerStateLine(
              "Number of ranks loaded", Integer.toString(rankConfig.getRanks().size())));
      messageController.sendConsoleMessage(
          commonMessage.startupBannerStateLine(
              "Number of challenges loaded",
              Integer.toString(
                  rankConfig.getRanks().stream()
                      .map(Rank::getRankUpChallenges)
                      .map(Objects::requireNonNull)
                      .mapToInt(List::size)
                      .sum())));

      messageController.sendConsoleMessage(
          commonMessage.startupBannerProgressionLine("Guice full injection"));

      prerequisitesValidation.validate();

      messageController.sendConsoleMessage(
          commonMessage.startupBannerProgressionLine("Dependencies validation"));

      commandRegister.registerCommands();
      commandRegister.registerCommandCompletions();

      messageController.sendConsoleMessage(
          commonMessage.startupBannerProgressionLine("Commands registration"));

      listenerRegister.registerListeners();

      messageController.sendConsoleMessage(
          commonMessage.startupBannerProgressionLine("Listeners registration"));

      messageController.sendConsoleMessage(commonMessage.startupBannerEnablingSuccessLine());
    } catch (Exception e) {
      // TODO: more centralized error management (listeners, commands, ...)
      messageController.sendConsoleMessage(commonMessage.startupBannerEnablingFailureLine());
      logger.error("Something went wrong and prevent plugin activation.", e);
      logger.error("Disabling plugin...");
      plugin.getServer().getPluginManager().disablePlugin(plugin);
    }
  }
}
