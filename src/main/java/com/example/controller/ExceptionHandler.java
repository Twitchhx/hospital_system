package com.example.controller;

import com.example.exception.HospitalNotFoundException;
import com.example.exception.PatientNotFoundException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Error;

public class ExceptionHandler {

    @Error(global = true)
    public HttpResponse<?> handlePatientNotFoundException(PatientNotFoundException ex) {
        return HttpResponse.notFound("Error: " + ex.getMessage());
    }
    @Error(global = true)
    public HttpResponse<?> handleHospitalNotFoundException(HospitalNotFoundException ex) {
        return HttpResponse.notFound("Error: " + ex.getMessage());
    }
}
