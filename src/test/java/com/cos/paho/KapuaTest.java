package com.cos.paho;

import java.io.File;
import java.net.URISyntaxException;
import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

/** @author Réda Housni Alaoui */
public class KapuaTest extends IntegrationTest {

  private static final Logger LOG = LoggerFactory.getLogger(KapuaTest.class);

  private static final String BROKER_SERVICE_NAME = "broker";
  private static final int BROKER_WEBSOCKET_PORT = 61614;

  @ClassRule
  public static DockerComposeContainer kapua =
      new DockerComposeContainer(dockerComposeFile())
          .withLogConsumer(BROKER_SERVICE_NAME, new Slf4jLogConsumer(LOG))
          .withExposedService(BROKER_SERVICE_NAME, BROKER_WEBSOCKET_PORT);

  @Override
  protected String serverUri() {
    return "ws://"
        + kapua.getServiceHost(BROKER_SERVICE_NAME, BROKER_WEBSOCKET_PORT)
        + ":"
        + kapua.getServicePort(BROKER_SERVICE_NAME, BROKER_WEBSOCKET_PORT)
        + "/";
  }

  @Override
  protected String username() {
    return "kapua-sys";
  }

  @Override
  protected String password() {
    return "kapua-password";
  }

  private static File dockerComposeFile() {
    try {
      return new File(KapuaTest.class.getResource("/kapua/docker-compose.yml").toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
