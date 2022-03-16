package fr.voltariuss.diagonia.view.message;

import fr.voltariuss.diagonia.view.EconomyFormatter;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlayerShopBuyMessage {

  private final EconomyFormatter economyFormatter;
  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;

  @Inject
  public PlayerShopBuyMessage(
      @NotNull EconomyFormatter economyFormatter,
      @NotNull MiniMessage miniMessage,
      @NotNull ResourceBundle resourceBundle) {
    this.economyFormatter = economyFormatter;
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull Component buySuccessMessage(double buyCost) {
    return miniMessage.deserialize(
        String.format(
            resourceBundle.getString("diagonia.playershop.buy.successfully_bought"),
            economyFormatter.format(buyCost)));
  }
}
