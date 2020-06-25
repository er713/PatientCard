package pl.put.poznan.iwm.fhir;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientObservationElement extends PatientHistoryElement{

    public PatientObservationElement(String id, String title, LocalDate when, String detail) {
        super(id, title, when, detail);
    }

}
