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

import fr.voltariuss.diagonia.controller.MessageController;
import fr.voltariuss.diagonia.controller.MessageControllerImpl;
import fr.voltariuss.diagonia.guice.GuiceInjector;
import fr.voltariuss.diagonia.model.RankConfigDeserializer;
import fr.voltariuss.diagonia.model.RankConfigInitializer;
import fr.voltariuss.diagonia.model.config.PluginConfig;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.config.rank.RankConfig;
import fr.voltariuss.diagonia.model.service.PluginConfigService;
import fr.voltariuss.diagonia.utils.UrlUtils;
import fr.voltariuss.diagonia.view.message.CommonMessage;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.inject.Inject;
import lombok.SneakyThrows;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;

/** Diagonia plugin */
public class DiagoniaPlugin extends JavaPlugin {

  @Inject private SessionFactory sessionFactory;
  @Inject private CommandRegister commandRegister;
  @Inject private ListenerRegister listenerRegister;
  @Inject private PrerequisitesValidation prerequisitesValidation;

  @SneakyThrows
  @Override
  public void onEnable() {
    ConsoleCommandSender consoleCommandSender = getServer().getConsoleSender();
    MiniMessage miniMessage = MiniMessage.miniMessage();
    ResourceBundle resourceBundle = ResourceBundle.getBundle("diagonia", Locale.FRANCE);
    MessageController messageController =
        new MessageControllerImpl(consoleCommandSender, miniMessage, resourceBundle, getServer());
    CommonMessage commonMessage = new CommonMessage(miniMessage, resourceBundle);

    try {
      PluginConfig pluginConfig = loadPluginConfig();
      RankConfig rankConfig = loadRankConfig();

      GuiceInjector.inject(miniMessage, this, pluginConfig, rankConfig, resourceBundle);

      messageController.sendConsoleMessage(commonMessage.startupBanner());
      messageController.sendConsoleMessage(
          commonMessage.startupBannerVersionLine(getDescription()));

      messageController.sendConsoleMessage(
          commonMessage.startupBannerProgressionLine("General config file loading"));
      messageController.sendConsoleMessage(
          commonMessage.startupBannerStateLine(
              "Debug Mode", Boolean.toString(pluginConfig.isDebugMode())));
      messageController.sendConsoleMessage(
          commonMessage.startupBannerStateLine(
              "Database connection URL",
              UrlUtils.getDatabaseUrl(
                  pluginConfig.getDatabaseConfig().getHost(),
                  pluginConfig.getDatabaseConfig().getPort(),
                  pluginConfig.getDatabaseConfig().getDatabase())));
      messageController.sendConsoleMessage(
          commonMessage.startupBannerStateLine(
              "Database username", pluginConfig.getDatabaseConfig().getUsername()));
      messageController.sendConsoleMessage(
          commonMessage.startupBannerStateLine(
              "LuckPerms rank track name", pluginConfig.getRankUpConfig().getLuckPermsTrackName()));

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
      messageController.sendConsoleMessage(commonMessage.startupBannerEnablingFailureLine());
      getSLF4JLogger().error("Something went wrong and prevent plugin activation.", e);
      getSLF4JLogger().error("Disabling plugin...");
      getServer().getPluginManager().disablePlugin(this);
    }
  }

  @Override
  public void onDisable() {
    if (sessionFactory != null) {
      sessionFactory.close();
      getSLF4JLogger().info("Database connection closed");
    }
    getSLF4JLogger().info("Plugin successfully disabled");
  }

  private @NotNull PluginConfig loadPluginConfig() {
    PluginConfigService.init(getConfig());
    getConfig().options().copyDefaults(true);
    saveConfig();
    PluginConfig pluginConfig = PluginConfigService.loadConfig(getConfig());

    if (!pluginConfig.getDatabaseConfig().isEnabled()) {
      throw new DiagoniaRuntimeException(
          "Database disabled. Please configure and activate it through config.yml file.");
    }

    return pluginConfig;
  }

  private @NotNull RankConfig loadRankConfig() throws IOException, DiagoniaException {
    RankConfigInitializer rankConfigInitializer =
        new RankConfigInitializer(
            getDataFolder(), this, getSLF4JLogger(), new RankConfigDeserializer());
    rankConfigInitializer.init();
    return rankConfigInitializer.readRankConfig();
  }
}
