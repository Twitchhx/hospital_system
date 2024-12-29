package com.example.exception;

public class HospitalsNotFoundException extends Exception {
    public HospitalsNotFoundException() {
        super("No hospitals found");
    }
}
