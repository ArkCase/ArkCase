package com.armedia.acm.plugins.objectassociation.dao;

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

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;

import java.util.List;

/**
 * Created by armdev on 11/6/14.
 */
public class ObjectAssociationDao extends AcmAbstractDao<ObjectAssociation>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    @Override
    protected Class<ObjectAssociation> getPersistenceClass()
    {
        return ObjectAssociation.class;
    }

    @Transactional
    public List<ObjectAssociation> findByParentTypeAndId(String parentType, Long parentId)
    {
        TypedQuery<ObjectAssociation> findByParentTypeAndId = getEm().createQuery(
                "SELECT e " +
                        "FROM ObjectAssociation e " +
                        "WHERE e.parentType = :parentType " +
                        "AND e.parentId = :parentId " +
                        "ORDER BY e.targetName",
                ObjectAssociation.class);

        findByParentTypeAndId.setParameter("parentId", parentId);
        findByParentTypeAndId.setParameter("parentType", parentType);

        List<ObjectAssociation> retval = findByParentTypeAndId.getResultList();

        return retval;

    }

    public ObjectAssociation findChildOfType(String parentType, Long parentId, String targetSubtype)
    {
        TypedQuery<ObjectAssociation> selectQuery = getEm().createQuery(
                "SELECT e " +
                        "FROM ObjectAssociation e " +
                        "WHERE e.parentType = :parentType " +
                        "AND e.parentId = :parentId " +
                        "AND e.targetType = :targetType " +
                        "AND e.category = :targetCategory " +
                        "AND e.targetSubtype = :targetSubtype " +
                        "ORDER BY e.targetName",
                ObjectAssociation.class);

        selectQuery.setParameter("parentId", parentId);
        selectQuery.setParameter("parentType", parentType);
        selectQuery.setParameter("targetType", "FILE");
        selectQuery.setParameter("targetCategory", "DOCUMENT");
        selectQuery.setParameter("targetSubtype", targetSubtype);

        ObjectAssociation retval = null;

        try
        {
            retval = selectQuery.getSingleResult();
        }
        catch (Exception e)
        {
            LOG.error("Cannot find Object Association for parentId=[{}], parentType=[{}] and targetSubtype=[{}].", parentId, parentType,
                    targetSubtype, e);
        }

        return retval;

    }

    @Transactional
    public void delete(Long id)
    {
        ObjectAssociation objectAssociation = getEm().find(getPersistenceClass(), id);
        getEm().remove(objectAssociation);
    }
}
