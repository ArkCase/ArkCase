/**
 * 
 */
package com.armedia.acm.services.timesheet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTime;
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
	public AcmTimesheet save(AcmTimesheet timesheet) 
	{
		AcmTimesheet saved = getAcmTimesheetDao().save(timesheet);
		
		return saved;
	}
	
	@Override
	public AcmTimesheet save(AcmTimesheet timesheet, String submissionName) 
	{
		timesheet.setStatus(getSubmissionStatusesMap().get(submissionName));
		AcmTimesheet saved = getAcmTimesheetDao().save(timesheet);
		
		return saved;
	}
	
	@Override
	public AcmTimesheet get(Long id) 
	{
		AcmTimesheet timesheet = getAcmTimesheetDao().find(id);
		
		return timesheet;
	}
	
	/**
	 * Return timesheets with times only for specified object id
	 */
	@Override
	public List<AcmTimesheet> getByObjectIdAndType(Long objectId, String objectType, int startRow, int maxRows, String sortParams)
	{
		List<AcmTimesheet> retval = null;
		
		if (objectId != null)
		{
			// Get timesheets form database for given object id
			List<AcmTimesheet> timesheets = getAcmTimesheetDao().findByObjectIdAndType(objectId, objectType, startRow, maxRows, sortParams);
		
			if (timesheets != null)
			{
				for (AcmTimesheet timesheet : timesheets)
				{
					List<AcmTime> times = getTimesForObjectId(timesheet.getTimes(), objectId);
					timesheet.setTimes(times);
					
					if (retval == null)
					{
						retval = new ArrayList<AcmTimesheet>();
					}
					
					retval.add(timesheet);
				}
			}
		}
		
		return retval;
	}
	
	private List<AcmTime> getTimesForObjectId(List<AcmTime> times, Long objectId)
	{
		List<AcmTime> retval = new ArrayList<AcmTime>();
		
		if (times != null)
		{			
			for (AcmTime time : times)
			{
				// If provided objectId is equal with id in the AcmTime, then we should show on UI, otherwise not - skip
				if (objectId.equals(time.getObjectId()))
				{
					retval.add(time);
				}
			}
		}
		
		return retval;
	}
	
	@Override
	public String getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams, String userId) 
	{
		String retval = null;
				
		LOG.debug("Taking objects from Solr for object type = " + objectType);
		
		String authorQuery = "";
		if (userId != null)
		{
			authorQuery = " AND author:" + userId;
		}
		
		String query = "object_type_s:" + objectType + authorQuery + " AND -status_s:DELETE";
		
		try 
		{
			retval = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, query, startRow, maxRows, sortParams);
			
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
