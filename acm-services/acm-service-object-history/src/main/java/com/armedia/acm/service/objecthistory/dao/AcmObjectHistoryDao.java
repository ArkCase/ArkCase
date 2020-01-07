/**
 * 
 */
package com.armedia.acm.service.objecthistory.dao;

/*-
 * #%L
 * ACM Service: Object History Service
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
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.Query;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class AcmObjectHistoryDao extends AcmAbstractDao<AcmObjectHistory>
{

    private final Logger LOG = LogManager.getLogger(getClass());

    @Override
    protected Class<AcmObjectHistory> getPersistenceClass()
    {
        return AcmObjectHistory.class;
    }

    public AcmObjectHistory safeFindLastInsertedByObjectIdAndObjectType(Long objectId, String objectType)
    {
        Query select = getEm().createQuery("SELECT objectHistory "
                + "FROM AcmObjectHistory objectHistory "
                + "WHERE objectHistory.objectId=:objectId "
                + "AND objectHistory.objectType=:objectType "
                + "ORDER BY objectHistory.modified DESC");

        select.setParameter("objectId", objectId);
        select.setParameter("objectType", objectType);

        // Set first result to 1 because the current object history is saved first and after that this
        // method is called, so we need second row from the query (which is previous)
        select.setFirstResult(1);
        select.setMaxResults(1);

        @SuppressWarnings("unchecked")
        List<AcmObjectHistory> results = (List<AcmObjectHistory>) select.getResultList();

        if (results != null && results.size() == 1)
        {
            return results.get(0);
        }

        return null;
    }

}
