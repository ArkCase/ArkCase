package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.dataaccess.model.DataAccessControlConstants;
import com.armedia.acm.services.dataaccess.service.AcmObjectDataAccessBatchUpdateLocator;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/28/14.
 */
public class AcmDataAccessBatchPolicyUpdateService
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * The default run date to use if this generator has never run before (or if the properties file that stores the
     * last run date is missing)
     */
    private static final String DEFAULT_LAST_RUN_DATE = "1970-01-01T00:00:00Z";

    /**
     * The property key to use in the properties file that stores the last run date.
     */
    private static final String DAC_LAST_RUN_DATE_PROPERTY_KEY = "dac.last.run.date";

    private boolean batchUpdateBasedOnLastModifiedEnabled;
    private String lastBatchUpdatePropertyFileLocation;
    private PropertyFileManager propertyFileManager;
    private SpringContextHolder springContextHolder;
    private int batchSize;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private AcmDataAccessBatchUpdater dataAccessBatchUpdater;

    public void batchPolicyUpdate()
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("DAC batch update enabled: " + isBatchUpdateBasedOnLastModifiedEnabled());
        }

        if ( !isBatchUpdateBasedOnLastModifiedEnabled() )
        {
            return;
        }

        getAuditPropertyEntityAdapter().setUserId("DAC-BATCH-UPDATE");

        String lastRunDate = getPropertyFileManager().load(
                getLastBatchUpdatePropertyFileLocation(),
                DAC_LAST_RUN_DATE_PROPERTY_KEY,
                DEFAULT_LAST_RUN_DATE);
        DateFormat solrDateFormat = new SimpleDateFormat(DataAccessControlConstants.LAST_RUN_DATE_FORMAT);

        try
        {
            Date lastBatchRunDate = getLastBatchRunDate(lastRunDate, solrDateFormat);
            storeCurrentDateForNextBatchRun(solrDateFormat);

            if ( log.isDebugEnabled() )
            {
                log.debug("Checking for objects modified since: " + lastBatchRunDate);
            }

            Collection<? extends AcmObjectDataAccessBatchUpdateLocator> locators =
                    getSpringContextHolder().getAllBeansOfType(AcmObjectDataAccessBatchUpdateLocator.class).values();
            if ( log.isDebugEnabled() )
            {
                log.debug(locators.size() + " object locators found.");
            }

            for (AcmObjectDataAccessBatchUpdateLocator locator : locators)
            {
            	try
            	{
            		updateDataAccessControlPolicy(lastBatchRunDate, locator);
            	}
            	catch(Exception exception)
            	{
            		log.error("Could not update data access controls for locator " + locator.getClass(), exception);
            	}
            }
        }
        catch (ParseException e)
        {
            log.error("Could not update data access controls: " + e.getMessage(), e);
        }
    }

    private void storeCurrentDateForNextBatchRun(DateFormat dateFormat)
    {
        // store the current time as the last run date to use the next time this job runs.  This allows us to
        // scan only for objects updated since this date.
        String now = dateFormat.format(new Date());
        getPropertyFileManager().store(DAC_LAST_RUN_DATE_PROPERTY_KEY, now, getLastBatchUpdatePropertyFileLocation());
    }

    private Date getLastBatchRunDate(String lastRunDate, DateFormat solrDateFormat) throws ParseException
    {
        Date sinceWhen = solrDateFormat.parse(lastRunDate);

        // back up one minute just to be sure we get everything
        Calendar cal = Calendar.getInstance();
        cal.setTime(sinceWhen);
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - 1);
        sinceWhen = cal.getTime();
        return sinceWhen;
    }

    private void updateDataAccessControlPolicy(Date lastUpdate, AcmObjectDataAccessBatchUpdateLocator locator)
    {
        boolean debug = log.isDebugEnabled();

        if ( debug )
        {
            log.debug("Handling locator type: " + locator.getClass().getName() + "; last mod date: " + lastUpdate);
        }

        int current = 0;
        int batchSize = getBatchSize();

        // keep retrieving another batch of objects modified since the last update, until we find no more objects.
        List<AcmAssignedObject> updatedObjects;
        do
        {
            updatedObjects = locator.getObjectsModifiedSince(lastUpdate, current, batchSize);
            if ( debug )
            {
                log.debug("Number of objects for " + locator.getClass().getName() + ": " + updatedObjects.size());
            }

            if ( !updatedObjects.isEmpty() )
            {
                current += batchSize;
                getDataAccessBatchUpdater().updateDataAccessPolicy(updatedObjects, locator);
            }
        }
        while ( !updatedObjects.isEmpty() );

    }


    public boolean isBatchUpdateBasedOnLastModifiedEnabled()
    {
        return batchUpdateBasedOnLastModifiedEnabled;
    }

    public void setBatchUpdateBasedOnLastModifiedEnabled(boolean batchUpdateBasedOnLastModifiedEnabled)
    {
        this.batchUpdateBasedOnLastModifiedEnabled = batchUpdateBasedOnLastModifiedEnabled;
    }

    public String getLastBatchUpdatePropertyFileLocation()
    {
        return lastBatchUpdatePropertyFileLocation;
    }

    public void setLastBatchUpdatePropertyFileLocation(String lastBatchUpdatePropertyFileLocation)
    {
        this.lastBatchUpdatePropertyFileLocation = lastBatchUpdatePropertyFileLocation;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public void setBatchSize(int batchSize)
    {
        this.batchSize = batchSize;
    }

    public int getBatchSize()
    {
        return batchSize;
    }


    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public AcmDataAccessBatchUpdater getDataAccessBatchUpdater()
    {
        return dataAccessBatchUpdater;
    }

    public void setDataAccessBatchUpdater(AcmDataAccessBatchUpdater dataAccessBatchUpdater)
    {
        this.dataAccessBatchUpdater = dataAccessBatchUpdater;
    }
}
