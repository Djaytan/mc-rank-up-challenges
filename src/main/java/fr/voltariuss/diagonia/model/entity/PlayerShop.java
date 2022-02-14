package fr.voltariuss.diagonia.model.entity;

import fr.voltariuss.diagonia.model.LocationDtoConverter;
import fr.voltariuss.diagonia.model.dto.LocationDto;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.builder.EqualsBuilder;
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
  @Column(name = "ps_id", nullable = false, updatable = false)
  private long id;

  @Column(name = "ps_owner_uuid", nullable = false, unique = true, updatable = false)
  @Setter
  @NotNull
  private UUID ownerUuid;

  @Column(name = "ps_description", length = 150)
  @Setter
  @Nullable
  private String description;

  @Column(name = "ps_item_icon")
  @Enumerated(EnumType.ORDINAL)
  @Setter
  @Nullable
  private Material itemIcon;

  @Column(name = "ps_tp_location")
  @Convert(converter = LocationDtoConverter.class)
  @Setter
  @Nullable
  private LocationDto tpLocation;

  @Column(name = "ps_is_active", nullable = false)
  @Setter
  private boolean isActive;
}
