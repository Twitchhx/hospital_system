package com.example.controller;

import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import com.example.configuration.DBConfig;
import com.example.exception.PatientNotFoundException;
import com.example.model.Patient;
import com.example.model.Hospital;
import io.micronaut.http.annotation.*;
import java.io.IOException;
import java.util.*;

@Controller("/patients")
public class PatientController {

    private final DBConfig arangoDB;
    private final String databaseName;

    public PatientController(DBConfig arangoDB) {
        this.arangoDB = arangoDB;
        this.databaseName = arangoDB.getDatabaseName();
    }


    @Post("/patient")
    public String savePatient(@Body Patient patient) {
        BaseDocument document = new BaseDocument();
        document.addAttribute("name", patient.getName());
        document.addAttribute("age", patient.getAge());
        document.addAttribute("gender", patient.getGender());
        document.addAttribute("treatment", patient.getTreatment());
        arangoDB.getArangoDB().db(databaseName).collection("Patient").insertDocument(document);
        patient.setId(document.getKey());  // Set the generated document key as the patient ID
        return "Patient saved successfully!";
    }


    @Get("/patients")
    public List<Patient> getAllPatients(){
        List<Patient> patients = new ArrayList<>();
        try (ArangoCursor<BaseDocument> cursor = arangoDB.getArangoDB()
                .db(databaseName)
                .query("FOR p IN Patient RETURN p", BaseDocument.class)) {

            cursor.forEachRemaining(document -> {
                Patient patient = new Patient();
                patient.setId(document.getKey());
                patient.setName((String) document.getAttribute("name"));
                patient.setGender((String) document.getAttribute("gender"));
                patient.setAge((Integer) document.getAttribute("age"));
                patient.setTreatment((String) document.getAttribute("treatment"));
                patients.add(patient);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return patients;
    }

    @Get("/patient/{id}")
    public Patient getPatientById(@PathVariable String id) {
        // Retrieve the existing document
        BaseDocument existingDocument = Optional.ofNullable(arangoDB.getArangoDB()
                        .db(databaseName)
                        .collection("Patient")
                        .getDocument(id, BaseDocument.class))
                .orElseThrow(() -> new PatientNotFoundException(id));

        Patient retrievedPatient = new Patient();
        retrievedPatient.setId(existingDocument.getKey());
        retrievedPatient.setName((String) existingDocument.getAttribute("name"));
        retrievedPatient.setAge((Integer) existingDocument.getAttribute("age"));
        retrievedPatient.setGender((String) existingDocument.getAttribute("gender"));
        retrievedPatient.setTreatment((String) existingDocument.getAttribute("treatment"));

        return retrievedPatient;
    }

    @Get("/patient/{patientId}/hospitals")
    public List<Hospital> getHospitalsForPatient(@PathVariable String patientId) {
        String patientDocId = "Patient/" + patientId; // Format the document ID for the patient
        List<Hospital> hospitals = new ArrayList<>();

        // Query the isPatientIn edge collection for edges where _from matches the patient ID
        String query = "FOR edge IN isPatientIn FILTER edge._from == @patientId RETURN edge._to";
        Map<String, Object> bindVars = new HashMap<>();
        bindVars.put("patientId", patientDocId);

        try (ArangoCursor<String> cursor = arangoDB.getArangoDB()
                .db(databaseName)
                .query(query, String.class, bindVars, null)) {

            // For each _to attribute (hospital ID), fetch the hospital document
            cursor.forEachRemaining(hospitalId -> {
                BaseDocument hospitalDoc = arangoDB.getArangoDB()
                        .db(databaseName)
                        .collection("Hospital")
                        .getDocument(hospitalId.replace("Hospital/", ""), BaseDocument.class);

                if (hospitalDoc != null) {
                    Hospital hospital = new Hospital();
                    hospital.setId(hospitalDoc.getKey());
                    hospital.setName((String) hospitalDoc.getAttribute("name"));
                    hospital.setLocation((String) hospitalDoc.getAttribute("location"));

                    // Map patients if needed, similar to other hospital retrieval logic
                    hospitals.add(hospital);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Error fetching hospitals for patient with ID: " + patientId, e);
        }

        return hospitals;
    }


    @Put("/patient/{id}")
    public String updatePatient(@PathVariable String id, @Body Patient updatedPatient) {
        // Retrieve the existing document
        BaseDocument existingDocument = Optional.ofNullable(arangoDB.getArangoDB()
                        .db(databaseName)
                        .collection("Patient")
                        .getDocument(id, BaseDocument.class))
                .orElseThrow(() -> new PatientNotFoundException(id));

        // Update the document attributes
        existingDocument.addAttribute("name", updatedPatient.getName());
        existingDocument.addAttribute("age", updatedPatient.getAge());
        existingDocument.addAttribute("gender", updatedPatient.getGender());
        existingDocument.addAttribute("treatment", updatedPatient.getTreatment());

        // Save the updated document back to the database
        arangoDB.getArangoDB().db(databaseName).collection("Patient").updateDocument(id, existingDocument);

        updatedPatient.setId(id);
        return "Patient with ID " + id + " updated successfully!";
    }

    @Patch("/patient/{id}")
    public String partialUpdatePatient(@PathVariable String id, @Body Map<String, Object> updates) {
        BaseDocument existingDocument = Optional.ofNullable(arangoDB.getArangoDB()
                        .db(databaseName)
                        .collection("Patient")
                        .getDocument(id, BaseDocument.class))
                .orElseThrow(() -> new PatientNotFoundException(id));

        // Update only the provided fields
        updates.forEach(existingDocument::addAttribute);

        arangoDB.getArangoDB().db(databaseName).collection("Patient").updateDocument(id, existingDocument);

        return "Patient with ID " + id + " updated successfully!";
    }

    @Delete("/patient/{id}")
    public String deletePatient(@PathVariable String id) {
        // Attempt to retrieve the document with the provided ID
        BaseDocument existingDocument = Optional.ofNullable(arangoDB.getArangoDB()
                .db(databaseName)
                .collection("Patient")
                .getDocument(id, BaseDocument.class))
            .orElseThrow(() -> new PatientNotFoundException(id));

        // Delete the document if it exists
        arangoDB.getArangoDB()
                .db(databaseName)
                .collection("Patient")
                .deleteDocument(id);

        // Use AQL query to delete documents in the isPatientIn collection
        String query = "FOR doc IN isPatientIn FILTER doc._from == @patientId REMOVE doc IN isPatientIn";
        Map<String, Object> bindVars = Collections.singletonMap("patientId", "Patient/"+id);

        // Execute the query
        try (ArangoCursor<Void> cursor = arangoDB.getArangoDB()
                .db(databaseName)
                .query(query, Void.class, bindVars, null)) {
            System.out.println("Deleting.....");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "Patient with ID " + id + " deleted successfully!";
    }




}
