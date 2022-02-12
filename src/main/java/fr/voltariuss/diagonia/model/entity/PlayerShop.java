package fr.voltariuss.diagonia.model.entity;

import fr.voltariuss.diagonia.model.LocationConverter;
import java.util.UUID;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.Material;

@Entity
@Table(name = "diagonia_ps_playershop")
@ToString
@Getter
@Setter
public class PlayerShop {

  @Id
  @GeneratedValue
  @Column(nullable = false, updatable = false)
  private long id;

  @Column(nullable = false, unique = true, updatable = false)
  private UUID owner;

  @Column(length = 150)
  private String description;

  private Material itemRepresentation;

  @Convert(converter = LocationConverter.class)
  private Location tpLocation;

  @Column(nullable = false)
  private boolean isActive;
}
