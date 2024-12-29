package com.example.exception;

public class PatientsNotFoundException extends Exception {
    public PatientsNotFoundException() {
        super("No patients found");
    }
}
