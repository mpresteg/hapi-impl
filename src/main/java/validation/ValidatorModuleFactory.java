package validation;

import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Resource;

import ca.uhn.fhir.validation.IValidatorModule;

/* Useful if we are going to engage in custom validations beyond what may be easily offered in a profile */

public class ValidatorModuleFactory {
	
	private static ValidatorModuleFactory instance;
	
	private ValidatorModuleFactory() {
		
	}
	
	public static ValidatorModuleFactory getInstance() {
		if (instance == null) {
			instance = new ValidatorModuleFactory();
		}
		
		return instance;
	}
	
	public IValidatorModule getValidator(Resource resource) {
		if (resource instanceof Patient) {
			return new PatientValidatorModule();
		}
		
		return null;
	}
}
