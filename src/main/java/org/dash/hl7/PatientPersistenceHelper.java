package org.dash.hl7;

import java.util.logging.Logger;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

public class PatientPersistenceHelper {
	private static final Logger LOGGER = Logger.getLogger(PatientPersistenceHelper.class.getName());

	public boolean hasPatient(Identifier identifier) {
		Bundle queryResponse = findPatientById(constructSearchCriterion(identifier));
		
		return queryResponse.hasEntry();
	}
	
	public Patient getPatient(Identifier identifier) {
		Bundle queryResponse = findPatientById(constructSearchCriterion(identifier));

		return (Patient) queryResponse.getEntry();
	}
	
	private ICriterion<TokenClientParam> constructSearchCriterion(Identifier identifier) {
		String system = identifier.getSystem();

		ICriterion<TokenClientParam> patientIdCriterion = Patient.IDENTIFIER.exactly().systemAndIdentifier(system, identifier.getValue());
		
		return patientIdCriterion;
	}
	
	private Bundle findPatientById(ICriterion<TokenClientParam> criterion) {
		return findResource(Patient.class, criterion);
	}
	
	public MethodOutcome updatePatient(Patient patient) {
		return ClientFactory.getGenericClient().update().resource(patient).conditional().where(constructSearchCriterion(patient.getIdentifierFirstRep())).execute();
	}
	
	@SafeVarargs
	private final <T> Bundle findResource(Class<T> resourceClass, ICriterion<TokenClientParam>... criterion) {	
		Bundle queryResponse = new Bundle();
		
		IQuery<IBaseBundle> query = ClientFactory.getGenericClient().search().forResource(resourceClass.getSimpleName());
		for (ICriterion<TokenClientParam> singleCriterion : criterion) {
			int i=0;
			
			if (i ==0) {
				query.where(singleCriterion);
			}
			else {
				query.and(singleCriterion);
			}
		}
		
		try {
			queryResponse = query.returnBundle(Bundle.class).execute();
		}
		catch (ResourceNotFoundException e) {
			LOGGER.info("Patient resource not found.");
		}
		
		return queryResponse;
	}
}
