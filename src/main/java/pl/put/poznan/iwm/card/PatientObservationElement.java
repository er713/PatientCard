package pl.put.poznan.iwm.card;

import java.time.LocalDate;

public class PatientObservationElement extends PatientHistoryElement{

    public PatientObservationElement(String id, String title, LocalDate when, String detail) {
        super(id, title, when, detail);
    }

}
