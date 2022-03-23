package fr.voltariuss.diagonia.view.message;

import fr.voltariuss.diagonia.model.dto.LocationDto;
import fr.voltariuss.diagonia.model.dto.response.EconomyResponse;
import fr.voltariuss.diagonia.view.EconomyFormatter;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
    // TODO: use TagResolver instead of String#format()
    return miniMessage.deserialize(
        String.format(
            resourceBundle.getString("diagonia.playershop.buy.successfully_bought"),
            economyFormatter.format(economyResponse.getModifiedAmount()),
            economyFormatter.format(economyResponse.getNewBalance())));
  }

  public @NotNull Component teleportPointDefined(@NotNull LocationDto locationDto) {
    return miniMessage.deserialize(
        String.format(
            resourceBundle.getString("diagonia.playershop.config.define_teleport.defined"),
            locationDto));
  }

  public @NotNull Component toggleShop(boolean isPlayerShopActive) {
    return miniMessage.deserialize(
        String.format(
            resourceBundle.getString("diagonia.playershop.config.activation.toggled"),
            isPlayerShopActive
                ? resourceBundle.getString("diagonia.playershop.config.activation.toggled.enabled")
                : resourceBundle.getString(
                    "diagonia.playershop.config.activation.toggled.disabled")));
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
