/*
 * Copyright (c) 2022 - Loïc DUBOIS-TERMOZ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.voltariuss.diagonia;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Diagonia's logger.
 *
 * <p>The purpose of this implementation is to overcome the Bukkit's limitation which didn't display
 * any debug message in console. And because it's not possible to change the Log4J2 config file to
 * fix this issue properly, this class was created to display debug messages through the INFO level
 * canal.
 *
 * <p>For others log levels (warn, error, trace, ...) the implementation only redirect to the SLF4J
 * implementation.
 *
 * @author Voltariuss
 */
// TODO: rename RemakeLogger
@Singleton
public final class DiagoniaLogger implements Logger {

  private static final String DEBUG_MODE_PREFIX = "[DEBUG]";

  private final Logger logger;
  private final boolean debugMode;

  /**
   * Constructor.
   *
   * @param logger The Bukkit's SLF4J logger to be used in the implementation.
   * @param debugMode <code>true</code> if the debug mode is enabled, <code>false</code> otherwise.
   */
  @Inject
  public DiagoniaLogger(@NotNull final Logger logger, @Named("debugMode") final boolean debugMode) {
    this.logger = logger;
    this.debugMode = debugMode;
  }

  @Override
  @Contract(pure = true)
  public String getName() {
    return logger.getName();
  }

  @Override
  @Contract(pure = true)
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  @Override
  @Contract(pure = true)
  public void trace(@Nullable final String msg) {
    logger.trace(msg);
  }

  @Override
  @Contract(pure = true)
  public void trace(@Nullable final String format, @Nullable final Object arg) {
    logger.trace(format, arg);
  }

  @Override
  @Contract(pure = true)
  public void trace(
      @Nullable final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
    logger.trace(format, arg1, arg2);
  }

  @Override
  @Contract(pure = true)
  public void trace(@Nullable final String format, @Nullable final Object... args) {
    logger.trace(format, args);
  }

  @Override
  @Contract(pure = true)
  public void trace(@Nullable final String msg, @Nullable final Throwable throwable) {
    logger.trace(msg, throwable);
  }

  @Override
  @Contract(pure = true)
  public boolean isTraceEnabled(@Nullable final Marker marker) {
    return logger.isTraceEnabled(marker);
  }

  @Override
  @Contract(pure = true)
  public void trace(@Nullable final Marker marker, @Nullable final String msg) {
    logger.trace(marker, msg);
  }

  @Override
  @Contract(pure = true)
  public void trace(
      @Nullable final Marker marker, @Nullable final String format, @Nullable final Object arg) {
    logger.trace(marker, format, arg);
  }

  @Override
  @Contract(pure = true)
  public void trace(
      @Nullable final Marker marker,
      @Nullable final String format,
      @Nullable final Object arg1,
      @Nullable final Object arg2) {
    logger.trace(marker, format, arg1, arg2);
  }

  @Override
  @Contract(pure = true)
  public void trace(
      @Nullable final Marker marker,
      @Nullable final String format,
      @Nullable final Object... args) {
    logger.trace(marker, format, args);
  }

  @Override
  @Contract(pure = true)
  public void trace(
      @Nullable final Marker marker,
      @Nullable final String msg,
      @Nullable final Throwable throwable) {
    logger.trace(marker, msg, throwable);
  }

  @Override
  @Contract(pure = true)
  public boolean isDebugEnabled() {
    return logger.isInfoEnabled() && debugMode;
  }

  @Override
  @Contract(pure = true)
  public void debug(@Nullable final String msg) {
    if (isDebugEnabled()) {
      logger.info(debugFormat(msg));
    }
  }

  @Override
  @Contract(pure = true)
  public void debug(@Nullable final String format, @Nullable final Object arg) {
    if (isDebugEnabled()) {
      logger.info(debugFormat(format), arg);
    }
  }

  @Override
  @Contract(pure = true)
  public void debug(
      @Nullable final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
    if (isDebugEnabled()) {
      logger.info(debugFormat(format), arg1, arg2);
    }
  }

