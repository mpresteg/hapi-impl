package org.dash.hl7.dstu3;

import java.util.List;
import java.util.stream.Collectors;

import org.dash.hl7.PatientPersistenceHelper;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

public class PatientPersistenceImpl extends PatientPersistenceHelper {
	public PatientPersistenceImpl(IGenericClient genericClient) {
		super(genericClient);
	}
	
	public boolean hasPatient(Identifier identifier) {
		ICriterion<TokenClientParam> patientIdCriterion = contructIdentifierCriterion(identifier);

		Bundle queryResponse = findPatient(patientIdCriterion);
		
		return queryResponse.hasEntry();
	}
	
	public Patient getPatient(Identifier identifier) {
		ICriterion<TokenClientParam> patientIdCriterion = contructIdentifierCriterion(identifier);
		
		Bundle queryResponse = findPatient(patientIdCriterion);
		
		return (Patient) queryResponse.getEntry();
	}

	private ICriterion<TokenClientParam> contructIdentifierCriterion(Identifier identifier) {
		String system = identifier.getSystem();

		ICriterion<TokenClientParam> patientIdCriterion = Patient.IDENTIFIER.exactly().systemAndIdentifier(system, identifier.getValue());
		return patientIdCriterion;
	}
	
	public boolean hasPatient(HumanName name) {
		Bundle queryResponse = findPatientByName(name);
		
		return queryResponse.hasEntry();
	}
	
	public Patient getPatient(HumanName name) {
		Bundle queryResponse = findPatientByName(name);
		
		return (Patient) queryResponse.getEntry();
	}

	public Bundle findPatientByName(HumanName name) {
		ICriterion<StringClientParam> familyNameCriterion = Patient.FAMILY.matches().value(name.getFamily());
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
