package pl.put.poznan.iwm.card;

import java.io.IOException;
import javafx.fxml.FXML;
import pl.put.poznan.iwm.fhir.PatientData;

public class SecondaryController {

    private PatientData patient;

    @FXML
    public void initialize() {

    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    public void setPatient(PatientData patient){
        this.patient = patient;
    }
}