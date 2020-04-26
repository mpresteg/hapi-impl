package org.dash.hl7.model;

import org.hl7.fhir.instance.model.api.IBaseBundle;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.IFhirVersion;

public enum BundleEnum {
	DSTU2_BUNDLE (FhirVersionEnum.DSTU2.getVersionImplementation(), ca.uhn.fhir.model.dstu2.resource.Bundle.class),
	DSTU3_BUNDLE (FhirVersionEnum.DSTU3.getVersionImplementation(), org.hl7.fhir.dstu3.model.Bundle.class);
	
	private IFhirVersion fhirVersion;
	private Class<IBaseBundle> bundleClass;
	
	@SuppressWarnings("unchecked")
	private BundleEnum(IFhirVersion fhirVersion, Class<?> bundleClass) {
		this.fhirVersion = fhirVersion;
		this.bundleClass = (Class<IBaseBundle>) bundleClass;
	}
	
	private IFhirVersion getFhirVersion() {
		return this.fhirVersion;
	}
	
	private Class<IBaseBundle> getBundleClass() {
		return this.bundleClass;
	}
	
	public static Class<IBaseBundle> lookup(IFhirVersion fhirVersion) {
		for (BundleEnum bundleEnum : values()) {
			if (fhirVersion.equals(bundleEnum.getFhirVersion())) {
				return bundleEnum.getBundleClass();
			}
		}
		
		// TODO:  Fix this
		return null;
	}
}
