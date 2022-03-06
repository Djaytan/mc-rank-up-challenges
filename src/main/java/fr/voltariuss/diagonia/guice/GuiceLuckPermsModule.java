package fr.voltariuss.diagonia.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.track.TrackManager;
import org.jetbrains.annotations.NotNull;

public class GuiceLuckPermsModule extends AbstractModule {

  private final LuckPerms luckPerms;

  public GuiceLuckPermsModule() {
    luckPerms = LuckPermsProvider.get();
  }

  @Provides
  @Singleton
  public @NotNull LuckPerms provideLuckPerms() {
    return luckPerms;
  }

  @Provides
  @Singleton
  public @NotNull UserManager provideUserManager() {
    return luckPerms.getUserManager();
  }

  @Provides
  @Singleton
  public @NotNull GroupManager provideGroupManager() {
    return luckPerms.getGroupManager();
  }

  @Provides
  @Singleton
  public @NotNull TrackManager provideTrackManager() {
    return luckPerms.getTrackManager();
  }
}
