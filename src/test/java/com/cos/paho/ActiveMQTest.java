package com.cos.paho;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

/** @author Réda Housni Alaoui */
public class ActiveMQTest {

  private static final Logger LOG = LoggerFactory.getLogger(ActiveMQTest.class);

  private static final int WEBSOCKET_PORT = 61614;

  @ClassRule
  public static GenericContainer activeMQ =
      new GenericContainer("rmohr/activemq:5.14.5")
          .withLogConsumer(new Slf4jLogConsumer(LOG))
          .withExposedPorts(WEBSOCKET_PORT);

  private static final byte[] MESSAGE_PAYLOAD = "hello world".getBytes();

  @Test
  public void message_sent_to_the_topic_is_received_by_the_consumer_subscribed_to_the_topic()
      throws MqttException, InterruptedException {
    String topic = UUID.randomUUID().toString();

    TestingPahoClient consumer = createClient(UUID.randomUUID().toString());

    CountDownLatch messageReception = consumer.expectMessage(topic, MESSAGE_PAYLOAD);

    TestingPahoClient producer = createClient(UUID.randomUUID().toString());
    producer.publish(topic, new MqttMessage(MESSAGE_PAYLOAD));

    messageReception.await(30, TimeUnit.SECONDS);
  }

  @Test
  public void
      message_sent_when_the_consumer_was_offline_is_received_when_the_consumer_is_back_online()
          throws MqttException, InterruptedException {
    String topic = UUID.randomUUID().toString();

    String consumerClientId = UUID.randomUUID().toString();
    // We register the consumer's client id then pass it offline
    createClient(consumerClientId).subscribe(topic).close();

    TestingPahoClient producer = createClient(UUID.randomUUID().toString());
    producer.publish(topic, new MqttMessage(MESSAGE_PAYLOAD));

    createClient(consumerClientId)
        .expectMessage(topic, MESSAGE_PAYLOAD)
        .await(30, TimeUnit.SECONDS);
  }

  private TestingPahoClient createClient(String clientId) throws MqttException {
    return TestingPahoClient.builder()
        .serverUri("ws://localhost:" + activeMQ.getMappedPort(WEBSOCKET_PORT) + "/")
        .clientId(clientId)
        .cleanSession(false)
        .username("admin")
        .password("admin")
        .open();
  }
}
