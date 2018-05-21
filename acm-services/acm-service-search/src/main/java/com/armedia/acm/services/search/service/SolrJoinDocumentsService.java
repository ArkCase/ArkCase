package com.armedia.acm.services.search.service;

/*-
 * #%L
 * ACM Service: Search
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;

import org.springframework.security.core.Authentication;

public interface SolrJoinDocumentsService
{
    /**
     * Executes solr join query and combines results
     *
     * @param auth
     *            Authentication
     * @param ownerId
     *            Id of the owning object
     * @param ownerIdFieldName
     *            name of the field for Id of the owning object
     * @param ownerType
     *            Object Type of the owning object
     * @param ownerTypeFieldName
     *            name of the field for Object Type of the owning object
     * @param referenceType
     *            Object Type of the reference holder object
     * @param targetType
     *            Object Type of the target object
     * @param targetTypeFieldName
     *            name of the field for Object Type of the target object
     * @param storeTargetObjectFieldName
     *            field name where target object should be stored to be part of the response
     * @param joinFromField
     *            name of the field which holds reference in the reference document
     * @param joinToField
     *            name of the field which field with reference in the reference document points to document
     * @param start
     *            from which row to start
     * @param limit
     *            how many rows should return
     * @param sort
     *            sort by field
     * @return String SolrResponse
     * @throws AcmObjectNotFoundException
     */
    String getJoinedDocuments(Authentication auth, Long ownerId, String ownerIdFieldName,
            String ownerType, String ownerTypeFieldName,
            String referenceType,
            String targetType, String targetTypeFieldName,
            String storeTargetObjectFieldName,
            String joinFromField, String joinToField, int start, int limit, String sort) throws AcmObjectNotFoundException;
}
