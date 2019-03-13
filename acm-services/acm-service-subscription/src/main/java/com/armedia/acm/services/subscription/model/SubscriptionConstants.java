package com.armedia.acm.services.subscription.model;

/*-
 * #%L
 * ACM Service: Subscription
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

public interface SubscriptionConstants
{
    String OBJECT_TYPE = "SUBSCRIPTION";
    String OBJECT_TYPE_EVENT = "SUBSCRIPTION_EVENT";

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

}
