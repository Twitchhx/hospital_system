package com.example.controller;

import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import com.example.configuration.DBConfig;
import com.example.exception.HospitalNotFoundException;
import com.example.model.*;
import io.micronaut.http.annotation.*;
import java.io.IOException;
import java.util.*;

@Controller("/hospitals")
public class HospitalController {

    private final DBConfig arangoDB;
    private final String databaseName;
    private final PatientController patientController;

    public HospitalController(DBConfig arangoDB, PatientController patientController) {
        this.arangoDB = arangoDB;
        this.databaseName = arangoDB.getDatabaseName();
        this.patientController = patientController;

    }

    //Helper functions for handling the list of patients in crud operations
    private List<Patient> mapPatients(List<Map<String, Object>> patientDocuments) {
        List<Patient> patients = new ArrayList<>();
        for (Map<String, Object> patientData : patientDocuments) {
            Patient patient = new Patient();
            patient.setId((String) patientData.get("_key"));
            patient.setName((String) patientData.get("name"));
            patient.setAge((Integer) patientData.get("age"));
            patient.setGender((String) patientData.get("gender"));
            patient.setTreatment((String) patientData.get("treatment"));
            patients.add(patient);
        }
        return patients;
    }


    private static List<BaseDocument> getBaseDocuments(Hospital updatedHospital) {
        List<BaseDocument> updatedPatientDocuments = new ArrayList<>();
        for (Patient patient : updatedHospital.getPatients()) {
            BaseDocument patientDocument = new BaseDocument();
            patientDocument.addAttribute("_key", patient.getId());
            patientDocument.addAttribute("name", patient.getName());
            patientDocument.addAttribute("age", patient.getAge());
            patientDocument.addAttribute("gender", patient.getGender());
            patientDocument.addAttribute("treatment", patient.getTreatment());
            updatedPatientDocuments.add(patientDocument);
        }
        return updatedPatientDocuments;
    }


    @Post("/hospital")
    public String saveHospital(@Body Hospital hospital) {
        BaseDocument document = new BaseDocument();
        document.addAttribute("name", hospital.getName());
        document.addAttribute("location", hospital.getLocation());
        List<BaseDocument> patientDocuments = getBaseDocuments(hospital);
        document.addAttribute("patients", patientDocuments);
        arangoDB.getArangoDB().db(databaseName).collection("Hospital").insertDocument(document);
        hospital.setId(document.getKey());
        return "Hospital added successfully!";
    }

    @Post("/hospital/{id}/{patientId}")
    public String registerPatientInHospital(@PathVariable String patientId, @PathVariable String id) {
        Patient p = patientController.getPatientById(patientId);
        Hospital h = getHospitalById(id);
        h.getPatients().add(p);
        updateHospital(id, h);
        //HospitalPatients edge = new HospitalPatients("Patient/"+patientId, "Hospital/"+id);
        BaseDocument hpEdge = new BaseDocument();
        hpEdge.addAttribute("_from", "Patient/"+patientId);
        hpEdge.addAttribute("_to", "Hospital/"+id);
        arangoDB.getArangoDB().db(databaseName).collection("isPatientIn").insertDocument(hpEdge);
        return "Patient with ID " + patientId + " added in hospital with ID " + id +" successfully!";
    }


