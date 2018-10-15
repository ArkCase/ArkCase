package com.armedia.acm.service.objectlock.dao;

/*-
 * #%L
 * ACM Service: Object lock
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
import com.armedia.acm.service.objectlock.model.AcmObjectLock;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * Created by nebojsha on 25.08.2015.
 */
public class AcmObjectLockDao extends AcmAbstractDao<AcmObjectLock>
{

    List getAllObjectsByType(String objectType)
    {
        return null;
    }

    @Override
    protected Class getPersistenceClass()
    {
        return AcmObjectLock.class;
    }

    public AcmObjectLock findLock(Long objectId, String objectType)
    {
        String queryText = "SELECT ol " +
                "FROM AcmObjectLock ol " +
                "WHERE " +
                "     ol.objectType = :objectType AND ol.objectId = :objectId";
        Query locksQuery = getEm().createQuery(queryText);

        locksQuery.setParameter("objectType", objectType);
        locksQuery.setParameter("objectId", objectId);
        try
        {
            AcmObjectLock retval = (AcmObjectLock) locksQuery.getSingleResult();
            return retval;
        }
        catch (NoResultException e)
        {
            return null;
        }
    }

    public void remove(AcmObjectLock ol)
    {
        if (!getEm().contains(ol))
        {
            ol = getEm().merge(ol);
        }
        getEm().remove(ol);
    }

    public List<AcmObjectLock> getExpiredLocks()
    {
        String timestampNow = ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        String queryText = "SELECT ol " +
                "FROM AcmObjectLock ol " +
                "WHERE " +
                "     ol.expiry < :timeStampNow";

        Query query = getEm().createQuery(queryText);
        query.setParameter("timeStampNow", new Date(), TemporalType.TIMESTAMP);
        return query.getResultList();
    }
}
