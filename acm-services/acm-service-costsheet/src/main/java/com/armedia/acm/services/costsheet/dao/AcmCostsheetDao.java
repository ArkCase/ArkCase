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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class AcmCostsheetDao extends AcmAbstractDao<AcmCostsheet>
{

    private Logger log = LogManager.getLogger(getClass());

    @Override
    protected Class<AcmCostsheet> getPersistenceClass()
    {
        return AcmCostsheet.class;
    }

    public List<AcmCostsheet> findByObjectIdAndType(Long objectId, String objectType, int startRow, int maxRows, String sortParams)
    {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<AcmCostsheet> criteriaQuery = builder.createQuery(AcmCostsheet.class);
        Root<AcmCostsheet> costsheetRoot = criteriaQuery.from(AcmCostsheet.class);
        criteriaQuery.select(costsheetRoot).where(
                builder.and(
                        builder.equal(costsheetRoot.<Long> get("parentId"), objectId),
                        builder.equal(costsheetRoot.<Long> get("parentType"), objectType)));
        if (sortParams != null && !"".equals(sortParams))
        {
            criteriaQuery.orderBy(builder.asc(costsheetRoot.get(sortParams)));
        }
        TypedQuery<AcmCostsheet> query = getEm().createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public String getSupportedObjectType()
    {
        return CostsheetConstants.OBJECT_TYPE;
    }

}
