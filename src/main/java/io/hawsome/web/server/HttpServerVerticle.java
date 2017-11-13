package io.hawsome.web.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class HttpServerVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    final Router r = Router.router(vertx);

    final StaticHandler h = StaticHandler.create()
      .setAllowRootFileSystemAccess(true)
      .setWebRoot(System.getProperty("user.dir"));

    r.route("/*").handler(h);

    final HttpServer s = vertx.createHttpServer();

    s.requestHandler(r::accept).listen(8080, ar -> {
      if (ar.succeeded()) {
        System.out.println("HTTP Server started on 8080");
        startFuture.complete();
      } else {
        System.out.println("Failed to start HTTP server");
        startFuture.fail(ar.cause());
      }
    });
  }

  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    super.stop(stopFuture);
  }

}
