package com.example.model;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

//Document
@Serdeable
@Introspected
public class Patient {
    private String id;
    private String name;
    private int age;
    private String gender;
    private String treatment;

    public Patient(){

    }

    public Patient(String name, int age, String gender, String treatment) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.treatment = treatment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }
}

