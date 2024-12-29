package com.example.exception;

public class PatientNotFoundException extends IllegalArgumentException{
    public PatientNotFoundException(String id) {
        super("Patient with ID " + id + " not found.");
    }
}
