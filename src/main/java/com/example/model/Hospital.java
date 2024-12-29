package com.example.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import java.util.ArrayList;
import java.util.List;

//Document
@Serdeable
@Introspected
public class Hospital {
    private String id;
    private String identifier;
    private String name;
    private String location;
    private List<Patient> patients = new ArrayList<>();

    public Hospital() {

    }

    public Hospital(String identifier, String name, String location, List<Patient> patients) {
        this.identifier = identifier;
        this.name = name;
        this.location = location;
        this.patients = patients;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }
}
