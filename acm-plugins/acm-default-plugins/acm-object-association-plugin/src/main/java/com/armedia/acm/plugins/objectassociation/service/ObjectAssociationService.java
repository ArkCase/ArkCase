package com.armedia.acm.plugins.objectassociation.service;

/*-
 * #%L
 * ACM Default Plugin: Object Associations
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
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.objectassociation.model.AcmChildObjectEntity;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ObjectAssociationService
{
    void addReference(Long id, String number, String type, String title, String status, Long parentId, String parentType) throws Exception;

    @Transactional
    void delete(Long id);

    AcmAbstractDao<AcmChildObjectEntity> getDaoForChildObjectEntity(String objectType);

    ObjectAssociation saveObjectAssociation(ObjectAssociation oa);

    List<ObjectAssociation> findByParentTypeAndId(String type, Long id);

    /**
     * List Associations for given object id and type. Results are combined with target documents.
     *
     * @param auth
     *            Authentication
     * @param parentId
     *            id of the owner of associations
     * @param parentType
     *            type of the owner of associations
     * @param targetType
     *            type of the target of associations
     * @param orderBy
     *            name of the field to order by
     * @param start
     *            which row to start
     * @param limit
     *            number of rows to retrieve
     * @return solr response
     */
    String getAssociations(Authentication auth, Long parentId, String parentType, String targetType, String orderBy, int start, int limit)
            throws AcmObjectNotFoundException;

    /**
     * saves object association
     *
     * @param objectAssociation
     *            Object association
     * @param auth
     *            Authentication
     * @return saved association
     */
    ObjectAssociation saveAssociation(ObjectAssociation objectAssociation, Authentication auth) throws AcmObjectAssociationException;

    /**
     * Removes object association
     *
     * @param id
     *            id of the association
     * @param auth
     *            Authentication
     */
    void deleteAssociation(Long id, Authentication auth);

    /**
     * return object association for given id
     *
     * @param id
     *            id of the association
     * @param auth
     *            Authentication
     * @return object association
     */
    ObjectAssociation getAssociation(Long id, Authentication auth);
}
