package pl.put.poznan.iwm.fhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.*;
import ca.uhn.fhir.context.FhirContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.regex.Pattern;

public class FHIRDatabase {

    private IGenericClient client;
    private String path;
    private final Pattern nameSearch = Pattern.compile("^[A-Z][a-z]+");

    public FHIRDatabase(String url) {
        path = url;
        client = FhirContext.forR4().newRestfulGenericClient(path);
    }

    public List<PatientData> getPatientList(@Nullable String surnamePattern) {
        var res = new ArrayList<PatientData>();

        Bundle bundle = null;
        if (surnamePattern == null)
            bundle = client.search().forResource(Patient.class).sort().ascending(Patient.FAMILY)
                    .returnBundle(Bundle.class).execute();
        else {
            bundle = client.search().forResource(Patient.class)
                    .where(Patient.FAMILY.matches().value(surnamePattern))
                    .sort().ascending(Patient.FAMILY)
                    .returnBundle(Bundle.class).execute();
        }

        Patient patient;
        List<Bundle.BundleEntryComponent> pat;
        while (bundle != null) {
            pat = bundle.getEntry();
            for (var p : pat) {
                patient = (Patient) p.getResource();
                var name = nameSearch.matcher(patient.getName().get(0).getGivenAsSingleString());
                var last = nameSearch.matcher(patient.getName().get(0).getFamily());
                name.find();
                last.find();
                res.add(new PatientData(
                        patient.getIdElement().getIdPart(),
                        name.group(),
                        last.group(),
                        patient.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        (patient.getDeceasedDateTimeType().getValue() != null) ?
                                patient.getDeceasedDateTimeType().getValue()
                                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null
                ));
            }

            if (bundle.getLink(Bundle.LINK_NEXT) != null)
                bundle = client.loadPage().next(bundle).execute();
            else
                bundle = null;
        }
        return res;
    }

    public List<PatientHistoryElement> getPatientHistory(@NotNull PatientData patientData,
                                                         @Nullable LocalDate begin, @Nullable LocalDate end) {
        var res = new ArrayList<PatientHistoryElement>();

        var query = client.search().forResource(Observation.class)
                .where(Observation.PATIENT.hasId(patientData.id()));
        if (begin != null) {
            query.and(Observation.DATE.afterOrEquals().day(
                    Date.from(begin.atStartOfDay(ZoneId.systemDefault()).toInstant())
            ));
        }
        if (end != null) {
            query.and(Observation.DATE.beforeOrEquals().day(
                    Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant())
            ));
        }
        Bundle bundle = query.returnBundle(Bundle.class).execute();

        Observation observation;
        while (bundle != null) {

            for (var b : bundle.getEntry()) {
                observation = (Observation) b.getResource();
                res.add(new PatientObservationElement(
                        observation.getIdElement().getIdPart(),
                        observation.getCode().getText(),
                        observation.getIssued().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
//                        (observation.getValueQuantity().getValue() != null) ? observation.getValueQuantity()
//                                .getValue().setScale(2, RoundingMode.HALF_EVEN)
//                                .toString() + " [" + observation.getValueQuantity().getUnit() + "]" : null
                        (observation.hasValue()) ? observation.getValue().toString() : null //TODO: obsługa
                ));
            }
            if (bundle.getLink(Bundle.LINK_NEXT) != null) {
                bundle = client.loadPage().next(bundle).execute();
            } else bundle = null;
        }

        query = client.search().forResource(MedicationRequest.class)
                .where(MedicationRequest.PATIENT.hasId(patientData.id()));
        if (begin != null) {
            query.and(Observation.DATE.afterOrEquals().day(
                    Date.from(begin.atStartOfDay(ZoneId.systemDefault()).toInstant())
            ));
        }
        if (end != null) {
            query.and(Observation.DATE.beforeOrEquals().day(
                    Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant())
            ));
        }
        bundle = query.returnBundle(Bundle.class).execute();

        MedicationRequest medication;
        while (bundle != null) {

            for (var b : bundle.getEntry()) {
                medication = (MedicationRequest) b.getResource();
                res.add(new PatientMedicationElement(
                        medication.getIdElement().getIdPart(),
                        medication.getMedicationCodeableConcept().getText(),
                        medication.getAuthoredOn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        null
                ));
            }
            if (bundle.getLink(Bundle.LINK_NEXT) != null) {
                bundle = client.loadPage().next(bundle).execute();
            } else bundle = null;
        }

        res.sort(Comparator.comparing(
                patientHistoryElement -> patientHistoryElement.getWhen().toEpochDay()
        ));
        Collections.reverse(res);

        return res;
    }

}
