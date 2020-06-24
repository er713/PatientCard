module FHIRDatabase {
    requires hapi.fhir.base;
    requires org.hl7.fhir.r4;
    requires java.sql;
    requires org.jetbrains.annotations;

//    opens pl.put.poznan.iwm.fhir to PatientCard;
    exports pl.put.poznan.iwm.fhir;
}