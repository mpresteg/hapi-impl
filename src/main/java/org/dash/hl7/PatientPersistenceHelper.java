package org.dash.hl7;

import java.util.logging.Logger;

import org.dash.hl7.model.BundleEnum;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

public abstract class PatientPersistenceHelper {
	private static final Logger LOGGER = Logger.getLogger(PatientPersistenceHelper.class.getName());
	private IGenericClient genericClient;
	
	public IGenericClient getGenericClient() {
		return genericClient;
	}
	
	public PatientPersistenceHelper(IGenericClient genericClient) {
		this.genericClient = genericClient;
	}
	
	public abstract IBaseBundle findPatient(ICriterion<?>... criterion);
	
	@SafeVarargs
	public final <T> IBaseBundle findResource(Class<T> resourceClass, ICriterion<?>... criterion) {	
		IBaseBundle queryResponse = null;
		
		IQuery<IBaseBundle> query = genericClient.search().forResource(resourceClass.getSimpleName());
		for (ICriterion<?> singleCriterion : criterion) {
			int i=0;
			
			if (i ==0) {
				query.where(singleCriterion);
			}
			else {
				query.and(singleCriterion);
			}
		}
		
		try {
			queryResponse = (IBaseBundle) query.returnBundle(BundleEnum.lookup(genericClient.getFhirContext().getVersion())).execute();
		}
		catch (ResourceNotFoundException e) {
			LOGGER.info("Patient resource not found.");
		}
		
		return queryResponse;
	}
}
