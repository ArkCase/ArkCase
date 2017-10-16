/**
 *
 */
package com.armedia.acm.services.notification.model;

/**
 * @author riste.tutureski
 */
public interface NotificationConstants
{

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
     * Notification message state when the message is not successfully sent to the user after numbers of retries (if
     * any)
     */
    String STATE_NOT_SENT = "NOT_SENT";

    /**
     * Notification message state when popup is shown on the screen that shows the notification
     */
    String STATE_READ = "READ";

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
     * When those strings appear in a notification title or note to be e-mailed, it will be replaced with the
     * appropriate label from notification.properties.
     */
    String OBJECT_TYPE_LABEL_PLACEHOLDER = "${objectTypeLabel}";
    String PARENT_TYPE_LABEL_PLACEHOLDER = "${parentTypeLabel}";

    /**
     * When this string appears in a notification note to be e-mailed, it will be replaced with a URL to the object in
     * the notification.
     */
    String ANCHOR_PLACEHOLDER = "${urlanchor}";

    /**
     * When this string appears in notification's title it will be replaced with the objectName of COMPLAINT, CASE_FILE
     * or TASK appropriate
     */
    String NAME_LABEL = "${nameLabel}";

    String LAST_BATCH_RUN_PROPERTY_FILE = System.getProperty("user.home") + "/.arkcase/acm/notificationLastBatchUpdate.properties";

    String BASE_URL_KEY = "arkcase.url.base";

    String PARTICIPANT_TYPE_GROUP = "owning group";
    String SPECIAL_PARTICIPANT_TYPE = "*";

}
