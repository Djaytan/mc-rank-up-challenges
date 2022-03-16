package fr.voltariuss.diagonia.view.message;

import fr.voltariuss.diagonia.model.config.rank.Rank;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankUpMessage {

  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankUpMessage(@NotNull MiniMessage miniMessage, @NotNull ResourceBundle resourceBundle) {
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull Component rankUpFailure() {
    return miniMessage
        .deserialize(resourceBundle.getString("diagonia.rankup.rankup.failure"))
        .decoration(TextDecoration.ITALIC, false);
  }

  public @NotNull Component rankUpSuccess(@NotNull Rank newRank) {
    return miniMessage
        .deserialize(
            String.format(
                resourceBundle.getString("diagonia.rankup.rankup.success"), newRank.getId()))
        .decoration(TextDecoration.ITALIC, false)
        .append(
            Component.text(newRank.getName())
                .color(newRank.getColor())
                .decoration(TextDecoration.ITALIC, false));
  }

  public @NotNull Component prerequisitesNotRespected() {
    return miniMessage
        .deserialize(
            resourceBundle.getString("diagonia.rankup.rankup.cost.prerequisites_not_respected"))
        .decoration(TextDecoration.ITALIC, false);
  }
}
