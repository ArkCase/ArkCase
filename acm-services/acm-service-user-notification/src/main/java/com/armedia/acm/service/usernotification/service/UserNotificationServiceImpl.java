/**
 * 
 */
package com.armedia.acm.service.usernotification.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.usernotification.model.AssignmentRule;
import com.armedia.acm.service.usernotification.model.UserNotificationConstants;

/**
 * @author riste.tutureski
 *
 */
public class UserNotificationServiceImpl implements UserNotificationService {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

    private boolean batchRun;
    private int batchSize;
    private PropertyFileManager propertyFileManager;
    private String userNotificationPropertyFileLocation;
    private AssignmentRule assignRule;
    private AssignmentRule unassignRule;
    private AcmAssignmentDao acmAssignmentDao;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat(UserNotificationConstants.DATE_FORMAT);
	
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
		
		String lastRunDate = getPropertyFileManager().load(getUserNotificationPropertyFileLocation(), UserNotificationConstants.SOLR_LAST_RUN_DATE_PROPERTY_KEY, UserNotificationConstants.DEFAULT_LAST_RUN_DATE);
		
		try
		{
			Date lastRun = getLastRunDate(lastRunDate);
			setLastRunDate();
			
			// Send assign notifications
			runRule(lastRun, getAssignRule());
			
			// Send unassign notifications
			runRule(lastRun, getUnassignRule());
						
		}
		catch(Exception e)
		{
			LOG.error("Cannot send notifications to the users: " + e.getMessage(), e);
		}
	}
	
	/**
	 * This method is called for executing the rule query and sending notifications
	 */
	@Override
	public void runRule(Date lastRun, AssignmentRule rule)
	{
		int currentBatchSize = 0;
		int batchSize = getBatchSize();
		
		List<AcmAssignment> assignments;
		
		do
		{
			assignments = getAcmAssignmentDao().executeQuery(lastRun, currentBatchSize, batchSize, rule.getJpaQuery());
			
			if ( !assignments.isEmpty() )
            {
				currentBatchSize += batchSize;
				
				// TODO: Send Notification for each "assignment" in "assignments" list. Discuss with James Wu how to re-use his code for notifications
            }
		}
		while ( !assignments.isEmpty() );
	}
	
	/**
	 * Get the last run date corrected for 1 minute before
	 * 
	 * @param lastRunDate
	 * @return
	 * @throws ParseException
	 */
	private Date getLastRunDate(String lastRunDate) throws ParseException
    {
        Date date = getDateFormat().parse(lastRunDate);

        Calendar calendar = Calendar.getInstance();
        
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 1);
        
        date = calendar.getTime();
        
        return date;
    }
	
	private void setLastRunDate()
	{
		String lastRunDate = getDateFormat().format(new Date());
		
		getPropertyFileManager().store(UserNotificationConstants.SOLR_LAST_RUN_DATE_PROPERTY_KEY, lastRunDate, getUserNotificationPropertyFileLocation());
	}

	public boolean isBatchRun() {
		return batchRun;
	}

	public void setBatchRun(boolean batchRun) {
		this.batchRun = batchRun;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public PropertyFileManager getPropertyFileManager() {
		return propertyFileManager;
	}

	public void setPropertyFileManager(PropertyFileManager propertyFileManager) {
		this.propertyFileManager = propertyFileManager;
	}

	public String getUserNotificationPropertyFileLocation() {
		return userNotificationPropertyFileLocation;
	}

	public void setUserNotificationPropertyFileLocation(String userNotificationPropertyFileLocation) {
		this.userNotificationPropertyFileLocation = userNotificationPropertyFileLocation;
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public AssignmentRule getAssignRule() {
		return assignRule;
	}

	public void setAssignRule(AssignmentRule assignRule) {
		this.assignRule = assignRule;
	}

	public AssignmentRule getUnassignRule() {
		return unassignRule;
	}

	public void setUnassignRule(AssignmentRule unassignRule) {
		this.unassignRule = unassignRule;
	}

	public AcmAssignmentDao getAcmAssignmentDao() {
		return acmAssignmentDao;
	}

	public void setAcmAssignmentDao(AcmAssignmentDao acmAssignmentDao) {
		this.acmAssignmentDao = acmAssignmentDao;
	}
}
