package pl.put.poznan.iwm.card;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.jetbrains.annotations.Nullable;
import pl.put.poznan.iwm.fhir.PatientData;
import pl.put.poznan.iwm.fhir.PatientHistoryElement;

public class SecondaryController {

    static private final DateTimeFormatter dataFormatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.UK);


    @FXML
    public VBox history;
    public DatePicker from;
    public DatePicker to;

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
                this.initializeHistory(patient, from.getValue(), to.getValue()));
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
                this.initializeHistory(patient, from.getValue(), to.getValue()));
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    public void setPatient(PatientData patient) {
        this.patient = patient;
        initializeHistory(patient, null, null);
    }

    private void initializeHistory(PatientData patientData, @Nullable LocalDate begin, @Nullable LocalDate end) {
        history.getChildren().clear();
        var patientHistory = App.db.getPatientHistory(patientData, begin, end);
        LocalDate actual = null;

        for (var h : patientHistory) {
            if (actual == null || !actual.equals(h.getWhen())) {
                actual = h.getWhen();
//                history.getChildren().add(new Text(dataFormatter.format(actual)));
                ListItem.generateDate(history, dataFormatter.format(actual));
            }
//            history.getChildren().add(
//                    new Text(h.getTitle())
//            );
            ListItem.generateHistory(history, h.getTitle());
        }
    }
}