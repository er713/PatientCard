package pl.put.poznan.iwm.card;

import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import pl.put.poznan.iwm.fhir.PatientData;

public class PrimaryController {

    @FXML
    public VBox listName;
    public TextField searchName;
    public GridPane title;
    public GridPane main;

    private Thread loader = null;
    private final Runnable waitAndLoad = () -> {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
//            e.printStackTrace();
            System.out.println("Przerwano czekanie");
            return;
        }
        String name = searchName.getText();
        if (!name.equals(""))
            Platform.runLater(() -> this.initPatientList(name));
        else
            Platform.runLater(() -> this.initPatientList());
    };

    @FXML
    public void initialize() {

        searchName.textProperty().addListener((observableValue, s, t1) -> {
            if (loader != null) {
                loader.interrupt();
            }
            loader = new Thread(waitAndLoad);
            loader.start();
        });

//        listName.setPrefWidth(App.getWindowWidth() - listName.getPadding().getRight() - listName.getPadding().getLeft());
        initPatientList();
    }

    private void initPatientList() {
        listName.getChildren().clear();
        List<PatientData> patients = App.db.getPatientList(null);

        for (var p : patients) {
            ListItem.generateItem(
                    listName, p.firstName() + " " + p.lastName(), p.birthDate(), "secondary", p
            );
        }
    }

    private void initPatientList(String name) {
        listName.getChildren().clear();
        List<PatientData> patients = App.db.getPatientList(name);

        for (var p : patients) {
            ListItem.generateItem(
                    listName, p.firstName() + " " + p.lastName(), p.birthDate(), "secondary", p
            );
        }
    }


}
