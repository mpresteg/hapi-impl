package validation;

import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.validation.IValidationContext;
import ca.uhn.fhir.validation.IValidatorModule;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;

/* Example of a custom validator...discard assuming we use the profile to define validation for simple things like presence of a birth date */
public class PatientValidatorModule implements IValidatorModule {
	@Override
	public void validateResource(IValidationContext<IBaseResource> validationContext) {		
		IBaseResource resource = validationContext.getResource();
		if (!(resource instanceof Patient)) {
			validationContext = addValidationMessage(validationContext, "Expected Patient Resource", ResultSeverityEnum.ERROR);
			
			return;
		}
		
		Patient patient = (Patient) resource;
		
		/* An example validation that can (and is) handled by the profile and associated validation - can be discarded */
		if (patient.getBirthDate() == null) {
			validationContext = addValidationMessage(validationContext, "Birthdate is required", ResultSeverityEnum.ERROR);
		}
		
		return;

	}
	
	private IValidationContext<IBaseResource> addValidationMessage(IValidationContext<IBaseResource> validationContext, String message, ResultSeverityEnum severity) {
		SingleValidationMessage validationMessage = new SingleValidationMessage();
		validationMessage.setMessage(message);
		validationMessage.setSeverity(severity);
		validationContext.addValidationMessage(validationMessage);
		
		return validationContext;
	}

}
