package com.mauro.colella.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandlerOptions;
import io.vertx.ext.web.handler.graphql.GraphiQLOptions;

import graphql.GraphQL;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
	// Create a router object.
    Router router = Router.router(vertx);
    API api = new API();

    GraphQL graphQL = GraphQL.newGraphQL(api.getSchema()).build();
    GraphQLHandlerOptions graphQLOptions = new GraphQLHandlerOptions()
      .setGraphiQLOptions(new GraphiQLOptions()
        .setEnabled(true)
        .setGraphQLUri("\"/\"") // <-- ?
      );

    router.route("/").handler(GraphQLHandler.create(graphQL, graphQLOptions));

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
