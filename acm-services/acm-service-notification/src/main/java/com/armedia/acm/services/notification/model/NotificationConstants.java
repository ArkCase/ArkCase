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
	String OBJECT_TYPE = "NOTIFICATION";
	
	/**
	 * User who trigger scheduled task (this should be system user)
	 */
	String SYSTEM_USER = "NOTIFICATION-BATCH-INSERT";
	
	/**
     * The default run date to use if this generator has never run before (or if the properties file that stores the
     * last run date is missing)
     */
    String DEFAULT_LAST_RUN_DATE = "1970-01-01T00:00:00Z";
    
    /**
     * The property key to use in the properties file that stores the last run date.
     */
    String SOLR_LAST_RUN_DATE_PROPERTY_KEY = "notification.user.last.run.date";
    
    /**
     * Date format that is needed for converting string to date 
     */
    String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    /**
     * Notification message state when the message is successfully sent to the user
     */
    String STATE_SENT = "SENT";
    
    /**
     * Notification message state when the message is not successfully sent to the user after numbers of retries (if any)
     */
    String STATE_NOT_SENT = "NOT_SENT";	
    
    /**
     * Notification message state when popup is shown on the screen that shows the notification 
     */
    String STATE_READ = "READ";
    
    /**
     * The property key to use in the properties file that keeps email host address
     */
    String EMAIL_HOST_KEY = "notification.user.email.host";
    
    /**
     * The property key to use in the properties file that keeps email port
     */
    String EMAIL_PORT_KEY = "notification.user.email.port";
    
    /**
     * The property key to use in the properties file that keeps email user
     */
    String EMAIL_USER_KEY = "notification.user.email.user";
    
    /**
     * The property key to use in the properties file that keeps email password
     */
    String EMAIL_PASSWORD_KEY = "notification.user.email.password";
    
    /**
     * The property key to use in the properties file that keeps email from
     */
    String EMAIL_FROM_KEY = "notification.user.email.from";
    
    /**
     * The property key to use in the properties file that keeps email response timeout
     */
    String EMAIL_RESPONSE_TIMEOUT_KEY = "notification.user.email.responseTimeout";
    
    /**
     * Notification message statuses
     */
    String STATUS_NEW = "New";
    String STATUS_DELETE = "DELETE";
    
    /**
     * Notification message action empty (default one)
     */
    String ACTION_DEFAULT = "";
    
    String TYPE_POPUP = "popup";

    /*
     * When this string appears in a notification title or note to be e-mailed, it will be replaced with the
     * appropriate label from notification.properties.
     */
    String OBJECT_TYPE_LABEL_PLACEHOLDER = "${objectTypeLabel}";

    /**
     * When this string appears in a notification note to be e-mailed, it will be replaced with a URL to the
     * object in the notification.
     */
    String ANCHOR_PLACEHOLDER = "${urlanchor}";
    
}
