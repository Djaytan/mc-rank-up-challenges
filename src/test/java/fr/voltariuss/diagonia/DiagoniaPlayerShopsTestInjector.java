package fr.voltariuss.diagonia;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class DiagoniaPlayerShopsTestInjector {

  /** Private constructor. */
  private DiagoniaPlayerShopsTestInjector() {}

  public static void inject(Object target) {
    Injector injector =
      Guice.createInjector(
        new GuiceGeneralTestModule());
    injector.injectMembers(target);
  }
}
