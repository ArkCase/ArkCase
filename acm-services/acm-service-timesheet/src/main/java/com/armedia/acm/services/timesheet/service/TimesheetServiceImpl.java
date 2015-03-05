/**
 * 
 */
package com.armedia.acm.services.timesheet.service;

import java.util.Map;

import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;

/**
 * @author riste.tutureski
 *
 */
public class TimesheetServiceImpl implements TimesheetService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private AcmTimesheetDao acmTimesheetDao;
	private Map<String, String> submissionStatusesMap;
	private ExecuteSolrQuery executeSolrQuery;
	
	@Override
	public AcmTimesheet save(AcmTimesheet timesheet, String submissionName) 
	{
		timesheet.setStatus(getSubmissionStatusesMap().get(submissionName));
		AcmTimesheet saved = getAcmTimesheetDao().save(timesheet);
		
		return saved;
	}
	
	@Override
	public JSONObject getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams) 
	{
		JSONObject retval = null;
				
		LOG.debug("Taking objects from Solr for object type = " + objectType);
		
		String query = "object_type_s:" + objectType + " AND -status_s:DELETE";
		
		try 
		{
			String result = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, query, startRow, maxRows, sortParams);
			retval = new JSONObject(result);
			
			LOG.debug("Objects was retrieved.");
		} 
		catch (MuleException e) 
		{
			LOG.error("Cannot retrieve objects from Solr.", e);
		}
		
		return retval;
	}

	public AcmTimesheetDao getAcmTimesheetDao() {
		return acmTimesheetDao;
	}

	public void setAcmTimesheetDao(AcmTimesheetDao acmTimesheetDao) {
		this.acmTimesheetDao = acmTimesheetDao;
	}

	public Map<String, String> getSubmissionStatusesMap() {
		return submissionStatusesMap;
	}

	public void setSubmissionStatusesMap(Map<String, String> submissionStatusesMap) {
		this.submissionStatusesMap = submissionStatusesMap;
	}

	public ExecuteSolrQuery getExecuteSolrQuery() {
		return executeSolrQuery;
	}

	public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery) {
		this.executeSolrQuery = executeSolrQuery;
	}
}
