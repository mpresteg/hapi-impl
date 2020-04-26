package org.dash.hl7;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.primitive.IdDt;

public class StructureDefinitionBundler {
	public static final String RESEARCH_PATIENT_SD = "CIBMTRPatientTest.json";
	public static final String RESEARCH_SDS = "ResearchStructureDefinitions.json";
	public static final String RESOURCES_PATH = "src/main/resources/";
	
	public static final Set<String> structureDefinitions = new HashSet<String>();
	
	public static final FhirContext fhirContext = FhirContextFactory.getFhirContext(FhirVersionEnum.DSTU3);
	
	static {
		structureDefinitions.add(RESEARCH_PATIENT_SD);
	}
	
	public static void main(String args[]) {
		writeBundledStructureDefinitions();
	}
	
	public static Bundle bundleStructureDefinitions() {
		Bundle bundle = new Bundle();
		bundle.setType(BundleType.COLLECTION);
		bundle.setId(IdDt.newRandomUuid());
		
		for (String sdString : structureDefinitions) {
			InputStream stream = StructureDefinitionBundler.class.getClassLoader().getResourceAsStream(sdString);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			
			Resource resource = (Resource) fhirContext.newJsonParser().parseResource(reader);
			BundleEntryComponent bec = new BundleEntryComponent();
			bec.setResource(resource);
			bundle.addEntry(bec);
		}
		
		return bundle;
	}
	
	public static void writeBundledStructureDefinitions() {
		Bundle bundle = bundleStructureDefinitions();
		
		String output = fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
		
		try {
			FileWriter writer = new FileWriter(new File(RESOURCES_PATH + RESEARCH_SDS));
			
			writer.write(output);
			
			writer.flush();
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
