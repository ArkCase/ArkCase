/**
 * 
 */
package com.armedia.acm.services.costsheet.service;

import java.util.Map;

import com.armedia.acm.services.costsheet.dao.AcmCostsheetDao;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;

/**
 * @author riste.tutureski
 *
 */
public class CostsheetServiceImpl implements CostsheetService {
	
	private AcmCostsheetDao acmCostsheetDao;
	private Map<String, String> submissionStatusesMap;
	
	@Override
	public AcmCostsheet save(AcmCostsheet costsheet, String submissionName) 
	{		
		costsheet.setStatus(getSubmissionStatusesMap().get(submissionName));
		AcmCostsheet saved = getAcmCostsheetDao().save(costsheet);
		
		return saved;
	}

	public AcmCostsheetDao getAcmCostsheetDao() {
		return acmCostsheetDao;
	}

	public void setAcmCostsheetDao(AcmCostsheetDao acmCostsheetDao) {
		this.acmCostsheetDao = acmCostsheetDao;
	}

	public Map<String, String> getSubmissionStatusesMap() {
		return submissionStatusesMap;
	}

	public void setSubmissionStatusesMap(Map<String, String> submissionStatusesMap) {
		this.submissionStatusesMap = submissionStatusesMap;
	}
}
