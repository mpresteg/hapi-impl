package org.dash.hl7.dstu2;

import java.util.List;
import java.util.stream.Collectors;

import org.dash.hl7.PatientPersistenceHelper;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

public class PatientPersistenceImpl extends PatientPersistenceHelper {
	
	public PatientPersistenceImpl(IGenericClient genericClient) {
		super(genericClient);
	}
	
	public boolean hasPatient(IdentifierDt identifier) {
		ICriterion<TokenClientParam> patientIdCriterion = contructIdentifierCriterion(identifier);

		Bundle queryResponse = findPatient(patientIdCriterion);
		
		return !queryResponse.getEntry().isEmpty();
	}
	
	public Patient getPatient(IdentifierDt identifier) {
		ICriterion<TokenClientParam> patientIdCriterion = contructIdentifierCriterion(identifier);
		
		Bundle queryResponse = findPatient(patientIdCriterion);
		
		return (Patient) queryResponse.getEntry();
	}

	private ICriterion<TokenClientParam> contructIdentifierCriterion(IdentifierDt identifier) {
		String system = identifier.getSystem();

		ICriterion<TokenClientParam> patientIdCriterion = Patient.IDENTIFIER.exactly().systemAndIdentifier(system, identifier.getValue());
		return patientIdCriterion;
	}
	
	public boolean hasPatient(HumanNameDt name) {
		Bundle queryResponse = findPatientByName(name);
		
		IResource resource = queryResponse.getEntryFirstRep().getResource();
		
		return resource != null && resource.getClass().equals(Patient.class) ? true : false;
	}
	
	public Patient getPatient(HumanNameDt name) {
		Bundle queryResponse = findPatientByName(name);
		
		return (Patient) queryResponse.getEntry();
	}

	public Bundle findPatientByName(HumanNameDt name) {
		List<String> familyNames = name.getFamily().stream().map(Object::toString)
                .collect(Collectors.toList());
		ICriterion<StringClientParam> familyNameCriterion = Patient.FAMILY.matches().values(familyNames);
		
		List<String> givenNames = name.getGiven().stream().map(Object::toString)
                .collect(Collectors.toList());
		ICriterion<StringClientParam> givenNameCriterion = Patient.GIVEN.matches().values(givenNames);
		
		Bundle queryResponse = findPatient(familyNameCriterion, givenNameCriterion);
		return queryResponse;
	}
	
	@Override
	public Bundle findPatient(ICriterion<?>... criterion) {
		return (Bundle) findResource(Patient.class, criterion);
	}
	
	public MethodOutcome updatePatient(Patient patient) {
		return getGenericClient().update().resource(patient).conditional().where(contructIdentifierCriterion(patient.getIdentifierFirstRep())).execute();
	}
}
