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
import com.armedia.acm.service.objecthistory.model.AcmAssignment;

import javax.persistence.TypedQuery;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class AcmAssignmentDao extends AcmAbstractDao<AcmAssignment>
{

    @Override
    protected Class<AcmAssignment> getPersistenceClass()
    {
        return AcmAssignment.class;
    }

    public List<AcmAssignment> findByObjectTypeAndObjectId(String objectType, Long objectId)
    {
        TypedQuery<AcmAssignment> findByObjectTypeAndObjectIdQuery = getEm().createQuery("SELECT ass FROM AcmAssignment ass "
                + "WHERE ass.objectType = :objectType AND ass.objectId = :objectId", AcmAssignment.class);
        findByObjectTypeAndObjectIdQuery.setParameter("objectType", objectType);
        findByObjectTypeAndObjectIdQuery.setParameter("objectId", objectId);
        return findByObjectTypeAndObjectIdQuery.getResultList();
    }

}
