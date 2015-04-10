/**
 * 
 */
package com.armedia.acm.audit.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.audit.model.AuditConstants;
import com.armedia.acm.audit.model.AuditEvent;

/**
 * @author riste.tutureski
 *
 */
public class AuditServiceImpl implements AuditService {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private AuditDao auditDao;
	private Properties auditPluginProperties;
	
	/**
	 * This method is called by scheduled task
	 */
	@Override
	public void run() 
	{
		if (!isBatchRun())
		{
			return;
		}
		
		int firstResult = 0;
		int maxResult = getMaxResults();
		Date dateThreshold = createPurgeThreshold();
		
		List<AuditEvent> audits;
		
		do 
		{
			audits = getAuditDao().findAuditsForPurge(dateThreshold, firstResult, maxResult);
			
			if ( !audits.isEmpty() )
            {	
				audits.stream()
				      .map((element) -> {element.setStatus(AuditConstants.STATUS_DELETE); return element;})
					  .forEach(element -> {getAuditDao().save(element);});
            }
		}
		while (!audits.isEmpty());
	}
	
	private Date createPurgeThreshold()
	{
		int purgeDays = getPurgeDays();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -purgeDays);
		
		return calendar.getTime();
	}
	
	private int getMaxResults()
	{
		String batchSizeString = (String) getAuditPluginProperties().get(AuditConstants.PROPERTY_BATCH_SIZE);
		
		return getInt(batchSizeString);
	}
	
	private int getPurgeDays()
	{
		String purgeDaysString = (String) getAuditPluginProperties().get(AuditConstants.PROPERTY_PURGE_DAYS);
		
		return getInt(purgeDaysString);
	}
	
	private boolean isBatchRun()
	{
		String batchRunString = (String) getAuditPluginProperties().get(AuditConstants.PROPERTY_BATCH_RUN);
		
		return getBoolean(batchRunString);
	}
	
	private int getInt(String value)
	{
		int retval = 0;
		
		try
		{
			retval = Integer.parseInt(value);
		}
		catch(Exception e)
		{
			LOG.error("Cannot parse string " + value + " to integer.", e);
		}
		
		return retval;
	}
	
	private boolean getBoolean(String value)
	{
		boolean retval = false;
		
		try
		{
			retval = Boolean.parseBoolean(value);
		}
		catch(Exception e)
		{
			LOG.error("Cannot parse string " + value + " to integer.", e);
		}
		
		return retval;
	}

	public AuditDao getAuditDao() {
		return auditDao;
	}

	public void setAuditDao(AuditDao auditDao) {
		this.auditDao = auditDao;
	}

	public Properties getAuditPluginProperties() {
		return auditPluginProperties;
	}

	public void setAuditPluginProperties(Properties auditPluginProperties) {
		this.auditPluginProperties = auditPluginProperties;
	}

}
