package fr.voltariuss.diagonia.view.message;

import fr.voltariuss.diagonia.model.dto.LocationDto;
import fr.voltariuss.diagonia.model.dto.response.EconomyResponse;
import fr.voltariuss.diagonia.view.EconomyFormatter;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlayerShopMessage {

  private final EconomyFormatter economyFormatter;
  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;

  @Inject
  public PlayerShopMessage(
      @NotNull EconomyFormatter economyFormatter,
      @NotNull MiniMessage miniMessage,
      @NotNull ResourceBundle resourceBundle) {
    this.economyFormatter = economyFormatter;
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull Component buySuccess(@NotNull EconomyResponse economyResponse) {
    return miniMessage.deserialize(
        resourceBundle.getString("diagonia.playershop.buy.successfully_bought"),
        TemplateResolver.templates(
            Template.template(
                "diag_price", economyFormatter.format(economyResponse.getModifiedAmount())),
            Template.template(
                "diag_new_balance", economyFormatter.format(economyResponse.getNewBalance()))));
  }

  public @NotNull Component teleportPointDefined(@NotNull LocationDto locationDto) {
    return miniMessage.deserialize(
        resourceBundle.getString("diagonia.playershop.config.define_teleport.defined"),
        TemplateResolver.templates(
            Template.template("diag_teleport_point", locationDto.toString())));
    // TODO: more user-friendly message by preventing usage of LocationDto#toString()
  }

  public @NotNull Component toggleShop(boolean isPlayerShopActive) {
    return miniMessage.deserialize(
        resourceBundle.getString("diagonia.playershop.config.activation.toggled"),
        TemplateResolver.templates(
            Template.template(
                "diag_activation_state",
                isPlayerShopActive
                    ? Component.text(
                            resourceBundle.getString(
                                "diagonia.playershop.config.activation.toggled.enabled"))
                        .color(NamedTextColor.GREEN)
                    : Component.text(
                            resourceBundle.getString(
                                "diagonia.playershop.config.activation.toggled.disabled"))
                        .color(NamedTextColor.RED))));
  }

  public @NotNull Component shopActivationRequireTeleportPointFirst() {
    return miniMessage.deserialize(
        resourceBundle.getString(
            "diagonia.playershop.config.activation.enabling.teleport_point_definition_required"));
  }

  public @NotNull Component insufficientFunds() {
    return miniMessage.deserialize(
        resourceBundle.getString("diagonia.playershop.buy.insufficient_funds"));
  }

  public @NotNull Component transactionFailed() {
    return miniMessage.deserialize(
        resourceBundle.getString("diagonia.playershop.buy.transaction_failed"));
  }

  public @NotNull Component noTeleportPointDefined() {
    return miniMessage.deserialize(
        resourceBundle.getString("diagonia.playershop.teleport.no_tp_defined_error"));
  }
}
