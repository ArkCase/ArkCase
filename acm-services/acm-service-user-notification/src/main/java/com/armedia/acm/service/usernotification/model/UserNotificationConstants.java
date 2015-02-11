/**
 * 
 */
package com.armedia.acm.service.usernotification.model;

/**
 * @author riste.tutureski
 *
 */
public interface UserNotificationConstants {

	/**
     * The default run date to use if this generator has never run before (or if the properties file that stores the
     * last run date is missing)
     */
    public static final String DEFAULT_LAST_RUN_DATE = "1970-01-01T00:00:00Z";
    
    /**
     * The property key to use in the properties file that stores the last run date.
     */
    public static final String SOLR_LAST_RUN_DATE_PROPERTY_KEY = "user.notification.last.run.date";
    
    /**
     * Date format that is needed for converting string to date 
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
}