    @Get("/hospitals")
    public List<Hospital> getAllHospitals() {
        List<Hospital> hospitals = new ArrayList<>();
        try (ArangoCursor<BaseDocument> cursor = arangoDB.getArangoDB()
                .db(databaseName)
                .query("FOR p IN Hospital RETURN p", BaseDocument.class)) {

            cursor.forEachRemaining(document -> {
                Hospital hospital = new Hospital();
                hospital.setId(document.getKey());
                hospital.setName((String) document.getAttribute("name"));
                hospital.setLocation((String) document.getAttribute("location"));

                Object patientsAttribute = document.getAttribute("patients");
                if (patientsAttribute instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> patientDocuments = (List<Map<String, Object>>) patientsAttribute;
                    List<Patient> patientList = mapPatients(patientDocuments);
                    hospital.setPatients(patientList);
                }

                hospitals.add(hospital);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return hospitals;
    }

    @Get("/hospital/{id}")
    public Hospital getHospitalById(@PathVariable String id){
        BaseDocument existingDocument = Optional.ofNullable(arangoDB.getArangoDB()
                        .db(databaseName)
                        .collection("Hospital")
                        .getDocument(id, BaseDocument.class))
                .orElseThrow(() -> new HospitalNotFoundException(id));

        Hospital retrievedHospital = new Hospital();
        retrievedHospital.setId(existingDocument.getKey());
        retrievedHospital.setName((String) existingDocument.getAttribute("name"));
        retrievedHospital.setLocation((String) existingDocument.getAttribute("location"));
        Object patientsAttribute = existingDocument.getAttribute("patients");
        if (patientsAttribute instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> patientDocuments = (List<Map<String, Object>>) patientsAttribute;
            List<Patient> patientList = mapPatients(patientDocuments);
            retrievedHospital.setPatients(patientList);
        }

        return retrievedHospital;
    }

    @Get("/hospital/{id}/patients")
    public List<Patient> listPatientsOfHospital(@PathVariable String id){
        return getHospitalById(id).getPatients();
    }



    @Put("/hospital/{id}")
    public String updateHospital(@PathVariable String id, @Body Hospital updatedHospital) {
        BaseDocument existingDocument = Optional.ofNullable(arangoDB.getArangoDB()
                        .db(databaseName)
                        .collection("Hospital")
                        .getDocument(id, BaseDocument.class))
                .orElseThrow(() -> new HospitalNotFoundException(id));

        // Update the document attributes
        existingDocument.addAttribute("name", updatedHospital.getName());
        existingDocument.addAttribute("location", updatedHospital.getLocation());

        // Handle patient list mapping
        List<BaseDocument> updatedPatientDocuments = getBaseDocuments(updatedHospital);
        existingDocument.addAttribute("patients", updatedPatientDocuments);

        // Save the updated document back to the database
        arangoDB.getArangoDB().db(databaseName).collection("Hospital").updateDocument(id, existingDocument);

        updatedHospital.setId(id);
        return "Hospital with ID " + id + " updated successfully!";
    }

    @Patch("/hospital/{id}")
    public String partialUpdateHospital(@PathVariable String id, @Body Map<String, Object> updates) {
        BaseDocument existingDocument = Optional.ofNullable(arangoDB.getArangoDB()
                        .db(databaseName)
                        .collection("Hospital")
                        .getDocument(id, BaseDocument.class))
                .orElseThrow(() -> new HospitalNotFoundException(id));

        // Update only the provided fields
        updates.forEach(existingDocument::addAttribute);

        arangoDB.getArangoDB().db(databaseName).collection("Hospital").updateDocument(id, existingDocument);

        return "Hospital with ID " + id + " updated successfully!";
    }


    @Delete("/hospital/{id}")
    public String deleteHospital(@PathVariable String id) {
        BaseDocument existingDocument = Optional.ofNullable(arangoDB.getArangoDB()
                        .db(databaseName)
                        .collection("Hospital")
                        .getDocument(id, BaseDocument.class))
                .orElseThrow(() -> new HospitalNotFoundException(id));

        arangoDB.getArangoDB()
                .db(databaseName)
                .collection("Hospital")
                .deleteDocument(id);
        // Use AQL query to delete documents in the isPatientIn collection
        String query = "FOR doc IN isPatientIn FILTER doc._to == @hospitalId REMOVE doc IN isPatientIn";
        Map<String, Object> bindVars = Collections.singletonMap("hospitalId", "Hospital/"+id);

        // Execute the query
        try (ArangoCursor<Void> cursor = arangoDB.getArangoDB()
                .db(databaseName)
                .query(query, Void.class, bindVars, null)) {
            System.out.println("Deleting.....");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "Hospital with ID " + id + " deleted successfully!";
    }


}
