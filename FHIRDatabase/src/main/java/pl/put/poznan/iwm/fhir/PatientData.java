package pl.put.poznan.iwm.fhir;

import java.time.LocalDate;

public record PatientData(String id, String firstName, String lastName, LocalDate birthDate) {
}
