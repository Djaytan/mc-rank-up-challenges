package fr.voltariuss.diagonia.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class ItemUtils {

  private static final int LORE_LINE_SIZE = 30;

  @Contract(value = "_ -> new", pure = true)
  public @NotNull List<Component> asLore(@Nullable String description) {
    List<Component> descriptionComponent = Collections.emptyList();
    if (description != null) {
      // TODO: manage case where word is very long with a limit to tolerant additional size (troll
      // of a player for example)
      String wrapped = WordUtils.wrap(description, LORE_LINE_SIZE);
      String[] split = wrapped.split("\\n");
      descriptionComponent = new ArrayList<>(split.length);
      for (String loreLine : split) {
        Component component = Component.text(loreLine);
        descriptionComponent.add(component);
      }
    }
    return descriptionComponent;
  }
}
