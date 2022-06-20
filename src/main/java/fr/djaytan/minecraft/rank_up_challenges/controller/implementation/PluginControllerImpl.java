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

package fr.djaytan.minecraft.rank_up_challenges.controller.implementation;

import fr.djaytan.minecraft.rank_up_challenges.JdbcUrl;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.challenge.ChallengeConfig;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.MessageController;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.PluginController;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.PluginConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.RankConfig;
import fr.djaytan.minecraft.rank_up_challenges.plugin.CommandRegister;
import fr.djaytan.minecraft.rank_up_challenges.plugin.ListenerRegister;
import fr.djaytan.minecraft.rank_up_challenges.plugin.PrerequisitesValidation;
import fr.djaytan.minecraft.rank_up_challenges.view.message.CommonMessage;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.plugin.Plugin;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class PluginControllerImpl implements PluginController {

  private final ChallengeConfig challengeConfig;
  private final CommandRegister commandRegister;
  private final CommonMessage commonMessage;
  private final JdbcUrl jdbcUrl;
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
      @NotNull ChallengeConfig challengeConfig,
      @NotNull CommandRegister commandRegister,
      @NotNull CommonMessage commonMessage,
      @NotNull JdbcUrl jdbcUrl,
      @NotNull ListenerRegister listenerRegister,
      @NotNull Logger logger,
      @NotNull MessageController messageController,
      @NotNull Plugin plugin,
      @NotNull PluginConfig pluginConfig,
      @NotNull PrerequisitesValidation prerequisitesValidation,
      @NotNull RankConfig rankConfig,
      @NotNull SessionFactory sessionFactory) {
    this.challengeConfig = challengeConfig;
    this.commandRegister = commandRegister;
    this.commonMessage = commonMessage;
    this.jdbcUrl = jdbcUrl;
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
          commonMessage.startupBannerStateLine("Database connection URL", jdbcUrl.asStringUrl()));
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
          commonMessage.startupBannerProgressionLine("Challenge config file loading"));
      messageController.sendConsoleMessage(
          commonMessage.startupBannerStateLine(
              "Number of challenges loaded",
              Integer.toString(challengeConfig.countNbChallenges())));

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
