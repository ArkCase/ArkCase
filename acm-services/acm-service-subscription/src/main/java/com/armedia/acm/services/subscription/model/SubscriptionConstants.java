package com.armedia.acm.services.subscription.model;

import com.armedia.acm.objectonverter.DateFormats;

public interface SubscriptionConstants
{
    String OBJECT_TYPE = "SUBSCRIPTION";
    String OBJECT_TYPE_EVENT = "SUBSCRIPTION_EVENT";

    String SUCCESS_MSG = "subscription.removed.successful";
    String SUBSCRIPTION_NOT_FOUND_MSG = "subscription.not.found";
    String AUDIT_ACTIVITY_RESULT_SUCCESS = "success";
    String SUBSCRIPTION_USER = "SUBSCRIPTION-BATCH-INSERT";
    int NO_ROW_DELETED = 0;

    /**
     * The default run date to use if this generator has never run before (or if the properties file that stores the
     * last run date is missing)
     */
    String DEFAULT_LAST_RUN_DATE = "1970-01-01T00:00:00Z";

    /**
     * The property key to use in the properties file that stores the last run date.
     */
    String SUBSCRIPTION_EVENT_LAST_RUN_DATE_PROPERTY_KEY = "subscription.event.last.run.date";
    String DATE_FORMAT = DateFormats.DEFAULT_DATE_FORMAT;

    /**
     * The property key to get all events that should not be saved as AcmSubscriptionEvent
     */
    String SUBSCRIPTION_EVENT_TYPES_TO_BE_REMOVED = "subscription.removed.event.types";
}
