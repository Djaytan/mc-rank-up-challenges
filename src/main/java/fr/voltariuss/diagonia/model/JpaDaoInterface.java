package fr.voltariuss.diagonia.model;

import java.io.Serializable;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface JpaDaoInterface<T, I extends Serializable> {

  void persist(@NotNull T entity);

  void update(@NotNull T entity);

  T findById(@NotNull I id);

  void delete(@NotNull T entity);

  @NotNull
  List<T> findAll();
}
