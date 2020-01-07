package com.armedia.mule.cmis.basic.auth;

/*-
 * #%L
 * ACM Mule CMIS Connector
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.MDC;

/**
 * Helper class for HttpInvoker classes providing the external user authentication values.
 * <p>
 * Created by bojan.milenkoski on 23.11.2016
 */
public class HttpInvokerUtil
{
    public static final String EXTERNAL_AUTH_KEY = "X-Alfresco-Remote-User";
    private static final Logger log = LogManager.getLogger(HttpInvokerUtil.class);
    private static final String ANONYMOUS_USER = "anonymous";

    private static final String EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY = "MDC_ALFRESCO_USER_ID";

    /**
     * Returns the userId set in the thread local variable of {@link MDC} class. If the userId is 'anonymous' this
     * method returns null.
     * 
     * @return the userId or null
     */
    public static final String getExternalUserIdValue()
    {
        String userId = MDC.get(EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY);
        if ((userId == null) || ANONYMOUS_USER.equals(userId))
        {
            // should not happen
            log.error("X-Alfresco-Remote-User is null!");
            return null;
        }
        return userId;
    }
}
