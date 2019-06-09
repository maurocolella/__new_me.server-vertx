package com.mauro.colella.server;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandlerOptions;
import io.vertx.ext.web.handler.graphql.GraphiQLOptions;

import graphql.ExecutionResult;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;
// import org.apache.commons.text.StringEscapeUtils;

import java.io.File;
import java.io.FileReader;

public class API {
  private GraphQLSchema schema = null;
  private GraphQL graphQL = null;
  // private String baseUrl = "/";

  public API() {
    File schemaDefinition = loadSchema("resources/schema.graphqls");

    SchemaParser schemaParser = new SchemaParser();
    TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schemaDefinition);

    RuntimeWiring runtimeWiring = newRuntimeWiring()
            .type("Query", builder -> builder.dataFetcher("hello", new StaticDataFetcher("world")))
            .build();

    SchemaGenerator schemaGenerator = new SchemaGenerator();
    this.schema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    this.graphQL = GraphQL.newGraphQL(this.schema).build();
  }

  public GraphQLHandler getHandler() {
    GraphQLHandlerOptions graphQLOptions = new GraphQLHandlerOptions()
      .setGraphiQLOptions(new GraphiQLOptions()
        .setEnabled(true)
        .setGraphQLUri("\"/\"") // <-- ? "\"/\"" StringEscapeUtils.escapeJava(baseUrl)
      );

    return GraphQLHandler.create(this.graphQL, graphQLOptions);
  }

  private File loadSchema(String path) {
    File file = null;
    try {
      file = new File(path);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return file;
  }
}
