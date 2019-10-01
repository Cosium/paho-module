package com.cos.paho;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;

/** @author Réda Housni Alaoui */
public class PahoClientFactory {

  PahoClientFactory() {}

  public IMqttClient build(String serverURI, String clientId, MqttClientPersistence persistence)
      throws MqttException {
    return new MqttClient(serverURI, clientId, persistence);
  }
}
