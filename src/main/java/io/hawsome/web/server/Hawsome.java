package io.hawsome.web.server;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class Hawsome {

  public static void main(String[] args) {
    final VertxOptions vOpt = new VertxOptions();
//    vOpt.setBlockedThreadCheckInterval(60000);

    final Vertx v = Vertx.vertx(vOpt);

    final DeploymentOptions dOpt = new DeploymentOptions();
    v.deployVerticle(new HttpServerVerticle(), dOpt);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> v.close(event -> {
      if (event.succeeded()) {
        System.out.println("shut 'er down");
      } else {
        System.out.println("Failed to shutdown gracefully");
      }
    })));
  }

}
