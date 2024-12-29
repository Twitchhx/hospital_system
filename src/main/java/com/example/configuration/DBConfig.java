package com.example.configuration;

import com.arangodb.ArangoDB;
import com.arangodb.entity.CollectionType;
import com.arangodb.entity.EdgeDefinition;
import com.arangodb.model.CollectionCreateOptions;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.util.Collections;

@Singleton
public class DBConfig {

    private final ArangoDB arangoDB;
    private final String databaseName;

    // Building the database and connecting to it using details from application.properties
    public DBConfig(
            @Value("${arangodb.host}") String host,
            @Value("${arangodb.port}") int port,
            @Value("${arangodb.user}") String user,
            @Value("${arangodb.password}") String password,
            @Value("${arangodb.database}") String databaseName) {
        this.arangoDB = new ArangoDB.Builder()
                .host(host, port)
                .user(user)
                .password(password)
                .build();
        this.databaseName = databaseName;

        // Making sure the documents and the appropriate edge are present
        if (!arangoDB.db(databaseName).collection("Hospital").exists()) {
            arangoDB.db(databaseName).createCollection("Hospital", new CollectionCreateOptions()
                    .type(CollectionType.DOCUMENT));
        }
        if (!arangoDB.db(databaseName).collection("Patient").exists()) {
            arangoDB.db(databaseName).createCollection("Patient", new CollectionCreateOptions()
                    .type(CollectionType.DOCUMENT));
        }
        if (!arangoDB.db(databaseName).collection("isPatientIn").exists()) {
            arangoDB.db(databaseName).createCollection("isPatientIn", new CollectionCreateOptions()
                    .type(CollectionType.EDGES));
        }

        // Define the edge definition
        EdgeDefinition edgeDefinition = new EdgeDefinition()
                .collection("isPatientIn")
                .from("Patient")
                .to("Hospital");

        // Creating the graph
        if (!arangoDB.db(databaseName).graph("Hospital_Patients").exists()) {
            arangoDB.db(databaseName)
                    .createGraph("Hospital_Patients", Collections.singletonList(edgeDefinition));
        }

    }

    public ArangoDB getArangoDB() {
        return arangoDB;
    }

    public String getDatabaseName() {
        return databaseName;
    }

}
