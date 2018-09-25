/**
 * 
 */
package com.armedia.acm.services.costsheet.dao;

/*-
 * #%L
 * ACM Service: Costsheet
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
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.TypedQuery;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class AcmCostsheetDao extends AcmAbstractDao<AcmCostsheet>
{

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected Class<AcmCostsheet> getPersistenceClass()
    {
        return AcmCostsheet.class;
    }

    public List<AcmCostsheet> findByObjectIdAndType(Long objectId, String objectType, int startRow, int maxRows, String sortParams)
    {
        String orderByQuery = "";
        if (sortParams != null && !"".equals(sortParams))
        {
            orderByQuery = " ORDER BY costsheet." + sortParams;
        }

        TypedQuery<AcmCostsheet> selectQuery = getEm().createQuery("SELECT costsheet "
                + "FROM AcmCostsheet costsheet "
                + "WHERE costsheet.parentId = :parentId "
                + "AND costsheet.parentType = :parentType "
                + orderByQuery, AcmCostsheet.class);

        selectQuery.setParameter("parentId", objectId);
        selectQuery.setParameter("parentType", objectType);
        selectQuery.setFirstResult(startRow);
        selectQuery.setMaxResults(maxRows);

        List<AcmCostsheet> costsheets = selectQuery.getResultList();

        return costsheets;
    }

    @Override
    public String getSupportedObjectType()
    {
        return CostsheetConstants.OBJECT_TYPE;
    }

}
