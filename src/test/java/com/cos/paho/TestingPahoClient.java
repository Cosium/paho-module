package com.cos.paho;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/** @author Réda Housni Alaoui */
public class TestingPahoClient implements AutoCloseable {

  private final IMqttClient client;

  private TestingPahoClient(Builder builder) throws MqttException {
    client =
        new PahoModule()
            .clientFactory()
            .build(
                builder.serverUri,
                builder.clientId,
                new MqttDefaultFilePersistence(System.getProperty("java.io.tmpdir")));
    MqttConnectOptions options = new MqttConnectOptions();
    options.setAutomaticReconnect(true);
    options.setCleanSession(builder.cleanSession);
    options.setUserName(builder.username);
    if (builder.password != null) {
      options.setPassword(builder.password.toCharArray());
    }
    client.connect(options);
  }

  public static Builder builder() {
    return new Builder();
  }

  public TestingPahoClient subscribe(String topic) throws MqttException {
    client.subscribe(topic);
    return this;
  }

  public void subscribe(String topic, IMqttMessageListener messageListener) throws MqttException {
    client.subscribe(topic, messageListener);
  }

  public void publish(String topic, MqttMessage message) throws MqttException {
    client.publish(topic, message);
  }

  public CompletableFuture<Void> expectMessage(String topic, byte[] messagePayload)
      throws MqttException {
    CountDownLatch countDownLatch = new CountDownLatch(1);

    client.subscribe(
        topic,
        (t, message) -> {
          if (!Arrays.equals(message.getPayload(), messagePayload)) {
            return;
          }
          countDownLatch.countDown();
        });

    return CompletableFuture.runAsync(
        () -> {
          try {
            countDownLatch.await();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public void close() {
    try {
      client.disconnect();
      client.close();
    } catch (MqttException e) {
      throw new RuntimeException(e);
    }
  }

  public static class Builder {

    private String serverUri;
    private String clientId;
    private boolean cleanSession = true;
    private String username;
    private String password;

    private Builder() {}

    public Builder serverUri(String serverUri) {
      this.serverUri = serverUri;
      return this;
    }

    public Builder clientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    public Builder cleanSession(boolean cleanSession) {
      this.cleanSession = cleanSession;
      return this;
    }

    public Builder username(String username) {
      this.username = username;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public TestingPahoClient open() throws MqttException {
      return new TestingPahoClient(this);
    }
  }
}
