package fr.voltariuss.diagonia.model.service;

import fr.voltariuss.diagonia.DiagoniaException;
import org.jetbrains.annotations.NotNull;

public class EconomyException extends DiagoniaException {

  public EconomyException(@NotNull String message) {
    super(message);
  }
}
