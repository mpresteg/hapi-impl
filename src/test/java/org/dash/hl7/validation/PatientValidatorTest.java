package org.dash.hl7.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.dash.hl7.FhirContextFactory;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.junit.Before;
import org.junit.Test;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.ValidationResult;
import validation.ValidationHelper;
import validation.ValidatorModuleFactory;

public class PatientValidatorTest {
	private static final Logger LOGGER = Logger.getLogger(PatientValidatorTest.class.getName());
	private final String TEST_PATIENT_FILENAME = "testPatient.json";
	private final String TEST_RESEARCH_PATIENT_FILENAME = "researchTestPatient.json";
	
	private Patient TEST_PATIENT;
	private Patient TEST_RESEARCH_PATIENT;
	
	private static FhirContext fhirContext;
	
	static {
		fhirContext = FhirContextFactory.getFhirContext(FhirVersionEnum.DSTU3);
	}
	
	@Before
	public void setUp() throws IOException {
		InputStream stream = PatientValidatorTest.class.getClassLoader().getResourceAsStream(TEST_PATIENT_FILENAME);
		if (stream == null) {
			stream = new FileInputStream(TEST_PATIENT_FILENAME);
		}
			
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		TEST_PATIENT = (Patient) fhirContext.newJsonParser().parseResource(reader);
		
		reader.close();
		stream.close();
		
		stream = PatientValidatorTest.class.getClassLoader().getResourceAsStream(TEST_RESEARCH_PATIENT_FILENAME);
		if (stream == null) {
			stream = new FileInputStream(TEST_RESEARCH_PATIENT_FILENAME);
		}
			
		reader = new BufferedReader(new InputStreamReader(stream));
		TEST_RESEARCH_PATIENT = (Patient) fhirContext.newJsonParser().parseResource(reader);
	}
	
	@Test
	public void testValid() {
		FhirValidator validator = fhirContext.newValidator();

		validator.registerValidatorModule(ValidatorModuleFactory.getInstance().getValidator(TEST_PATIENT));
		
		ValidationResult validationResult = validator.validateWithResult(TEST_PATIENT);
		
		assertTrue(validationResult.isSuccessful());
	}
	
	@Test
	public void testInvalidWithObservation() {
		FhirValidator validator = fhirContext.newValidator();
		validator.registerValidatorModule(ValidatorModuleFactory.getInstance().getValidator(TEST_PATIENT));
		
		ValidationResult validationResult = validator.validateWithResult(new Observation());
		
		assertFalse(validationResult.isSuccessful());
		
		LOGGER.info(validationResult.getMessages().toString());
	}
	
	@Test
	public void testInvalidNoBirthdate() {
		FhirValidator validator = fhirContext.newValidator();
		validator.registerValidatorModule(ValidatorModuleFactory.getInstance().getValidator(TEST_PATIENT));
		
		Patient patientNoBirthdate = TEST_PATIENT.copy();
		patientNoBirthdate.setBirthDate(null);
		
		ValidationResult validationResult = validator.validateWithResult(patientNoBirthdate);
		
		assertFalse(validationResult.isSuccessful());
		
		LOGGER.info(validationResult.getMessages().toString());
	}
	
	@Test
	public void testInvalidNoGenderProfile() throws IOException {
		Patient patientNoGender = TEST_RESEARCH_PATIENT.copy();
		patientNoGender.setGender(null);
		
		ValidationResult validationResult = ValidationHelper.getInstance().doResearchProfileValidation(patientNoGender);
		
		assertFalse(validationResult.isSuccessful());
		
		LOGGER.info(ValidationHelper.getInstance().returnValidationMessages(validationResult, ResultSeverityEnum.WARNING, ResultSeverityEnum.ERROR, ResultSeverityEnum.FATAL).toString());
	}
	
	@Test
	public void testValidResearchPatientProfile() throws IOException {
		ValidationResult validationResult = ValidationHelper.getInstance().doResearchProfileValidation(TEST_RESEARCH_PATIENT);
		
		assertTrue(validationResult.isSuccessful());
	}
}
