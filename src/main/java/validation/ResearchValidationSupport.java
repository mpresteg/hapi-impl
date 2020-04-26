package validation;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dash.hl7.FhirContextFactory;
import org.dash.hl7.StructureDefinitionBundler;
import org.hl7.fhir.dstu3.hapi.ctx.IValidationSupport;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.StructureDefinition;
import org.hl7.fhir.dstu3.model.ValueSet.ConceptSetComponent;
import org.hl7.fhir.dstu3.model.ValueSet.ValueSetExpansionComponent;
import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;

public class ResearchValidationSupport implements IValidationSupport {
	private Map<String, StructureDefinition> myStructureDefinitions;

    @SuppressWarnings("unchecked")
	@Override
	public <T extends IBaseResource> T fetchResource(FhirContext theContext, Class<T> theClass, String theUri) {
		return (T) fetchStructureDefinition(theContext, theUri);
	}

	@Override
	public boolean isCodeSystemSupported(FhirContext theContext, String theSystem) {
		return false;
	}

	@Override
	public CodeValidationResult validateCode(FhirContext theContext, String theCodeSystem, String theCode,
			String theDisplay) {
		return null;
	}

	@Override
	public List<IBaseResource> fetchAllConformanceResources(FhirContext theContext) {
		return null;
	}

	@Override
	public ValueSetExpansionComponent expandValueSet(FhirContext theContext, ConceptSetComponent theInclude) {
		return null;
	}

	@Override
	public List<StructureDefinition> fetchAllStructureDefinitions(FhirContext theContext) {
	    return new ArrayList<StructureDefinition>(provideStructureDefinitionMap(theContext).values());
	}

	@Override
	public CodeSystem fetchCodeSystem(FhirContext theContext, String theSystem) {
		return null;
	}

	@Override
	public StructureDefinition fetchStructureDefinition(FhirContext theCtx, String theUrl) {
		return provideStructureDefinitionMap(theCtx).get(theUrl);
	}
	
	private Map<String, StructureDefinition> provideStructureDefinitionMap(FhirContext theContext) {
		Map<String, StructureDefinition> structureDefinitions = myStructureDefinitions;
		if (structureDefinitions == null) {
		  structureDefinitions = new HashMap<String, StructureDefinition>();

		  InputStream stream = ResearchValidationSupport.class.getClassLoader().getResourceAsStream(StructureDefinitionBundler.RESEARCH_SDS);
		  
		  BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		  
		  Bundle bundle = (Bundle) FhirContextFactory.getFhirContext(FhirVersionEnum.DSTU3).newJsonParser().parseResource(reader);
		  
		  for (BundleEntryComponent next : bundle.getEntry()) {
		        if (next.getResource() instanceof StructureDefinition) {
		          StructureDefinition nextSd = (StructureDefinition) next.getResource();
		          nextSd.getText().setDivAsString("");
		          String system = nextSd.getUrl();
		          if (isNotBlank(system)) {
		            structureDefinitions.put(system, nextSd);
		          }
		        }
		      }
		  		  
		  myStructureDefinitions = structureDefinitions;
		}
		
		return structureDefinitions;
	}

}
