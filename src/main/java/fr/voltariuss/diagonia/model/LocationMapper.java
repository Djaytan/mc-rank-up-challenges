package fr.voltariuss.diagonia.model;

import fr.voltariuss.diagonia.model.dto.LocationDto;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class LocationMapper {

  private final Server server;

  @Inject
  public LocationMapper(@NotNull Server server) {
    this.server = server;
  }

  public @Contract("null -> null; !null -> !null") LocationDto toDto(@Nullable Location location) {
    LocationDto locationDto = null;
    if (location != null) {
      locationDto = LocationDto.builder()
        .worldName(location.getWorld().getName())
        .x(location.getX())
        .y(location.getY())
        .z(location.getZ())
        .pitch(location.getPitch())
        .yaw(location.getYaw())
        .build();
    }
    return locationDto;
  }

  public @Contract("null -> null; !null -> !null") Location fromDto(
      @Nullable LocationDto locationDto) {
    Location location = null;
    if (locationDto != null) {
      World world = server.getWorld(locationDto.getWorldName());
      location = new Location(
        world,
        locationDto.getX(),
        locationDto.getY(),
        locationDto.getZ(),
        locationDto.getPitch(),
        locationDto.getYaw());
    }
    return location;
  }
}
