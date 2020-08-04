/**
 *
 */
package com.armedia.acm.audit.model;

/*-
 * #%L
 * ACM Service: Audit Library
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
 * @author riste.tutureski
 */
public interface AuditConstants
{
    String USER_ID_ANONYMOUS = "anonymous";

    String EVENT_STATUS_COMPLETE = "COMPLETE";

    String EVENT_RESULT_SUCCESS = "success";
    String EVENT_RESULT_FAILURE = "failure";

    String EVENT_STATUS_DB_OBJECT_ADDED = "DB_OBJECT_ADDED";
    String EVENT_STATUS_DB_OBJECT_DELETED = "DB_OBJECT_DELETED";
    String EVENT_STATUS_DB_OBJECT_UPDATED = "DB_OBJECT_UPDATED";

    String EVENT_STATUS_ACTIVITI_ENTITY_CREATED = "ACTIVITI_ENTITY_CREATED";
    String EVENT_STATUS_ACTIVITI_ENTITY_DELETED = "ACTIVITI_ENTITY_DELETED";
    String EVENT_STATUS_ACTIVITI_ENTITY_UPDATED = "ACTIVITI_ENTITY_UPDATED";

    String EVENT_OBJECT_TYPE_ACTIVITI_ENTITY = "ACTIVITI_ENTITY";
    String EVENT_OBJECT_TYPE_WEB_REQUEST = "WEB REQUEST";
    String EVENT_OBJECT_TYPE_DATABASE = "DATABASE";
    String EVENT_OBJECT_TYPE_ACTIVITI_EVENT = "ACTIVITI EVENT";
}
