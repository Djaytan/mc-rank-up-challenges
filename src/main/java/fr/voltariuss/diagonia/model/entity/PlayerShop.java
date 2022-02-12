package fr.voltariuss.diagonia.model.entity;

import fr.voltariuss.diagonia.model.LocationConverter;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "diagonia_ps_playershop")
@ToString
@Getter
@NoArgsConstructor
public class PlayerShop {

  @Id
  @GeneratedValue
  @Column(nullable = false, updatable = false)
  private long id;

  @Column(nullable = false, unique = true, updatable = false)
  @Setter
  @NotNull
  private UUID owner;

  @Column(length = 150)
  @Setter
  @Nullable
  private String description;

  @Enumerated(EnumType.ORDINAL)
  @Setter
  @Nullable
  private Material itemRepresentation;

  @Convert(converter = LocationConverter.class)
  @Setter
  @Nullable
  private Location tpLocation;

  @Column(nullable = false)
  @Setter
  private boolean isActive;
}
