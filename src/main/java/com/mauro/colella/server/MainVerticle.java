package com.mauro.colella.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.core.http.HttpServerResponse;


import graphql.GraphQL;
import io.github.cdimascio.dotenv.Dotenv;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    // Create a router object.
    Dotenv dotenv = Dotenv.load();

    Router router = Router.router(vertx);
    API api = new API();

    router.route("/").handler(api.getHandler());

    vertx.createHttpServer().requestHandler(router::accept)
    .listen(8888, http -> {
      if (http.succeeded()) {
        startFuture.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startFuture.fail(http.cause());
      }
    });
  }
}
