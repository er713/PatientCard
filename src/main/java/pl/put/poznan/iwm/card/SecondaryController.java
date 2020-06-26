package pl.put.poznan.iwm.card;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class SecondaryController {

    static private final DateTimeFormatter dataFormatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.UK);


    @FXML
    public VBox history;
    public DatePicker from;
    public DatePicker to;
    public ProgressIndicator progress;
    public Label name;

    private PatientData patient;

    @FXML
    public void initialize() {
//        history.setPrefWidth(App.getWindowWidth() -  history.getPadding().getRight());
        to.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate localDate, boolean b) {
                super.updateItem(localDate, b);
                setDisable(localDate.isAfter(LocalDate.now()) || localDate.isBefore(
                        (from.getValue() != null) ? from.getValue().plusDays(1) : LocalDate.MIN
                ));
            }
        });
        to.valueProperty().addListener((observableValue, localDate, t1) ->
                this.startInitialize(patient, from.getValue(), to.getValue()));
        from.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate localDate, boolean b) {
                super.updateItem(localDate, b);
                setDisable(localDate.isAfter(
                        (to.getValue() != null) ? to.getValue().minusDays(1) : LocalDate.now().minusDays(1))
                );
            }
        });
        from.valueProperty().addListener((observableValue, localDate, t1) ->
                this.startInitialize(patient, from.getValue(), to.getValue()));
    }

    @FXML
    private void switchToPrimary() {
        App.setRoot("primary");
    }

    public void setPatient(PatientData patient) {
        this.patient = patient;
        name.setText(patient.firstName() + " " + patient.lastName());
        startInitialize(patient, null, null);
    }

    private void startInitialize(PatientData patientData, @Nullable LocalDate begin, @Nullable LocalDate end) {
        new Thread(() -> initializeHistory(patientData, begin, end)).start();
    }

    private void initializeHistory(PatientData patientData, @Nullable LocalDate begin, @Nullable LocalDate end) {
        Platform.runLater(() -> {
            startLoading();
            history.getChildren().clear();
        });
        var patientHistory = App.db.getPatientHistory(patientData, begin, end);
        LocalDate actual = null;

        for (var h : patientHistory) {
            if (actual == null || !actual.equals(h.getWhen())) {
                actual = h.getWhen();
//                history.getChildren().add(new Text(dataFormatter.format(actual)));
                LocalDate finalActual = actual;
                Platform.runLater(() -> ListItem.generateDate(history, dataFormatter.format(finalActual)));
            } else {
                Separator sep = new Separator();
                sep.setOrientation(Orientation.HORIZONTAL);
                sep.setPadding(new Insets(0, 35, 0, 35));
                Platform.runLater(() -> history.getChildren().add(sep));
            }
//            history.getChildren().add(
//                    new Text(h.getTitle())
//            );
            if (h instanceof PatientObservationElement) {
                Platform.runLater(() -> ListItem.generateHistory(history, h.getTitle(), h.getDetail()));
            } else if (h instanceof PatientMedicationElement) {
                Platform.runLater(() -> ListItem.generateHistory(history, (PatientMedicationElement) h));
            } else {
                Platform.runLater(() -> ListItem.generateHistory(history, h.getTitle(), null));
            }
            Platform.runLater(() -> stopLoading());
        }
    }

    private void startLoading() {
        progress.setDisable(false);
        progress.setVisible(true);
        to.setDisable(true);
        from.setDisable(true);
    }

    private void stopLoading() {
        progress.setDisable(true);
        progress.setVisible(false);
        to.setDisable(false);
        from.setDisable(false);
    }

}