package com.armedia.acm.services.signature.dao;

/*-
 * #%L
 * ACM Service: Electronic Signature
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
import com.armedia.acm.services.signature.model.Signature;
import com.google.common.base.Preconditions;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class SignatureDao extends AcmAbstractDao<Signature>
{
    private final Logger log = LogManager.getLogger(getClass());
    @PersistenceContext
    private EntityManager entityManager;
    private String lookupByObjectIdObjectType = "SELECT d " +
            "FROM Signature d " +
            "WHERE d.objectId = :objectId AND " +
            "d.objectType  = :objectType";

    public List<Signature> findByObjectIdObjectType(Long objectId, String objectType)
    {
        Preconditions.checkNotNull(objectId, "Object Id cannot be null");
        Preconditions.checkNotNull(objectType, "Object type cannot be null");

        TypedQuery<Signature> lookupQuery = getEntityManager().createQuery(lookupByObjectIdObjectType, Signature.class);
        lookupQuery.setParameter("objectId", objectId);
        lookupQuery.setParameter("objectType", objectType);

        List<Signature> results = lookupQuery.getResultList();
        if (null == results)
        {
            results = new ArrayList();
        }
        return results;
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    @Override
    protected Class<Signature> getPersistenceClass()
    {
        return Signature.class;
    }
}
