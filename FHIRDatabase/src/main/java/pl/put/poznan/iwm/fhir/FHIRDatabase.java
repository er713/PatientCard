package pl.put.poznan.iwm.fhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.*;
import ca.uhn.fhir.context.FhirContext;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

public class FHIRDatabase {

    private IGenericClient client;
    private String path;

    public FHIRDatabase(String url) {
        path = url;
        client = FhirContext.forR4().newRestfulGenericClient(path);
    }

    public List<PatientData> getPatientList(){
        var res = new ArrayList<PatientData>();

        Bundle bundle = client.search().forResource(Patient.class).returnBundle(Bundle.class).execute();

        Patient patient;
        List<Bundle.BundleEntryComponent> pat;
        while (bundle != null) {
            pat = bundle.getEntry();
            for(var p: pat){
                patient = (Patient) p.getResource();
                res.add(new PatientData(
                        patient.getIdElement().getIdPart(),
                        patient.getName().get(0).getGivenAsSingleString(),
                        patient.getName().get(0).getFamily(),
                        patient.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                ));
            }

            if(bundle.getLink(Bundle.LINK_NEXT)!=null)
                bundle = client.loadPage().next(bundle).execute();
            else
                bundle = null;
        }
        return res;
    }

}
