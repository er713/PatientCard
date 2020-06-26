package pl.put.poznan.iwm.card;

import java.time.LocalDate;

public record PatientData(String id, String firstName, String lastName, LocalDate birthDate, LocalDate deathDate) {
}
