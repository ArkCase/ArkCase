/**
 * 
 */
package com.armedia.acm.services.costsheet.service;

import org.springframework.security.core.Authentication;

import com.armedia.acm.services.costsheet.model.AcmCostsheet;

/**
 * @author riste.tutureski
 *
 */
public interface CostsheetService {

	public AcmCostsheet save(AcmCostsheet costsheet, String submissionName);
	public String getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams);
	
}
