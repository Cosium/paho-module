package com.cos.paho;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.junit.Ignore;
import org.junit.Test;

/** @author Réda Housni Alaoui */
public class KapuaTest {

  private static final boolean CLEAN_SESSION = false;

  private static final String SERVER_URI = "ws://localhost:61614";
  private static final String CLIENT_ID = "foo";
  private static final String ACCOUNT = "ACME123";
  private static final String USERNAME = "user123";
  private static final String PASSWORD = "Kapu@12345678";

  private static final String TOPIC_TEMPLATE = "%s/+/visited-demo";

  private final PahoModule pahoModule = new PahoModule();

  @Test
  @Ignore
  public void test() throws MqttException, InterruptedException {
    IMqttClient client =
        pahoModule
            .clientFactory()
            .build(
                SERVER_URI,
                CLIENT_ID,
                new MqttDefaultFilePersistence(System.getProperty("java.io.tmpdir")));
    MqttConnectOptions options = new MqttConnectOptions();
    options.setAutomaticReconnect(true);
    options.setCleanSession(CLEAN_SESSION);
    options.setUserName(USERNAME);
    options.setPassword(PASSWORD.toCharArray());
    client.connect(options);

    client.subscribe(
        String.format(TOPIC_TEMPLATE, ACCOUNT), (topic, message) -> System.out.println(message));
    Thread.sleep(Integer.MAX_VALUE);
  }
}
