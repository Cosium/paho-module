package com.cos.paho;

import java.util.ResourceBundle;
import org.eclipse.paho.client.mqttv3.logging.Logger;
import org.slf4j.LoggerFactory;

/** @author Réda Housni Alaoui */
class Slf4jLogger implements Logger {

  private ResourceBundle messageCatalog;

  @Override
  public void initialise(ResourceBundle messageCatalog, String loggerID, String resourceName) {
    this.messageCatalog = messageCatalog;
  }

  @Override
  public void setResourceName(String logContext) {
    // Do nothing
  }

  @Override
  public boolean isLoggable(int level) {
    return true;
  }

  @Override
  public void severe(String sourceClass, String sourceMethod, String msg) {
    severe(sourceClass, sourceMethod, msg, null);
  }

  @Override
  public void severe(String sourceClass, String sourceMethod, String msg, Object[] inserts) {
    severe(sourceClass, sourceMethod, msg, inserts, null);
  }

  @Override
  public void severe(
      String sourceClass, String sourceMethod, String msg, Object[] inserts, Throwable thrown) {
    log(SEVERE, sourceClass, sourceMethod, msg, inserts, thrown);
  }

  @Override
  public void warning(String sourceClass, String sourceMethod, String msg) {
    warning(sourceClass, sourceMethod, msg, null);
  }

  @Override
  public void warning(String sourceClass, String sourceMethod, String msg, Object[] inserts) {
    warning(sourceClass, sourceMethod, msg, inserts, null);
  }

  @Override
  public void warning(
      String sourceClass, String sourceMethod, String msg, Object[] inserts, Throwable thrown) {
    log(WARNING, sourceClass, sourceMethod, msg, inserts, thrown);
  }

  @Override
  public void info(String sourceClass, String sourceMethod, String msg) {
    info(sourceClass, sourceMethod, msg, null);
  }

  @Override
  public void info(String sourceClass, String sourceMethod, String msg, Object[] inserts) {
    info(sourceClass, sourceMethod, msg, inserts, null);
  }

  @Override
  public void info(
      String sourceClass, String sourceMethod, String msg, Object[] inserts, Throwable thrown) {
    log(INFO, sourceClass, sourceMethod, msg, inserts, thrown);
  }

  @Override
  public void config(String sourceClass, String sourceMethod, String msg) {
    config(sourceClass, sourceMethod, msg, null);
  }

  @Override
  public void config(String sourceClass, String sourceMethod, String msg, Object[] inserts) {
    config(sourceClass, sourceMethod, msg, inserts, null);
  }

  @Override
  public void config(
      String sourceClass, String sourceMethod, String msg, Object[] inserts, Throwable thrown) {
    log(CONFIG, sourceClass, sourceMethod, msg, inserts, thrown);
  }

  @Override
  public void fine(String sourceClass, String sourceMethod, String msg) {
    fine(sourceClass, sourceMethod, msg, null);
  }

  @Override
  public void fine(String sourceClass, String sourceMethod, String msg, Object[] inserts) {
    fine(sourceClass, sourceMethod, msg, inserts, null);
  }

  @Override
  public void fine(
      String sourceClass, String sourceMethod, String msg, Object[] inserts, Throwable ex) {
    log(FINE, sourceClass, sourceMethod, msg, inserts, ex);
  }

  @Override
  public void finer(String sourceClass, String sourceMethod, String msg) {
    finer(sourceClass, sourceMethod, msg, null);
  }

  @Override
  public void finer(String sourceClass, String sourceMethod, String msg, Object[] inserts) {
    finer(sourceClass, sourceMethod, msg, inserts, null);
  }

  @Override
  public void finer(
      String sourceClass, String sourceMethod, String msg, Object[] inserts, Throwable ex) {
    log(FINER, sourceClass, sourceMethod, msg, inserts, ex);
  }

  @Override
  public void finest(String sourceClass, String sourceMethod, String msg) {
    finest(sourceClass, sourceMethod, msg, null);
  }

  @Override
  public void finest(String sourceClass, String sourceMethod, String msg, Object[] inserts) {
    finest(sourceClass, sourceMethod, msg, inserts, null);
  }

  @Override
  public void finest(
      String sourceClass, String sourceMethod, String msg, Object[] inserts, Throwable ex) {
    log(FINEST, sourceClass, sourceMethod, msg, inserts, ex);
  }

  @Override
  public void log(
      int level,
      String sourceClass,
      String sourceMethod,
      String msg,
      Object[] inserts,
      Throwable thrown) {
    trace(level, sourceClass, sourceMethod, msg, inserts, thrown);
  }

  @Override
  public void trace(
      int level,
      String sourceClass,
      String sourceMethod,
      String msg,
      Object[] inserts,
      Throwable ex) {
    org.slf4j.Logger logger = LoggerFactory.getLogger(sourceClass);

    LevelLogger levelLogger = null;
    if (level == SEVERE && logger.isErrorEnabled()) {
      levelLogger = logger::error;
    } else if (level == WARNING && logger.isWarnEnabled()) {
      levelLogger = logger::warn;
    } else if (level == INFO && logger.isInfoEnabled()) {
      levelLogger = logger::info;
    } else if ((level == CONFIG) && logger.isDebugEnabled()) {
      levelLogger = logger::debug;
    } else if ((level == FINEST || level == FINE || level == FINER) && logger.isTraceEnabled()) {
      levelLogger = logger::trace;
    }

    if (levelLogger == null) {
      return;
    }

    levelLogger.log(formatMessage(msg, inserts), ex);
  }

  /** Copied from {@link java.util.Formatter#format(String, Object...)} */
  @Override
  public String formatMessage(String format, Object[] parameters) {
    if (messageCatalog != null) {
      try {
        format = messageCatalog.getString(format);
      } catch (java.util.MissingResourceException ex) {
        // Drop through.  Use record message as format
      }
    }
    // Do the formatting.
    try {
      if (parameters == null || parameters.length == 0) {
        // No parameters.  Just return format string.
        return format;
      }
      // Is it a java.text style format?
      // Ideally we could match with
      // Pattern.compile("\\{\\d").matcher(format).find())
      // However the cost is 14% higher, so we cheaply use indexOf
      // and charAt to look for that pattern.
      int index = -1;
      int fence = format.length() - 1;
      while ((index = format.indexOf('{', index + 1)) > -1) {
        if (index >= fence) break;
        char digit = format.charAt(index + 1);
        if (digit >= '0' & digit <= '9') {
          return java.text.MessageFormat.format(format, parameters);
        }
      }
      return format;

    } catch (Exception ex) {
      // Formatting failed: use localized format string.
      return format;
    }
  }

  @Override
  public void dumpTrace() {
    // Do nothing
  }

  private interface LevelLogger {
    void log(String message, Throwable throwable);
  }
}
