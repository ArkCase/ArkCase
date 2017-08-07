package com.armedia.acm.services.search.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import org.springframework.security.core.Authentication;

public interface SolrJoinDocumentsService
{
    /**
     * Executes solr join query and combines results
     *
     * @param ownerId                    Id of the owning object
     * @param ownerIdFieldName           name of the field for Id of the owning object
     * @param ownerType                  Object Type of the owning object
     * @param ownerTypeFieldName         name of the field for Object Type of the owning object
     * @param referenceType              Object Type of the reference holder object
     * @param targetType                 Object Type of the target object
     * @param targetTypeFieldName        name of the field for Object Type of the target object
     * @param storeTargetObjectFieldName field name where target object should be stored to be part of the response
     * @param start                      from which row to start
     * @param limit                      how many rows should return
     * @param sort                       sort by field
     * @param auth                       Authentication
     * @param joinFromField              name of the field which holds reference in the reference document
     * @param joinToField                name of the field which field with reference in the reference document points to document
     * @return String SolrResponse
     * @throws AcmObjectNotFoundException
     */
    String getJoinedDocuments(Long ownerId, String ownerIdFieldName,
                              String ownerType, String ownerTypeFieldName,
                              String referenceType,
                              String targetType, String targetTypeFieldName,
                              String storeTargetObjectFieldName,
                              int start, int limit, String sort,
                              Authentication auth,
                              String joinFromField, String joinToField) throws AcmObjectNotFoundException;
}
