package fr.voltariuss.diagonia.model;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

public class JpaDaoException extends RuntimeException {

  public JpaDaoException(@NotNull String message) {
    super(message);
    Preconditions.checkNotNull(message);
  }

  public JpaDaoException(@NotNull String message, @NotNull Throwable cause) {
    super(message, cause);
  }
}
