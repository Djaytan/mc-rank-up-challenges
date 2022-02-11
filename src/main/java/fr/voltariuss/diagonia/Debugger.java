package fr.voltariuss.diagonia;

import org.slf4j.Logger;

import javax.inject.Named;
import javax.inject.Singleton;

/** Debugger class for debug logging. */
@Singleton
public class Debugger {

  private static final String DEBUG_MODE_PREFIX = "[DEBUG]";

  private final Logger logger;
  private final boolean debugMode;

  /**
   * Constructor.
   *
   * @param logger The plugin's logger.
   * @param debugMode "true" if the debug mode for the plugin is enabled, "false" otherwise.
   */
  public Debugger(Logger logger, @Named("debugMode") boolean debugMode) {
    this.logger = logger;
    this.debugMode = debugMode;
  }

  /**
   * Log debug messages only if "debugMode" is enabled.
   *
   * <p>It's true that varargs are not used here and can lead to lose of performances, but this
   * happens only during debug phases...
   *
   * @param logMessage The message to log in debug mode.
   */
  public void debug(String logMessage) {
    if (debugMode) {
      logger.info("{} {}", DEBUG_MODE_PREFIX, logMessage);
    }
  }
}
