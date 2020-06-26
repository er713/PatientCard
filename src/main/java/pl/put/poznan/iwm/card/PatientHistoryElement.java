package pl.put.poznan.iwm.card;

import java.time.LocalDate;

public abstract class PatientHistoryElement {

    private final String id;
    private final String title;
    private final LocalDate when;
    private final String detail;

    public PatientHistoryElement(String id, String title, LocalDate when, String detail) {
        this.id = id;
        this.title = title;
        this.when = when;
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getWhen() {
        return when;
    }

    public String getDetail() {
        return detail;
    }
}
