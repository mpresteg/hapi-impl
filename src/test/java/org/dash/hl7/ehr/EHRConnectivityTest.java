package org.dash.hl7.ehr;

import static org.junit.Assert.assertTrue;

import org.dash.hl7.FhirContextFactory;
import org.dash.hl7.dstu2.PatientPersistenceImpl;
import org.junit.Test;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class EHRConnectivityTest {
	
	private static final FhirContext fhirContext = FhirContextFactory.getFhirContext(FhirVersionEnum.DSTU2);

	@Test
	public void testEpicSandboxConnectivity() {
		HumanNameDt humanName = new HumanNameDt();
		humanName.addGiven("Jason");
		humanName.addFamily("Argonaut");
		
		IGenericClient epicClient = fhirContext.newRestfulGenericClient("https://open-ic.epic.com/FHIR/api/FHIR/DSTU2/");
		PatientPersistenceImpl helper = new PatientPersistenceImpl(epicClient);
		
		assertTrue(helper.hasPatient(humanName));
	}
	
	@Test
	public void testCernerSandboxConnectivity() {
		HumanNameDt humanName = new HumanNameDt();
		humanName.addGiven("Nancy");
		humanName.addFamily("Smart");
		
		IGenericClient cernerClient = fhirContext.newRestfulGenericClient("https://fhir-open.sandboxcerner.com/dstu2/0b8a0111-e8e6-4c26-a91c-5069cbc6b1ca/");
		PatientPersistenceImpl helper = new PatientPersistenceImpl(cernerClient);
		
		assertTrue(helper.hasPatient(humanName));
	}

}
