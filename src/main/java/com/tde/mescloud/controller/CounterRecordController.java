package com.tde.mescloud.controller;

import com.tde.mescloud.entity.CounterRecord;
import com.tde.mescloud.repository.CounterRecordRepository;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/")
public class CounterRecordController {

    private final CounterRecordRepository repository;
    private GraphQL graphQL;

    @Value("classpath:counter-record.graphql")
    private Resource schemaResource;

    public CounterRecordController(CounterRecordRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<String> weAreAlive() {
        return new ResponseEntity<>("We are Alive!", HttpStatus.OK);
    }

    @PostConstruct
    public void loadSchema() throws IOException {
        File schemaFile = schemaResource.getFile();
        TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);
        RuntimeWiring wiring = buildWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(registry, wiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    private RuntimeWiring buildWiring() {
        DataFetcher<List<CounterRecord>> countersFetcher=data -> {
            return repository.findAll();
        };

        DataFetcher<CounterRecord> aliasFetcher=data -> {
            return repository.findByAlias(data.getArgument("alias"));
        };

        return RuntimeWiring.newRuntimeWiring().type("Query", typeWriting ->
                typeWriting.dataFetcher("getCounterRecords", countersFetcher).dataFetcher("findByAlias", aliasFetcher)).build();
    }

    @PostMapping("/add-counter")
    public String addCounterRecords(@RequestBody List<CounterRecord> counterRecords) {
        repository.saveAll(counterRecords);
        return "Counter Records inserted " + counterRecords.size();
    }

    @GetMapping("/counters")
    public List<CounterRecord> findAll() {
        return repository.findAll();
    }

    @PostMapping("/get-all")
    public ResponseEntity<Object> findAllCounterRecords(@RequestBody String query) {
        ExecutionResult result = graphQL.execute(query);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/get-by-alias")
    public ResponseEntity<Object> findByAlias(@RequestBody String query) {
        ExecutionResult result = graphQL.execute(query);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