  @Override
  @Contract(pure = true)
  public void debug(@Nullable final String format, @Nullable final Object... args) {
    if (isDebugEnabled()) {
      logger.info(debugFormat(format), args);
    }
  }

  @Override
  @Contract(pure = true)
  public void debug(@Nullable final String msg, @Nullable final Throwable throwable) {
    if (isDebugEnabled()) {
      logger.info(debugFormat(msg), throwable);
    }
  }

  @Override
  @Contract(pure = true)
  public boolean isDebugEnabled(@Nullable final Marker marker) {
    // Ignore markers
    return debugMode;
  }

  @Override
  @Contract(pure = true)
  public void debug(@Nullable final Marker marker, @Nullable final String msg) {
    if (isDebugEnabled()) {
      logger.info(marker, debugFormat(msg));
    }
  }

  @Override
  @Contract(pure = true)
  public void debug(
      @Nullable final Marker marker, @Nullable final String format, @Nullable final Object arg) {
    if (isDebugEnabled()) {
      logger.info(marker, debugFormat(format), arg);
    }
  }

  @Override
  @Contract(pure = true)
  public void debug(
      @Nullable final Marker marker,
      @Nullable final String format,
      @Nullable final Object arg1,
      @Nullable final Object arg2) {
    if (isDebugEnabled()) {
      logger.info(marker, debugFormat(format), arg1, arg2);
    }
  }

  @Override
  @Contract(pure = true)
  public void debug(
      @Nullable final Marker marker,
      @Nullable final String format,
      @Nullable final Object... args) {
    if (isDebugEnabled()) {
      logger.info(marker, debugFormat(format), args);
    }
  }

  @Override
  @Contract(pure = true)
  public void debug(
      @Nullable final Marker marker, @Nullable final String msg, Throwable throwable) {
    if (isDebugEnabled()) {
      logger.info(marker, debugFormat(msg), throwable);
    }
  }

  @Contract(pure = true)
  private @NotNull String debugFormat(@Nullable final String msg) {
    return String.format("%1$s %2$s", DEBUG_MODE_PREFIX, msg);
  }

  @Override
  @Contract(pure = true)
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  @Override
  @Contract(pure = true)
  public void info(@Nullable final String msg) {
    logger.info(msg);
  }

  @Override
  @Contract(pure = true)
  public void info(@Nullable final String format, @Nullable final Object arg) {
    logger.info(format, arg);
  }

  @Override
  @Contract(pure = true)
  public void info(
      @Nullable final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
    logger.info(format, arg1, arg2);
  }

  @Override
  @Contract(pure = true)
  public void info(@Nullable final String format, @Nullable final Object... args) {
    logger.info(format, args);
  }

  @Override
  @Contract(pure = true)
  public void info(@Nullable final String msg, @Nullable final Throwable throwable) {
    logger.info(msg, throwable);
  }

  @Override
  @Contract(pure = true)
  public boolean isInfoEnabled(@Nullable final Marker marker) {
    return logger.isInfoEnabled(marker);
  }

  @Override
  @Contract(pure = true)
  public void info(@Nullable final Marker marker, @Nullable final String msg) {
    logger.info(marker, msg);
  }

  @Override
  @Contract(pure = true)
  public void info(
      @Nullable final Marker marker, @Nullable final String format, @Nullable final Object arg) {
    logger.info(marker, format, arg);
  }

  @Override
  @Contract(pure = true)
  public void info(
      @Nullable final Marker marker,
      @Nullable final String format,
      @Nullable final Object arg1,
      @Nullable final Object arg2) {
    logger.info(marker, format, arg1, arg2);
  }

  @Override
  @Contract(pure = true)
  public void info(
      @Nullable final Marker marker,
      @Nullable final String format,
      @Nullable final Object... args) {
    logger.info(marker, format, args);
  }

  @Override
  @Contract(pure = true)
  public void info(
      @Nullable final Marker marker,
      @Nullable final String msg,
      @Nullable final Throwable throwable) {
    logger.info(marker, msg, throwable);
  }

