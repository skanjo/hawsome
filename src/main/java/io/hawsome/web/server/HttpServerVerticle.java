/*
 * Copyright (C) 2017 Samer Kanjo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hawsome.web.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.VirtualHostHandler;

public class HttpServerVerticle extends AbstractVerticle {

  private static final Handler<RoutingContext> DEFAULT_HANDLER = event -> event.response().setStatusCode(444).end();

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    final Router r = Router.router(vertx);

    final StaticHandler h = StaticHandler.create()
      .setAllowRootFileSystemAccess(true)
      .setWebRoot(webroot());

    final VirtualHostHandler vhost = VirtualHostHandler.create(host(), h);

    r.route("/*").handler(vhost);
    r.route("/*").handler(DEFAULT_HANDLER);

    final HttpServer s = vertx.createHttpServer();

    s.requestHandler(r::accept).listen(port(), ar -> {
      if (ar.succeeded()) {
        System.out.println("Hawsome started!");
        startFuture.complete();
      } else {
        System.out.println("Failed to start, that is not Hawsome :(");
        startFuture.fail(ar.cause());
      }
    });
  }

  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    super.stop(stopFuture);
  }

  private String host() {
    final String host;
    if (config().containsKey("host")) {
      host = config().getString("host");
    } else {
      host = "localhost";
    }
    return host;
  }

  private int port() {
    final int port;
    if (config().containsKey("port")) {
      port = config().getInteger("port");
    } else {
      port = 2017;
    }
    return port;
  }

  private String webroot() {
    final String webroot;
    if (config().containsKey("webroot")) {
      webroot = config().getString("webroot");
    } else {
      webroot = System.getProperty("user.dir");
    }
    return webroot;
  }
}
