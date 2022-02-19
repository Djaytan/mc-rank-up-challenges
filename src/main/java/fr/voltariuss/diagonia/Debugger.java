package fr.voltariuss.diagonia;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/** Debugger class for debug logging. */
@Singleton
public class Debugger {

  private static final String DEBUG_MODE_PREFIX = "[DEBUG] ";

  private final Logger logger;
  private final boolean debugMode;

  /**
   * Constructor.
   *
   * @param logger The plugin's logger.
   * @param debugMode "true" if the debug mode for the plugin is enabled, "false" otherwise.
   */
  @Inject
  public Debugger(@NotNull Logger logger, @Named("debugMode") boolean debugMode) {
    this.logger = logger;
    this.debugMode = debugMode;
  }

  public boolean isDebugEnabled() {
    return debugMode;
  }

  @SuppressWarnings({"java:S2629", "java:S3457"})
  public void debug(String logMessage, Object... args) {
    if (debugMode) {
      logger.info(DEBUG_MODE_PREFIX + logMessage, args);
    }
  }

  @SuppressWarnings({"java:S2629", "java:S3457"})
  public void debug(String logMessage, Throwable cause) {
    if (debugMode) {
      logger.info(DEBUG_MODE_PREFIX + logMessage, cause);
    }
  }
}
