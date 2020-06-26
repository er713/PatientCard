package pl.put.poznan.iwm.card;

import java.time.LocalDate;

public class PatientMedicationElement extends PatientHistoryElement {

    private final String dosage;
    private final String status;
    private final String priority;
    private final String note;

    public PatientMedicationElement(String id, String title, LocalDate when, String detail, String dosage,
                                    String status, String priority, String medicationText) {
        super(id, title, when, detail);
        this.dosage = dosage;
        this.status = status;
        this.priority = priority;
        this.note = medicationText;
    }

    public String getDosage() {
        return dosage;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public String getNote() {
        return note;
    }
}
