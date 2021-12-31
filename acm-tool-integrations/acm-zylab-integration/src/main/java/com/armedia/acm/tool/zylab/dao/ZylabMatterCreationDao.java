package com.armedia.acm.tool.zylab.dao;

/*-
 * #%L
 * Tool Integrations: Arkcase ZyLAB Integration
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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
import com.armedia.acm.tool.zylab.model.ZylabMatterCreationStatus;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import java.util.Optional;

public class ZylabMatterCreationDao extends AcmAbstractDao<ZylabMatterCreationStatus>
{

    @Override
    protected Class<ZylabMatterCreationStatus> getPersistenceClass()
    {
        return ZylabMatterCreationStatus.class;
    }

    public Optional<ZylabMatterCreationStatus> findByMatterName(String matterName)
    {
        String jpql = "SELECT e FROM ZylabMatterCreationStatus e WHERE e.matterName=:matterName";

        TypedQuery<ZylabMatterCreationStatus> query = getEm().createQuery(jpql, getPersistenceClass());
        query.setParameter("matterName", matterName);

        try
        {
            return Optional.of(query.getSingleResult());
        }
        catch (NoResultException e)
        {
            return Optional.empty();
        }
    }
}
