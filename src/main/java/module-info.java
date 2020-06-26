module PatientCard {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires hapi.fhir.base;
    requires org.hl7.fhir.r4;
    requires java.sql;


    opens pl.put.poznan.iwm.card to javafx.fxml;
    exports pl.put.poznan.iwm.card;
}