module PatientCard {
    requires javafx.controls;
    requires javafx.fxml;
    requires FHIRDatabase;
    requires org.jetbrains.annotations;


    opens pl.put.poznan.iwm.card to javafx.fxml;
    exports pl.put.poznan.iwm.card;
}