package com.armedia.acm.web.api;

/*-
 * #%L
 * ACM Shared Web Artifacts
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

/**
 * Created by Bojan Milenkoski on 28.1.2016.
 */
public interface MDCConstants
{
    String EVENT_MDC_REQUEST_ID_KEY = "MDC_REQUEST_ID";
    String EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY = "MDC_REMOTE_ADDRESS";
    String EVENT_MDC_REQUEST_USER_ID_KEY = "MDC_USER_ID";
    String EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY = "MDC_ALFRESCO_USER_ID";
    String EVENT_MDC_REQUEST_PENTAHO_USER_ID_KEY = "MDC_PENTAHO_USER_ID";
    String ANONYMOUS_USER = "anonymous";
}
