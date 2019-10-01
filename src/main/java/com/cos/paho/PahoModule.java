package com.cos.paho;

import org.eclipse.paho.client.mqttv3.logging.LoggerFactory;

/** @author Réda Housni Alaoui */
public class PahoModule {

  private final PahoClientFactory clientFactory;

  public PahoModule() {
    LoggerFactory.setLogger(Slf4jLogger.class.getCanonicalName());
    this.clientFactory = new PahoClientFactory();
  }

  public PahoClientFactory clientFactory() {
    return clientFactory;
  }
}
