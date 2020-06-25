package pl.put.poznan.iwm.fhir;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientMedicationElement extends PatientHistoryElement {

    public PatientMedicationElement(String id, String title, LocalDate when, String detail) {
        super(id, title, when, detail);
    }

}
