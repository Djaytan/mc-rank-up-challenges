package fr.voltariuss.diagonia.view.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("uuid")
@Singleton
public class UuidCommand extends BaseCommand {

  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;

  @Inject
  public UuidCommand(@NotNull MiniMessage miniMessage, @NotNull ResourceBundle resourceBundle) {
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
  }

  @Default
  public void onExecute(@NotNull Player player) {
    player.sendMessage(
        miniMessage.deserialize(
            resourceBundle.getString("diagonia.uuid"),
            TemplateResolver.templates(
                Template.template("diag_uuid", player.getUniqueId().toString()))));
  }
}
