/**
 * 
 */
package com.armedia.acm.services.notification.model;

/**
 * @author riste.tutureski
 *
 */
public interface NotificationConstants {

	/**
	 * Notification object type
	 */
	public static final String OBJECT_TYPE = "NOTIFICATION";
	
	/**
	 * User who trigger scheduled task (this should be system user)
	 */
	public static final String SYSTEM_USER = "NOTIFICATION-BATCH-INSERT";
	
	/**
     * The default run date to use if this generator has never run before (or if the properties file that stores the
     * last run date is missing)
     */
    public static final String DEFAULT_LAST_RUN_DATE = "1970-01-01T00:00:00Z";
    
    /**
     * The property key to use in the properties file that stores the last run date.
     */
    public static final String SOLR_LAST_RUN_DATE_PROPERTY_KEY = "notification.user.last.run.date";
    
    /**
     * Date format that is needed for converting string to date 
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    /**
     * Notification message state when the message is successfully sent to the user
     */
    public static final String STATE_SENT = "SENT";
    
    /**
     * Notification message state when the message is not successfully sent to the user after numbers of retries (if any)
     */
    public static final String STATE_NOT_SENT = "NOT_SENT";	
    
    /**
     * The property key to use in the properties file that keeps email host address
     */
    public static final String EMAIL_HOST_KEY = "notification.user.email.host";
    
    /**
     * The property key to use in the properties file that keeps email port
     */
    public static final String EMAIL_PORT_KEY = "notification.user.email.port";
    
    /**
     * The property key to use in the properties file that keeps email user
     */
    public static final String EMAIL_USER_KEY = "notification.user.email.user";
    
    /**
     * The property key to use in the properties file that keeps email password
     */
    public static final String EMAIL_PASSWORD_KEY = "notification.user.email.password";
    
    /**
     * The property key to use in the properties file that keeps email from
     */
    public static final String EMAIL_FROM_KEY = "notification.user.email.from";
    
    /**
     * The property key to use in the properties file that keeps email response timeout
     */
    public static final String EMAIL_RESPONSE_TIMEOUT_KEY = "notification.user.email.responseTimeout";
    
    /**
     * Notification message status new
     */
    public static final String STATUS_NEW = "New";
    
    /**
     * Notification message action empty (default one)
     */
    public static final String ACTION_DEFAULT = "";
}
