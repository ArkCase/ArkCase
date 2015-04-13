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
	
	private boolean batchRun;
	private int purgeDays;
	private AuditDao auditDao;
	
	/**
	 * This method is called by scheduled task
	 */
	@Override
	public void purgeBatchRun() 
	{
		if (!isBatchRun())
		{
			return;
		}
		
		Date dateThreshold = createPurgeThreshold();
		
		int deletedAudits = getAuditDao().purgeAudits(dateThreshold);
		
		LOG.debug(deletedAudits + " audits was deleted.");
	}
	
	private Date createPurgeThreshold()
	{
		int purgeDays = getPurgeDays();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -purgeDays);
		
		return calendar.getTime();
	}

	public boolean isBatchRun() {
		return batchRun;
	}

	public void setBatchRun(boolean batchRun) {
		this.batchRun = batchRun;
	}

	public int getPurgeDays() {
		return purgeDays;
	}

	public void setPurgeDays(int purgeDays) {
		this.purgeDays = purgeDays;
	}

	public AuditDao getAuditDao() {
		return auditDao;
	}

	public void setAuditDao(AuditDao auditDao) {
		this.auditDao = auditDao;
	}

}
