package pl.put.poznan.iwm.fhir;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FHIRDatabaseTest {

    private Pattern nameSearch;

    @Before
    public void setUp() throws Exception {
        nameSearch = Pattern.compile("^[A-Z][a-z]+");
    }

    @Test
    public void testPattern() {
        String name = "Amama3241";
        Matcher match = nameSearch.matcher(name);
        if (match.find()) {
            String res = match.group();
            assertEquals(res, "Amama");
        }else fail("brak dopasowania");
    }
}