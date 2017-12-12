package validation;

import java.util.ArrayList;
import java.util.List;

import org.dash.hl7.FhirContextFactory;
import org.hl7.fhir.dstu3.hapi.ctx.IValidationSupport;
import org.hl7.fhir.dstu3.hapi.validation.DefaultProfileValidationSupport;
import org.hl7.fhir.dstu3.hapi.validation.FhirInstanceValidator;
import org.hl7.fhir.dstu3.hapi.validation.ValidationSupportChain;
import org.hl7.fhir.dstu3.model.Resource;

import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;

public class ValidationHelper {
	private static ValidationHelper instance;
	private DefaultProfileValidationSupport defaultProfileValidationSupport = new DefaultProfileValidationSupport();
	private ResearchValidationSupport researchValidationSupport = new ResearchValidationSupport();
	
	private ValidationHelper() {
		
	}
	
	public static ValidationHelper getInstance() {
		if (instance == null) {
			instance = new ValidationHelper();
		}
		
		return instance;
	}
	
	public ValidationResult doDefaultProfileValidation(Resource resource) {
		return doValidate(resource, defaultProfileValidationSupport);
	}
	
	public ValidationResult doResearchProfileValidation(Resource resource) {
		return doValidate(resource, defaultProfileValidationSupport, researchValidationSupport);
	}
	
	private ValidationResult doValidate(Resource resource, IValidationSupport... validationSupport) {
		FhirValidator validator = FhirContextFactory.getFhirContext().newValidator();
		FhirInstanceValidator instanceValidator = new FhirInstanceValidator();
		
		validator.registerValidatorModule(instanceValidator);
		
		ValidationSupportChain support = new ValidationSupportChain(validationSupport);
		instanceValidator.setValidationSupport(support);
		
		ValidationResult validationResult = validator.validateWithResult(resource);
		
		return validationResult;
	}
	
	public List<SingleValidationMessage> returnValidationMessages(ValidationResult validationResult, ResultSeverityEnum... resultSeverity) {
		List<SingleValidationMessage> allMessages = validationResult.getMessages();
		List<SingleValidationMessage> messages;
		
		if (resultSeverity == null || allMessages == null) return allMessages;
		
		messages = new ArrayList<SingleValidationMessage>();
		
		for (SingleValidationMessage singleMessage : allMessages) {
			for (ResultSeverityEnum severity : resultSeverity) {
				if (severity == singleMessage.getSeverity()) messages.add(singleMessage);
			}
		}
		
		return messages;
	}
}
