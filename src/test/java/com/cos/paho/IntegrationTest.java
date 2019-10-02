package com.cos.paho;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Test;

/** @author Réda Housni Alaoui */
public abstract class IntegrationTest {

  @Test
  public void message_sent_to_the_topic_is_received_by_the_consumer_subscribed_to_the_topic()
      throws MqttException, InterruptedException, TimeoutException, ExecutionException {
    String topic = topicPrefix() + UUID.randomUUID().toString();

    TestingPahoClient consumer = openClient(UUID.randomUUID().toString());

    CompletableFuture<Void> messageReception =
        consumer.expectMessage(topicPrefix() + "+", messagePayload());

    TestingPahoClient producer = openClient(UUID.randomUUID().toString());
    producer.publish(topic, new MqttMessage(messagePayload()));

    messageReception.get(5, TimeUnit.SECONDS);
  }

  @Test
  public void
      message_sent_when_the_consumer_was_offline_is_received_when_the_consumer_is_back_online()
          throws MqttException, InterruptedException, TimeoutException, ExecutionException {
    String topic = topicPrefix() + UUID.randomUUID().toString();

    String consumerClientId = UUID.randomUUID().toString();
    // We register the consumer's client id then pass it offline
    openClient(consumerClientId).subscribe(topicPrefix() + "+").close();

    TestingPahoClient producer = openClient(UUID.randomUUID().toString());
    producer.publish(topic, new MqttMessage(messagePayload()));

    openClient(consumerClientId)
        .expectMessage(topicPrefix() + "+", messagePayload())
        .get(5, TimeUnit.SECONDS);
  }

  protected abstract byte[] messagePayload();

  protected abstract String topicPrefix();

  protected abstract String serverUri();

  protected abstract String username();

  protected abstract String password();

  private TestingPahoClient openClient(String clientId) throws MqttException {
    return TestingPahoClient.builder()
        .serverUri(serverUri())
        .clientId(clientId)
        .cleanSession(false)
        .username(username())
        .password(password())
        .open();
  }
}
