package org.dash.hl7;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Bundle.HTTPVerb;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Quantity;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;

public class HapiTest {
	private static final Logger LOGGER = Logger.getLogger(HapiTest.class.getName());

	private static int ourPort;

	private static Server ourServer;
	
	private final String TEST_PATIENT_FILENAME = "testPatient.json";
	
	private Patient TEST_PATIENT;
	
	@AfterClass
	public static void afterClass() throws Exception {
		ourServer.stop();
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		/*
		 * This runs under maven, and I'm not sure how else to figure out the target directory from code..
		 */
		String path = HapiTest.class.getClassLoader().getResource(".keep_hapi-fhir-jpaserver-example").getPath();
		path = new File(path).getParent();
		path = new File(path).getParent();
		path = new File(path).getParent();

		LOGGER.info("Project base path is: {}" + path);

		ourPort = new Integer(ClientFactory.PORT);
		ourServer = new Server(ourPort);
		
		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setWar(path + "/src/main/lib/hapi-fhir-jpaserver-example.war");
		webAppContext.setParentLoaderPriority(true);

		ourServer.setHandler(webAppContext);
		ourServer.start();
	}
	
	@Before
	public void setUp() throws IOException {
		InputStream stream = HapiTest.class.getClassLoader().getResourceAsStream(TEST_PATIENT_FILENAME);
			
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		TEST_PATIENT = (Patient) FhirContextFactory.getFhirContext().newJsonParser().parseResource(reader);
	}

	@Test
	public void test() {
		PatientPersistenceHelper patientSearchHelper = new PatientPersistenceHelper();
		
		if (patientSearchHelper.hasPatient(TEST_PATIENT.getIdentifierFirstRep())) {
			LOGGER.info("Found test patient - updating");
			MethodOutcome outcome = patientSearchHelper.updatePatient(TEST_PATIENT);
			assertNotNull(outcome.getId());
		} else {
			LOGGER.info("Didn't find test patient - inserting");

			insertTestPatientAndObservation();			
		}
	}

	private void insertTestPatientAndObservation() {
		Observation observation = new Observation();
		observation.getSubject().setResource(TEST_PATIENT);
		observation.setStatus(ObservationStatus.PRELIMINARY);
		observation.getCode().addCoding().setSystem("http://loinc.org").setCode("8310-5").setDisplay("Body temperature");
		observation.setValue(new Quantity(98.6).setSystem("http://unitsofmeasure.org").setCode("Cel").setUnit("degree Celsius"));
		
		Bundle bundle = new Bundle();
		bundle.setType(BundleType.TRANSACTION);
		 
		// Add the patient as an entry. This entry is a POST with an
		// If-None-Exist header (conditional create) meaning that it
		// will only be created if there isn't already a Patient with
		// the identifier 54321
		bundle.addEntry()
		   .setFullUrl(TEST_PATIENT.getId())
		   .setResource(TEST_PATIENT)
		   .getRequest()
		      .setUrl("Patient")
		      //.setIfNoneExist("identifier=http://www.cibmtr.org|CRID54321")
		      .setMethod(HTTPVerb.POST);
		 
		// Add the observation. This entry is a POST with no header
		// (normal create) meaning that it will be created even if
		// a similar resource already exists.
		bundle.addEntry()
		   .setResource(observation)
		   .getRequest()
		      .setUrl("Observation")
		      .setMethod(HTTPVerb.POST);
		
		FhirContext fhirContext = FhirContextFactory.getFhirContext();
		 
		// Log the request
		LOGGER.info(fhirContext.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle));
		
		// Post the transaction to the server
		Bundle resp = ClientFactory.getGenericClient().transaction().withBundle(bundle).execute();
		 
		// Log the response
		LOGGER.info(fhirContext.newXmlParser().setPrettyPrint(true).encodeResourceToString(resp));
		assertTrue(resp.hasEntry());
	}

}
