package com.example.exception;

public class HospitalNotFoundException extends IllegalArgumentException {
    public HospitalNotFoundException(String id) {
        super("Hospital with ID " + id + " not found.");
    }
}
