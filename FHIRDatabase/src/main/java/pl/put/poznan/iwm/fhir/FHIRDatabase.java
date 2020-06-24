package pl.put.poznan.iwm.fhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.*;
import ca.uhn.fhir.context.FhirContext;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
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
                        patient.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                ));
            }

            if (bundle.getLink(Bundle.LINK_NEXT) != null)
                bundle = client.loadPage().next(bundle).execute();
            else
                bundle = null;
        }
        return res;
    }

    

}
