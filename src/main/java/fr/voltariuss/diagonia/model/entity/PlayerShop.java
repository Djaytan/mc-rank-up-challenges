package fr.voltariuss.diagonia.model.entity;

import fr.voltariuss.diagonia.model.LocationDtoConverter;
import fr.voltariuss.diagonia.model.UUIDConverter;
import fr.voltariuss.diagonia.model.dto.LocationDto;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Material;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "diagonia_ps_playershop")
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class PlayerShop {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "ps_id", nullable = false, updatable = false)
  private long id;

  @NaturalId
  @Column(name = "ps_owner_uuid", nullable = false, unique = true, updatable = false)
  @Convert(converter = UUIDConverter.class)
  @Setter
  @NonNull
  private UUID ownerUuid;

  @Column(name = "ps_description", length = 150)
  @Setter
  private String description;

  @Column(name = "ps_item_icon")
  @Enumerated(EnumType.ORDINAL)
  @Setter
  private Material itemIcon;

  @Column(name = "ps_tp_location")
  @Convert(converter = LocationDtoConverter.class)
  @Setter
  private LocationDto tpLocation;

  @Column(name = "ps_is_active", nullable = false)
  @Setter
  private boolean isActive;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PlayerShop that)) {
      return false;
    }
    return new EqualsBuilder().append(ownerUuid, that.ownerUuid).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(ownerUuid).toHashCode();
  }

  public boolean hasItemIcon() {
    return itemIcon != null;
  }
}
