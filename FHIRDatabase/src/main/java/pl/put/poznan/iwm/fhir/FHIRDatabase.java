package pl.put.poznan.iwm.fhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.*;
import ca.uhn.fhir.context.FhirContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
//                name.find();
//                last.find();
                res.add(new PatientData(
                        patient.getIdElement().getIdPart(),
                        (name.find()) ? name.group() : null,
                        (last.find()) ? last.group() : null,
                        (patient.hasBirthDate()) ? patient.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null,
                        (patient.hasDeceasedDateTimeType()) ? patient.getDeceasedDateTimeType().getValue()
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
        addDateRestriction(begin, end, query);
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
                        (observation.hasValue()) ? getObservationValue(observation) : null //TODO: obsługa
                ));
            }
            if (bundle.getLink(Bundle.LINK_NEXT) != null) {
                bundle = client.loadPage().next(bundle).execute();
            } else bundle = null;
        }

        query = client.search().forResource(MedicationRequest.class)
                .where(MedicationRequest.PATIENT.hasId(patientData.id()));
        addDateRestriction(begin, end, query);
        bundle = query.returnBundle(Bundle.class).execute();

        MedicationRequest medication;
        while (bundle != null) {

            for (var b : bundle.getEntry()) {
                medication = (MedicationRequest) b.getResource();
                res.add(new PatientMedicationElement(
                        medication.getIdElement().getIdPart(),
                        (medication.hasMedicationCodeableConcept()) ? medication.getMedicationCodeableConcept().getText() :
                                null,
                        medication.getAuthoredOn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        (medication.hasMedicationReference() && medication.getMedicationReference().hasReference()) ?
                                checkoutMedicine(medication.getMedicationReference().getReference()) :
                                "Form: No information\nDescription: No information\n",
                        (medication.hasDosageInstruction()) ?
                                medication.getDosageInstruction().stream().map(dosage -> dosage.getText())
                                        .collect(Collectors.joining("\n")) : null,
                        (medication.hasStatus()) ? medication.getStatus().getDisplay() : null,
                        (medication.hasPriority()) ? medication.getPriority().getDisplay() : null,
                        (medication.hasNote()) ? medication.getNote().stream().map(annotation -> annotation.getText())
                                .collect(Collectors.joining("\n")) : null
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

    private String checkoutMedicine(String medicationUrl) {
        Bundle bundle = client.loadPage().byUrl(medicationUrl).andReturnBundle(Bundle.class).execute();
        Medication medication;
        if (bundle != null) {
            medication = (Medication) bundle.getEntry().get(0).getResource();

            return String.format("Form: %s\nDescription: %s\n", medication.getForm().getText(), medication.getText());
        }
        return "Form: No information\nDescription: No information\n";
    }

    private void addDateRestriction(@Nullable LocalDate begin, @Nullable LocalDate end, IQuery<IBaseBundle> query) {
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
    }

    private String getObservationValue(Observation observation) {
        String result = null;
        if (observation.hasValueQuantity()) {
            result = observation.getValueQuantity().getValue().setScale(2, RoundingMode.HALF_EVEN).toString()
                    + " [" + observation.getValueQuantity().getUnit() + "]";
        } else if (observation.hasValueCodeableConcept()) {
            result = observation.getValueCodeableConcept().getText();
        } else if (observation.hasValueStringType()) {
            result = observation.getValueStringType().getValue();
        } else if (observation.hasValueRange()) {
            result = "range from " +
                    observation.getValueRange().getLow().getValue().setScale(2, RoundingMode.HALF_EVEN).toString()
                    + " to "
                    + observation.getValueRange().getHigh().getValue().setScale(2, RoundingMode.HALF_EVEN).toString()
                    + " " + observation.getValueRange().getHigh().getUnit();
        } else if (observation.hasValueRatio()) {
            result = "ratio " +
                    observation.getValueRatio().getNumerator().getValue().setScale(0, RoundingMode.HALF_EVEN).toString()
                    + ":" +
                    observation.getValueRatio().getDenominator().getValue().setScale(0, RoundingMode.HALF_EVEN).toString();
        } else if (observation.hasValueTimeType()) {
            result = observation.getValueTimeType().getValue();
        } else if (observation.hasValueDateTimeType()) {
            result = observation.getValueDateTimeType().getValue().toString();
        } else if (observation.hasValuePeriod()) {
            result = "period from " +
                    observation.getValuePeriod().getStart().toString() + " to "
                    + observation.getValuePeriod().getEnd().toString();
        } else if (observation.hasValueBooleanType()) {
            result = (observation.getValueBooleanType().getValue()) ? "Yes" : "No";
        } else if (observation.hasValueIntegerType()) {
            result = observation.getValueIntegerType().getValue().toString();
        } else return null;

        return result;
    }

}
