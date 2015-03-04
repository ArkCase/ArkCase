/**
 * 
 */
package com.armedia.acm.services.costsheet.service;

import com.armedia.acm.services.costsheet.model.AcmCostsheet;

/**
 * @author riste.tutureski
 *
 */
public interface CostsheetService {

	public AcmCostsheet save(AcmCostsheet costsheet, String submissionName);
	
}
