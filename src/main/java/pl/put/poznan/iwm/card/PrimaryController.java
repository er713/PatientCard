package pl.put.poznan.iwm.card;

import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
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
    public ProgressIndicator progress;

    private Thread loader = null;
    private final Runnable waitAndLoad = () -> {
        try {
            Thread.sleep(900);
        } catch (InterruptedException e) {
//            e.printStackTrace();
            System.out.println("Przerwano czekanie");
            return;
        }
        String name = searchName.getText();
        if (!name.equals(""))
            this.initPatientList(name);
        else
            this.initPatientList();
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
        new Thread(() -> initPatientList()).start();
    }

    private void initPatientList() {
        Platform.runLater(() -> {
            startLoading();
            listName.getChildren().clear();
        });
        List<PatientData> patients = App.db.getPatientList(null);

        for (var p : patients) {
            Platform.runLater(() -> ListItem.generateItem(
                    listName, p.firstName() + " " + p.lastName(), p.birthDate(), "secondary", p
            ));
        }
        Platform.runLater(() -> stopLoading());
    }

    private void initPatientList(String name) {
        Platform.runLater(() -> {
            startLoading();
            listName.getChildren().clear();
        });
        List<PatientData> patients = App.db.getPatientList(name);

        for (var p : patients) {
            Platform.runLater(() -> ListItem.generateItem(
                    listName, p.firstName() + " " + p.lastName(), p.birthDate(), "secondary", p
            ));
        }
        Platform.runLater(() -> stopLoading());

    }

    private void startLoading() {
        progress.setVisible(true);
        progress.setDisable(false);
        searchName.setDisable(true);
        listName.setDisable(true);
    }

    private void stopLoading() {
        progress.setVisible(false);
        progress.setDisable(true);
        searchName.setDisable(false);
        listName.setDisable(false);
    }

}
