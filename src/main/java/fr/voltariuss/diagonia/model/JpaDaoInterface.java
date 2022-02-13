package fr.voltariuss.diagonia.model;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface JpaDaoInterface<T, I extends Serializable> {

  void persist(@NotNull T entity);

  void update(@NotNull T entity);

  @NotNull
  Optional<T> findById(@Nullable I id);

  void delete(@NotNull T entity);

  @NotNull
  List<T> findAll();
}
