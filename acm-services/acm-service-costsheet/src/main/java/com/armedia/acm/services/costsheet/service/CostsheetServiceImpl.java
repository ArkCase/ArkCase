/**
 * 
 */
package com.armedia.acm.services.costsheet.service;

import java.util.List;
import java.util.Map;

import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.armedia.acm.services.costsheet.dao.AcmCostsheetDao;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

/**
 * @author riste.tutureski
 *
 */
public class CostsheetServiceImpl implements CostsheetService {
	
	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private AcmCostsheetDao acmCostsheetDao;
	private Map<String, String> submissionStatusesMap;
	private ExecuteSolrQuery executeSolrQuery;
	
	@Override
	public AcmCostsheet save(AcmCostsheet costsheet) 
	{
		AcmCostsheet saved = getAcmCostsheetDao().save(costsheet);
		
		return saved;
	}
	
	@Override
	public AcmCostsheet save(AcmCostsheet costsheet, String submissionName) 
	{		
		costsheet.setStatus(getSubmissionStatusesMap().get(submissionName));
		AcmCostsheet saved = getAcmCostsheetDao().save(costsheet);
		
		return saved;
	}
	
	@Override
	public AcmCostsheet get(Long id) 
	{
		AcmCostsheet timesheet = getAcmCostsheetDao().find(id);
		
		return timesheet;
	}
	
	/**
	 * Return costsheets for specified object id and object type
	 */
	@Override
	public List<AcmCostsheet> getByObjectIdAndType(Long objectId, String objectType, int startRow, int maxRows, String sortParams)
	{
		List<AcmCostsheet> retval = null;
		
		if (objectId != null)
		{
			// Get costsheets form database for given object id and type
			retval = getAcmCostsheetDao().findByObjectIdAndType(objectId, objectType, startRow, maxRows, sortParams);
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

	public ExecuteSolrQuery getExecuteSolrQuery() {
		return executeSolrQuery;
	}

	public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery) {
		this.executeSolrQuery = executeSolrQuery;
	}
}
