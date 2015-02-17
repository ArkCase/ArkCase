package com.armedia.acm.services.subscription.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.subscription.dao.SubscriptionDao;
import com.armedia.acm.services.subscription.dao.SubscriptionEventDao;
import com.armedia.acm.services.subscription.model.AcmSubscriptionEvent;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.xmlbeans.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 29.01.2015.
 */
public class SubscriptionEventBatchInsertService {

    private SubscriptionDao subscriptionDao;
    private SubscriptionEventDao subscriptionEventDao;
    private PropertyFileManager propertyFileManager;
    private String lastBatchInsertPropertyFileLocation;
    private SubscriptionEventPublisher subscriptionEventPublisher;
    private String userHomeDir;
    private String fileSeparator = SystemProperties.getProperty("file.separator");
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private String fullPath;
    private SpringContextHolder springContextHolder;

    /**
     * The default run date to use if this generator has never run before (or if the properties file that stores the
     * last run date is missing)
     */
    private static final String DEFAULT_LAST_RUN_DATE = "1970-01-01T00:00:00Z";

    /**
     * The property key to use in the properties file that stores the last run date.
     */
    private static final String SUBSCRIPTION_EVENT_LAST_RUN_DATE_PROPERTY_KEY = "subscription.event.last.run.date";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private Logger log = LoggerFactory.getLogger(getClass());

    // this method is used by scheduled jobs in Spring beans loaded dynamically from the ACM configuration
    // folder ($HOME/.acm).
    public void insertNewSubscriptionEvents() {
        setFullPath(getUserHomeDir() + getLastBatchInsertPropertyFileLocation().replace("/",getFileSeparator()));
        getAuditPropertyEntityAdapter().setUserId("SUBSCRIPTION-BATCH-INSERT");

        String lastRunDate = getPropertyFileManager().load(
                getFullPath(),
                SUBSCRIPTION_EVENT_LAST_RUN_DATE_PROPERTY_KEY,
                DEFAULT_LAST_RUN_DATE);
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        try {
            Date lastBatchRunDate = getLastBatchRunDate(lastRunDate, dateFormat);
            storeCurrentDateForNextBatchRun(dateFormat);
            List<AcmSubscriptionEvent> subscriptionEventList = null;
        try {
                subscriptionEventList = getSubscriptionDao().createListOfNewSubscriptionEventsForInserting(lastBatchRunDate);
                for( AcmSubscriptionEvent subscriptionEvent: subscriptionEventList ){
                    AcmSubscriptionEvent subscriptionEventSaved = getSubscriptionEventDao().save(subscriptionEvent);
                    subscriptionEventPublisher.publishAcmSubscriptionEventCreatedEvent(subscriptionEventSaved,true);
                }
        } catch ( AcmObjectNotFoundException e ) {
            if ( log.isInfoEnabled() )
                log.info("There are no new events to be added",e);
        }
        } catch ( ParseException e ) {
            if ( log.isErrorEnabled() )
                log.error("Parsing exception occurred while fetching lastBatchRunDate ",e);
        }
    }

    private void storeCurrentDateForNextBatchRun(DateFormat dateFormat)
    {
        // store the current time as the last run date to use the next time this job runs.  This allows us to
        // scan only for objects updated since this date.
        String solrNow = dateFormat.format(new Date());
        getPropertyFileManager().store(SUBSCRIPTION_EVENT_LAST_RUN_DATE_PROPERTY_KEY, solrNow, getFullPath());
    }

    private Date getLastBatchRunDate(String lastRunDate, DateFormat dateFormat) throws ParseException
    {
        Date sinceWhen = dateFormat.parse(lastRunDate);

        // back up one minute just to be sure we get everything
        Calendar cal = Calendar.getInstance();
        cal.setTime(sinceWhen);
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - 1);
        sinceWhen = cal.getTime();
        return sinceWhen;
    }

    public String getLastBatchInsertPropertyFileLocation() {
        return lastBatchInsertPropertyFileLocation;
    }

    public void setLastBatchInsertPropertyFileLocation(String lastBatchInsertPropertyFileLocation) {
        this.lastBatchInsertPropertyFileLocation = lastBatchInsertPropertyFileLocation;
    }

    public PropertyFileManager getPropertyFileManager() {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager) {
        this.propertyFileManager = propertyFileManager;
    }

    public String getUserHomeDir() {
           return userHomeDir;
    }

    public void setUserHomeDir(String userHomeDir) {
        this.userHomeDir = userHomeDir;
    }

    public String getFileSeparator() {
        return fileSeparator;
    }

    public void setFileSeparator(String fileSeparator) {
        this.fileSeparator = fileSeparator;
    }

    public SubscriptionEventDao getSubscriptionEventDao() {
        return subscriptionEventDao;
    }

    public void setSubscriptionEventDao(SubscriptionEventDao subscriptionEventDao) {
        this.subscriptionEventDao = subscriptionEventDao;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter() {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter) {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public SubscriptionDao getSubscriptionDao() {
        return subscriptionDao;
    }

    public void setSubscriptionDao(SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
    }

    public SubscriptionEventPublisher getSubscriptionEventPublisher() {
        return subscriptionEventPublisher;
    }

    public void setSubscriptionEventPublisher(SubscriptionEventPublisher subscriptionEventPublisher) {
        this.subscriptionEventPublisher = subscriptionEventPublisher;
    }

    public SpringContextHolder getSpringContextHolder() {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder) {
        this.springContextHolder = springContextHolder;
    }
}
