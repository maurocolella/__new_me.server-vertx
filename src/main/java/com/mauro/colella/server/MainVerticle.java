package com.mauro.colella.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandlerOptions;
import io.vertx.ext.web.handler.graphql.GraphiQLOptions;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class MainVerticle extends AbstractVerticle {

  private GraphQL setupGraphQL() {
    String schema = "type Query{hello: String}";

    SchemaParser schemaParser = new SchemaParser();
    TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

    RuntimeWiring runtimeWiring = newRuntimeWiring()
            .type("Query", builder -> builder.dataFetcher("hello", new StaticDataFetcher("world")))
            .build();

    SchemaGenerator schemaGenerator = new SchemaGenerator();
    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

    GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
    return build;
  }

  @Override
  public void start(Future<Void> startFuture) throws Exception {
	// Create a router object.
    Router router = Router.router(vertx);
    GraphQL graphQL = setupGraphQL();
    GraphQLHandlerOptions graphQLOptions = new GraphQLHandlerOptions()
      .setGraphiQLOptions(new GraphiQLOptions()
        .setEnabled(true)
      );

    router.route("/").handler(routingContext -> {
      HttpServerResponse response = routingContext.response();
      response
        .putHeader("content-type", "text/html")
        .end("<h1>Hello from my first Vert.x 3 application</h1>");
    });

    router.route("/graphql").handler(GraphQLHandler.create(graphQL, graphQLOptions));

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
