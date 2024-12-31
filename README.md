# Hospital Management System

## Project Overview

This project is a simple hospital management system built using **Micronaut** and **ArangoDB**. It allows users to:

- Create, modify, and delete hospitals and patients.
- Register patients in hospitals.
- List all patients in a specific hospital.

The system uses **JSON** for data communication and **Gradle** as the build tool.

## Features

- RESTful API for hospital and patient management.
- Custom exception handling for better error reporting.
- Flexible database configuration using `DBConfig`.
- Graph-based relationships between hospitals and patients using ArangoDB.

## Technologies Used

- **Micronaut Framework**: A modern, lightweight Java framework for building microservices.
- **ArangoDB**: A native multi-model database supporting graph relationships.
- **Gradle**: Build automation tool.
- **Java**: The primary programming language.
- **Docker**: Used to access the ArangoDB web interface.

## Project Structure

- **Configuration**:
    - `DBConfig`: Handles database setup and configurations.
- **Controllers**:
    - `HospitalController`: Manages hospital-related routes.
    - `PatientController`: Manages patient-related routes.
- **Exceptions**:
    - Custom exceptions like `HospitalNotFoundException` for detailed error messages.
- **Models**:
    - `Hospital` and `Patient`: Define the main entities and their properties.

## Setup Instructions

### Prerequisites

- JDK 17 or higher
- Gradle
- Docker (to run ArangoDB)

### Steps

1. Clone the repository:
   ```bash
   git clone <repository_url>
   ```
2. Navigate to the project directory:
   ```bash
   cd Hospital
   ```
3. Configure the database connection in `DBConfig`.
4. Build the project:
   ```bash
   ./gradlew build
   ```
5. Run the application:
   ```bash
   ./gradlew run
   ```
   The Micronaut application is running on port 8080.
6. Use Docker to access the ArangoDB web interface:
   ```bash
   docker run -d --name arangodb -e ARANGO_ROOT_PASSWORD=password -p 8529:8529 arangodb
   ```
   Open [http://localhost:8529](http://localhost:8529) in your browser and log in with the root password.

## API Routes

### Hospital Routes

1. **Create a hospital**

    - `POST /hospitals/hospital`
    - Request Body:
      ```json
      {
        "identifier":"hosp1",
        "name": "Hospital Name",
        "location": "City, Country",
        "patients":[]
      }
      ```

2. **List all hospitals**

    - `GET /hospitals/hospitals`

3. **Get a hospital by ID**

    - `GET /hospitals/hospital/{id}`

4. **Update a hospital**

    - `PUT /hospitals/hospital/{id}`
    - `PATCH /hospitals/hospital/{id}`
    - Request Body:
      ```json
      {
        "identifier":"hosp1",
        "name": "Updated Name",
        "location": "Updated Location",
        "patients":[]
      }
      ```

5. **Delete a hospital**

    - `DELETE /hospitals/hospital/{id}`
6. **Register a patient in a hospital**

    - `POST /hospitals/hospital/{hospitalId}/{patientId}`

### Patient Routes

1. **Create a patient**

    - `POST /patients/patient`
    - Request Body:
      ```json
      {
        "identification": "eg100",
        "name": "Patient Name",
        "age": 30,
        "gender":"male",
        "treatment":"surgery"
      }
      ```

2. **List all patients**

    - `GET /patients/patients`

3. **Get a patient by ID**

    - `GET /patients/patient/{id}`

4. **Get hospitals of patient**

    - `GET /patients/patient/{id}/hospitals`

    
5. **Delete a patient**

    - `DELETE /patients/patient/{id}`

## Testing the Routes

### Using Postman

1. Import the routes into Postman.
2. Send requests with the required JSON body.
3. Verify the responses for each endpoint.

## Error Handling

- **HospitalNotFoundException**: Thrown when a requested hospital is not found.
- **HospitalsNotFoundException**: Thrown when a requested list of hospitals is not found.
- **PatientNotFoundException**: Thrown when a requested patient is not found.
- **PatientsNotFoundException**: Thrown when a requested list of patients is not found.



## Future Enhancements

- Add authentication and authorization.
- Implement a frontend.



---

Omar ElNahtawy, 2024

