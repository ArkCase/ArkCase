/**
 * 
 */
package com.armedia.acm.services.costsheet.service;

import org.json.JSONObject;
import org.springframework.security.core.Authentication;

import com.armedia.acm.services.costsheet.model.AcmCostsheet;

/**
 * @author riste.tutureski
 *
 */
public interface CostsheetService {

	public AcmCostsheet save(AcmCostsheet costsheet, String submissionName);
	public JSONObject getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams);
	
}
