package com.example.controller;

import com.example.exception.*;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;

@Controller
public class ExceptionHandler {

    @Error(global = true)
    public HttpResponse<?> handlePatientNotFoundException(PatientNotFoundException ex) {
        return HttpResponse.notFound("Error: " + ex.getMessage());
    }
    @Error(global = true)
    public HttpResponse<?> handleHospitalNotFoundException(HospitalNotFoundException ex) {
        return HttpResponse.notFound("Error: " + ex.getMessage());
    }

    @Error(global = true)
    public HttpResponse<?> handlePatientsNotFoundException(PatientsNotFoundException ex) {
        return HttpResponse.notFound("Error: " + ex.getMessage());
    }

    @Error(global = true)
    public HttpResponse<?> handleHospitalsNotFoundException(HospitalsNotFoundException ex) {
        return HttpResponse.notFound("Error: " + ex.getMessage());
    }
}
