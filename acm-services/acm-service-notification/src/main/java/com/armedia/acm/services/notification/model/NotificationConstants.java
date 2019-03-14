/**
 *
 */
package com.armedia.acm.services.notification.model;

/*-
 * #%L
 * ACM Service: Notification
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.objectonverter.DateFormats;

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
    String DATE_FORMAT = DateFormats.DEFAULT_DATE_FORMAT;

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

    String PARTICIPANT_TYPE_GROUP = "owning group";
    String SPECIAL_PARTICIPANT_TYPE = "*";

}
