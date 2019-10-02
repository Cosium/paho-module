package com.cos.paho;

import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

/** @author Réda Housni Alaoui */
public class ActiveMQTest extends IntegrationTest {

  private static final Logger LOG = LoggerFactory.getLogger(ActiveMQTest.class);

  private static final int WEBSOCKET_PORT = 61614;

  @ClassRule
  public static GenericContainer activeMQ =
      new GenericContainer("rmohr/activemq:5.14.5")
          .withLogConsumer(new Slf4jLogConsumer(LOG))
          .withExposedPorts(WEBSOCKET_PORT);

  @Override
  protected String serverUri() {
    return "ws://localhost:" + activeMQ.getMappedPort(WEBSOCKET_PORT) + "/";
  }

  @Override
  protected String username() {
    return "admin";
  }

  @Override
  protected String password() {
    return "admin";
  }
}
