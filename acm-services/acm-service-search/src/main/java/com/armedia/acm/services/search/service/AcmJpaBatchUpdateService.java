package com.armedia.acm.services.search.service;

import com.armedia.acm.data.AcmObjectChangelist;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
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
public class AcmJpaBatchUpdateService
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
    private static final String SOLR_LAST_RUN_DATE_PROPERTY_KEY = "solr.last.run.date";



    private boolean batchUpdateBasedOnLastModifiedEnabled;
    private String lastBatchUpdatePropertyFileLocation;
    private PropertyFileManager propertyFileManager;
    private SpringContextHolder springContextHolder;
    private JpaObjectsToSearchService objectsToSearchService;
    private int batchSize;

    public void jpaBatchUpdate()
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("JPA batch update enabled: " + isBatchUpdateBasedOnLastModifiedEnabled());
        }

        if ( !isBatchUpdateBasedOnLastModifiedEnabled() )
        {
            return;
        }

        String lastRunDate = getPropertyFileManager().load(
                getLastBatchUpdatePropertyFileLocation(),
                SOLR_LAST_RUN_DATE_PROPERTY_KEY,
                DEFAULT_LAST_RUN_DATE);
        DateFormat solrDateFormat = new SimpleDateFormat(SearchConstants.SOLR_DATE_FORMAT);

        try
        {
            Date lastBatchRunDate = getLastBatchRunDate(lastRunDate, solrDateFormat);
            storeCurrentDateForNextBatchRun(solrDateFormat);

            if ( log.isDebugEnabled() )
            {
                log.debug("Checking for objects modified since: " + lastBatchRunDate);
            }

            Collection<? extends AcmObjectToSolrDocTransformer> transformers =
                    getSpringContextHolder().getAllBeansOfType(AcmObjectToSolrDocTransformer.class).values();
            if ( log.isDebugEnabled() )
            {
                log.debug(transformers.size() + " object transformers found.");
            }

            for (AcmObjectToSolrDocTransformer transformer : transformers)
            {
            	try
            	{
            		sendUpdatedObjectsToSolr(lastBatchRunDate, transformer);
            	}
            	catch(Exception exception)
            	{
            		log.error("Could not send index updates to SOLR for transformer " + transformer.getClass(), exception);
            	}
            }
        }
        catch (ParseException e)
        {
            log.error("Could not send index updates to SOLR: " + e.getMessage(), e);
        }
    }

    private void storeCurrentDateForNextBatchRun(DateFormat solrDateFormat)
    {
        // store the current time as the last run date to use the next time this job runs.  This allows us to
        // scan only for objects updated since this date.
        String solrNow = solrDateFormat.format(new Date());
        getPropertyFileManager().store(SOLR_LAST_RUN_DATE_PROPERTY_KEY, solrNow, getLastBatchUpdatePropertyFileLocation());
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

    private void sendUpdatedObjectsToSolr(Date lastUpdate, AcmObjectToSolrDocTransformer transformer)
    {
        boolean debug = log.isDebugEnabled();

        if ( debug )
        {
            log.debug("Handling transformer type: " + transformer.getClass().getName());
        }

        int current = 0;
        int batchSize = getBatchSize();

        // keep retrieving another batch of objects modified since the last update, until we find no more objects.
        List<Object> updatedObjects;
        do
        {
            updatedObjects = transformer.getObjectsModifiedSince(lastUpdate, current, batchSize);
            if ( debug )
            {
                log.debug("Number of objects: " + updatedObjects.size());
            }

            if ( !updatedObjects.isEmpty() )
            {
                current += batchSize;

                AcmObjectChangelist changelist = new AcmObjectChangelist();
                changelist.setUpdatedObjects(updatedObjects);
                getObjectsToSearchService().updateObjectsInSolr(changelist);
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

    public JpaObjectsToSearchService getObjectsToSearchService()
    {
        return objectsToSearchService;
    }

    public void setObjectsToSearchService(JpaObjectsToSearchService objectsToSearchService)
    {
        this.objectsToSearchService = objectsToSearchService;
    }
}
