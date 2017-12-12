package org.dash.hl7;

import ca.uhn.fhir.rest.client.api.IGenericClient;

public class ClientFactory {
	private static IGenericClient client;
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
		client = FhirContextFactory.getFhirContext().newRestfulGenericClient(serverBase);
		
		//client = FhirContextFactory.getFhirContext().newRestfulGenericClient("http://fhirtest.b12x.org/baseDstu3");
	}

	public static IGenericClient getGenericClient() {
		return client;
	}
	
	public static String getServerBase() {
		return serverBase;
	}
}
