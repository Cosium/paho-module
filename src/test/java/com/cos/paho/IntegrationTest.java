package com.cos.paho;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Test;

/** @author Réda Housni Alaoui */
public abstract class IntegrationTest {

  private static final byte[] MESSAGE_PAYLOAD = "hello world".getBytes();

  @Test
  public void message_sent_to_the_topic_is_received_by_the_consumer_subscribed_to_the_topic()
      throws MqttException, InterruptedException {
    String topic = UUID.randomUUID().toString();

    TestingPahoClient consumer = openClient(UUID.randomUUID().toString());

    CountDownLatch messageReception = consumer.expectMessage(topic, MESSAGE_PAYLOAD);

    TestingPahoClient producer = openClient(UUID.randomUUID().toString());
    producer.publish(topic, new MqttMessage(MESSAGE_PAYLOAD));

    messageReception.await(5, TimeUnit.SECONDS);
  }

  @Test
  public void
      message_sent_when_the_consumer_was_offline_is_received_when_the_consumer_is_back_online()
          throws MqttException, InterruptedException {
    String topic = UUID.randomUUID().toString();

    String consumerClientId = UUID.randomUUID().toString();
    // We register the consumer's client id then pass it offline
    openClient(consumerClientId).subscribe(topic).close();

    TestingPahoClient producer = openClient(UUID.randomUUID().toString());
    producer.publish(topic, new MqttMessage(MESSAGE_PAYLOAD));

    openClient(consumerClientId).expectMessage(topic, MESSAGE_PAYLOAD).await(5, TimeUnit.SECONDS);
  }

  protected abstract String serverUri();

  protected abstract String username();

  protected abstract String password();

  private TestingPahoClient openClient(String clientId) throws MqttException {
    return TestingPahoClient.builder()
        .serverUri(serverUri())
        .clientId(clientId)
        .cleanSession(true)
        .username(username())
        .password(password())
        .open();
  }
}
