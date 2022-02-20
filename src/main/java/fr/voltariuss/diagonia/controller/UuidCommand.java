package fr.voltariuss.diagonia.controller;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.ResourceBundle;

@CommandAlias("uuid")
public class UuidCommand extends BaseCommand {

  private final ResourceBundle resourceBundle;

  @Inject
  public UuidCommand(@NotNull ResourceBundle resourceBundle) {
    this.resourceBundle = resourceBundle;
  }

  @Default
  public void onExecute(@NotNull Player player) {
    player.sendMessage(Component.text(String.format(resourceBundle.getString("diagonia.uuid"), player.getUniqueId())));
  }
}
