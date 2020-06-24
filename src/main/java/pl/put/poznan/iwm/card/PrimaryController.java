package pl.put.poznan.iwm.card;

import java.util.List;

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

    @FXML
    public void initialize() {
//        ListItem.generateItem(listName, "Ktoś", LocalDate.now(), "secondary");
//        ListItem.generateItem(listName, "Noś", LocalDate.now(), "secondary");
//        ListItem.generateItem(listName, "APJsdknsa", LocalDate.now().minusWeeks(2), "secondary");
//        ListItem.generateItem(listName, "enlafnk", LocalDate.now().minusYears(3).minusDays(1), "secondary");
        initPatientList();
    }

    private void initPatientList(){
        List<PatientData> patients = App.db.getPatientList();

        for(var p: patients){
            ListItem.generateItem(listName, p.firstName()+" "+p.lastName(), p.birthDate(), "secondary");
        }
    }

}
