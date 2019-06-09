package com.mauro.colella.server;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import graphql.ExecutionResult;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class API {
  private GraphQLSchema schema = null;

  public API() {
    String schemaDefinition = "type Query{hello: String}";

    SchemaParser schemaParser = new SchemaParser();
    TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schemaDefinition);

    RuntimeWiring runtimeWiring = newRuntimeWiring()
            .type("Query", builder -> builder.dataFetcher("hello", new StaticDataFetcher("world")))
            .build();

    SchemaGenerator schemaGenerator = new SchemaGenerator();
    this.schema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
  }

  public GraphQLSchema getSchema() {
    return this.schema;
  }
}
