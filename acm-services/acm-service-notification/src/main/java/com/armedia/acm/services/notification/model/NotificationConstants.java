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
     * Notification message state when template is failed.
     */
    String STATE_TEMPLATE_ERROR = "TEMPLATE_ERROR";

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

    /**
     * When this string appears in notification's title it will be replaced with the objectType: COMPLAINT, CASE_FILE
     * or TASK appropriate
     */
    String TYPE_LABEL = "${typeLabel}";

    /**
     * When this string appears in notification's title it will be replaced with the userId of referred user
     * in the notification
     */
    String USER_LABEL = "${userLabel}";

    /**
     * When this string appears in notification's title it will be replaced with the id of COMPLAINT, CASE_FILE
     * or TASK appropriate
     */
    String ID_LABEL = "${idLabel}";

    String PARTICIPANT_TYPE_GROUP = "owning group";
    String SPECIAL_PARTICIPANT_TYPE = "*";

    /**
     * This constants are for setting the titles of notifications, defined in notifications- yaml file
     */
    String NOTIFICATION_TASK_COMPLETED = "notifications.task.completed";
    String NOTIFICATION_FOIA_EXTENSION = "notifications.foia.extension.notification";
    String NOTIFICATION_PRIVACY_EXTENSION = "notifications.privacy.extension.notification";
    String PASSWORD_RESET = "notifications.password.reset";
    String FORGOT_USERNAME = "notifications.username.forgot";
    String STATUS_TRANSCRIPTION = "notifications.status.transcription";
    String COMPLAINT_INVOICE = "notifications.complaint.invoice";
    String TASK_UPCOMING = "notifications.task.upcoming";
    String TASK_OVERDUE = "notifications.task.overdue";
    String TASK_STATUS_CHANGED = "notifications.task.status.changed";
    String TASK_PRIORITY_CHANGED = "notifications.task.priority.changed";
    String TASK_CONCUR_NONCONCUR = "notifications.task.concur.nonconcur";
    String TASK_CONCUR_NOTE = "notifications.task.concur.note";
    String TASK_NON_CONCUR_NOTE = "notifications.task.nonconcur.note";
    String COMPLAINT_STATUS_CHANGED = "notifications.complaint.status.changed";
    String COMPLAINT_PRIORITY_CHANGED = "notifications.complaint.priority.changed";
    String CASE_STATUS_CHANGED = "notifications.case.status.changed";
    String CASE_PRIORITY_CHANGED = "notifications.case.priority.changed";
    String CONSULTATION_STATUS_CHANGED = "notifications.consultation.status.changed";
    String CONSULTATION_PRIORITY_CHANGED = "notifications.consultation.priority.changed";
    String PARTICIPANTS_ADDED = "notifications.participants.added";
    String PARTICIPANTS_DELETED = "notifications.participants.deleted";
    String PARTICIPANTS_USER_ADDED = "notifications.participants.user.added";
    String OBJECT_UNASSIGNED = "notifications.object.unassigned";
    String OBJECT_ASSIGNED = "notifications.object.assigned";
    String SINGLE_TASK_WORKFLOW = "notifications.acmdocumentsingletask.workflow";
    String EMAIL_MENTIONS = "notifications.email.mentions";
    String REQUEST_ASSIGNED = "notifications.request.assigned";
    String REQUEST_DOWNLOADED = "notifications.request.downloaded";
    String REQUEST_RELEASED = "notifications.request.released";
    String PORTAL_REGISTRATION = "notifications.portal.registration";
    String PASSWORD_RESET_REQUEST = "notifications.password.request";
    String NEW_PORTAL_USER_PASSWORD_RESET_REQUEST = "notifications.password.newPortalUser";
    String CALENDAR_EVENT_ADDED = "notifications.calendar.event.added";
    String NOTE_ADDED = "notifications.note.added";
    String STATUS_COMPREHEND_MEDICAL = "notifications.status.comprehendMedical";

    /**
     * Notification type AUTOMATED
     */
    String TYPE_AUTOMATED = "AUTOMATED";
    /**
     * Notification type MANUAL
     */
    String TYPE_MANUAL = "MANUAL";
    /**
     * Notification message state when the message is not sent to the user because of manually disabled 
     * sending messages with current template
     */
    String STATE_DISABLED = "DISABLED";


    /**
     * Email Notification Template Type
     */
    String EMAIL_NOTIFICATION_TEMPLATE_TYPE = "emailTemplate";
}