  @Override
  @Contract(pure = true)
  public boolean isWarnEnabled() {
    return logger.isWarnEnabled();
  }

  @Override
  @Contract(pure = true)
  public void warn(@Nullable final String msg) {
    logger.warn(msg);
  }

  @Override
  @Contract(pure = true)
  public void warn(@Nullable final String format, @Nullable final Object arg) {
    logger.warn(format, arg);
  }

  @Override
  @Contract(pure = true)
  public void warn(
      @Nullable final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
    logger.warn(format, arg1, arg2);
  }

  @Override
  @Contract(pure = true)
  public void warn(@Nullable final String format, @Nullable final Object... args) {
    logger.warn(format, args);
  }

  @Override
  @Contract(pure = true)
  public void warn(@Nullable final String msg, @Nullable final Throwable throwable) {
    logger.warn(msg, throwable);
  }

  @Override
  @Contract(pure = true)
  public boolean isWarnEnabled(@Nullable final Marker marker) {
    return logger.isWarnEnabled(marker);
  }

  @Override
  @Contract(pure = true)
  public void warn(@Nullable final Marker marker, @Nullable final String msg) {
    logger.warn(marker, msg);
  }

  @Override
  @Contract(pure = true)
  public void warn(
      @Nullable final Marker marker, @Nullable final String format, @Nullable final Object arg) {
    logger.warn(marker, format, arg);
  }

  @Override
  @Contract(pure = true)
  public void warn(
      @Nullable final Marker marker,
      @Nullable final String format,
      @Nullable final Object arg1,
      @Nullable final Object arg2) {
    logger.warn(marker, format, arg1, arg2);
  }

  @Override
  @Contract(pure = true)
  public void warn(
      @Nullable final Marker marker,
      @Nullable final String format,
      @Nullable final Object... args) {
    logger.warn(marker, format, args);
  }

  @Override
  @Contract(pure = true)
  public void warn(
      @Nullable final Marker marker,
      @Nullable final String msg,
      @Nullable final Throwable throwable) {
    logger.warn(marker, msg, throwable);
  }

  @Override
  @Contract(pure = true)
  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  @Override
  @Contract(pure = true)
  public void error(@Nullable final String msg) {
    logger.error(msg);
  }

  @Override
  @Contract(pure = true)
  public void error(@Nullable final String format, @Nullable final Object arg) {
    logger.error(format, arg);
  }

  @Override
  @Contract(pure = true)
  public void error(
      @Nullable final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
    logger.error(format, arg1, arg2);
  }

  @Override
  @Contract(pure = true)
  public void error(@Nullable final String format, @Nullable final Object... args) {
    logger.error(format, args);
  }

  @Override
  @Contract(pure = true)
  public void error(@Nullable final String msg, @Nullable final Throwable throwable) {
    logger.error(msg, throwable);
  }

  @Override
  @Contract(pure = true)
  public boolean isErrorEnabled(@Nullable final Marker marker) {
    return logger.isErrorEnabled(marker);
  }

  @Override
  @Contract(pure = true)
  public void error(@Nullable final Marker marker, @Nullable final String msg) {
    logger.error(marker, msg);
  }

  @Override
  @Contract(pure = true)
  public void error(
      @Nullable final Marker marker, @Nullable final String format, @Nullable final Object arg) {
    logger.error(marker, format, arg);
  }

  @Override
  @Contract(pure = true)
  public void error(
      @Nullable final Marker marker,
      @Nullable final String format,
      @Nullable final Object arg1,
      @Nullable final Object arg2) {
    logger.error(marker, format, arg1, arg2);
  }

  @Override
  @Contract(pure = true)
  public void error(
      @Nullable final Marker marker,
      @Nullable final String format,
      @Nullable final Object... args) {
    logger.error(marker, format, args);
  }

  @Override
  @Contract(pure = true)
  public void error(
      @Nullable final Marker marker,
      @Nullable final String msg,
      @Nullable final Throwable throwable) {
    logger.error(marker, msg, throwable);
  }
}
