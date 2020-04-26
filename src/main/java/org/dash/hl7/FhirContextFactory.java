package org.dash.hl7;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.impl.RestfulClientFactory;

public class FhirContextFactory {	
	private static final String PROTOCOL_PROPERTY = "org.dash.hl7.protocol";
	private static final String HOSTNAME_PROPERTY = "org.dash.hl7.hostname";
	private static final String PORT_PROPERTY = "org.dash.hl7.port";
	private static final String BASE_CONTEXT_PROPERTY = "org.dash.hl7.baseContext";
	
	public static String PROTOCOL;
	public static String HOSTNAME;
	public static String PORT;
	public static String BASE_CONTEXT;
	
	private static String serverBase;
		
	static {
		PROTOCOL = System.getProperty(PROTOCOL_PROPERTY);
		HOSTNAME = System.getProperty(HOSTNAME_PROPERTY);
		PORT = System.getProperty(PORT_PROPERTY);
		BASE_CONTEXT = System.getProperty(BASE_CONTEXT_PROPERTY);
		
		if (PROTOCOL == null || HOSTNAME == null || PORT == null || BASE_CONTEXT == null) {
			PROTOCOL = "http";
			HOSTNAME = "localhost";
			PORT = "8080";
			BASE_CONTEXT = "baseDstu3";			
		}
		
		serverBase = PROTOCOL + "://" + HOSTNAME + ":" + PORT + "/" + BASE_CONTEXT;
				
		//client = FhirContextFactory.getFhirContext().newRestfulGenericClient("http://fhirtest.b12x.org/baseDstu3");
	}
	
	public static FhirContext getFhirContext(FhirVersionEnum fhirVersionEnum) {
		FhirContext context;
		
		switch (fhirVersionEnum) {
		case DSTU2:
			context = FhirContext.forDstu2();
			break;
		case DSTU2_1:
			context = FhirContext.forDstu2_1();
			break;
		case DSTU2_HL7ORG:
			context = FhirContext.forDstu2Hl7Org();
			break;
		case DSTU3:
			context = FhirContext.forDstu3();
			break;
		case R4:
			context = FhirContext.forR4();
			break;
		default:
			context = FhirContext.forDstu3();
			break;
		}
		
		return context;
	}

	public static IGenericClient getGenericClient(FhirContext context) {
		return context.newRestfulGenericClient(serverBase);
	}
	
	public static String getServerBase() {
		return serverBase;
	}
}
