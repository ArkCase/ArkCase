package com.armedia.acm.camelcontext.basic.auth;

/*-
 * #%L
 * acm-camel-context-manager
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;

public class HttpInvokerUtil
{
    public static final String EXTERNAL_AUTH_KEY = "X-Alfresco-Remote-User";
    private static final Logger log = LogManager.getLogger(HttpInvokerUtil.class);
    public static String EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY = "MDC_ALFRESCO_USER_ID";

    public static final String getExternalUserIdValue()
    {
        String userId = MDC.get(EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY);
        if ((userId == null))
        {
            // should not happen
            log.error("X-Alfresco-Remote-User is null!");
            return null;
        }
        return userId;
    }

}
