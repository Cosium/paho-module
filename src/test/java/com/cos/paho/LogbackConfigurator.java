package com.cos.paho;

import ch.qos.logback.classic.BasicConfigurator;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;

/** @author Réda Housni Alaoui */
public class LogbackConfigurator extends BasicConfigurator {

  @Override
  public void configure(LoggerContext lc) {
    super.configure(lc);
    lc.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO);
  }
}
