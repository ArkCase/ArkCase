package com.armedia.acm.plugins.ecm.dao;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.RecycleBinConstants;
import com.armedia.acm.plugins.ecm.model.RecycleBinItem;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * @author darko.dimitrievski
 */

public class RecycleBinItemDao extends AcmAbstractDao<RecycleBinItem>
{
    @Override
    protected Class<RecycleBinItem> getPersistenceClass()
    {
        return RecycleBinItem.class;
    }


    @Transactional
    public RecycleBinItem removeItemFromRecycleBin(Long id)
    {
        RecycleBinItem recycleBinItem = getEm().find(getPersistenceClass(), id);
        getEm().remove(recycleBinItem);
        return recycleBinItem;
    }

    public AcmContainer getContainerForRecycleBin(String objectType, String cmisRepositoryId)
    {
        try
        {
            String jpql = "SELECT e FROM AcmContainer e WHERE e.containerObjectType =:objectType AND e.cmisRepositoryId =:cmisRepositoryId";

            TypedQuery<AcmContainer> query = getEm().createQuery(jpql, AcmContainer.class);

            query.setParameter("objectType", objectType);
            query.setParameter("cmisRepositoryId", cmisRepositoryId);
            return query.getSingleResult();
        }
        catch(NoResultException e)
        {
            return null;
        }
    }

    public String getSupportedObjectType()
    {
        return RecycleBinConstants.OBJECT_TYPE;
    }
}
