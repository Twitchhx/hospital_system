package com.example.model;

import io.micronaut.core.annotation.Introspected;
//Edge
@Introspected
public class HospitalPatients {
    private String patientId;
    private String hospitalId;

    public HospitalPatients(String from, String to) {
        this.patientId = from;
        this.hospitalId = to;
    }


    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
