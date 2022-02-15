package fr.voltariuss.diagonia;

public abstract class AbstractBaseTest {

  protected AbstractBaseTest() {
    DiagoniaPlayerShopsTestInjector.inject(this);
  }
}
