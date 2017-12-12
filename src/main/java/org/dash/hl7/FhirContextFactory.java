package org.dash.hl7;

import ca.uhn.fhir.context.FhirContext;

public class FhirContextFactory {
	private static final FhirContext fhirContext;
		
	static {
		fhirContext = FhirContext.forDstu3();
	}
	
	public static FhirContext getFhirContext() {
		return fhirContext;
	}
}
