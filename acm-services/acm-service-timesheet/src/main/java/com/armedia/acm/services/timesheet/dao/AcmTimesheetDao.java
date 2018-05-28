/**
 * 
 */
package com.armedia.acm.services.timesheet.dao;

/*-
 * #%L
 * ACM Service: Timesheet
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
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Query;

import java.util.Date;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class AcmTimesheetDao extends AcmAbstractDao<AcmTimesheet>
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    protected Class<AcmTimesheet> getPersistenceClass()
    {
        return AcmTimesheet.class;
    }

    public AcmTimesheet findByUserIdStartAndEndDate(String userId, Date startDate, Date endDate)
    {
        Query selectQuery = getEm().createQuery("SELECT timesheet "
                + "FROM AcmTimesheet timesheet "
                + "WHERE timesheet.user.userId = :userId "
                + "AND timesheet.startDate = :startDate "
                + "AND timesheet.endDate = :endDate");

        selectQuery.setParameter("userId", userId);
        selectQuery.setParameter("startDate", startDate);
        selectQuery.setParameter("endDate", endDate);

        AcmTimesheet timesheet = null;
        try
        {
            timesheet = (AcmTimesheet) selectQuery.getSingleResult();
        }
        catch (Exception e)
        {
            LOG.warn("Timesheet for period of " + startDate + " to " + endDate + " is not found.");
        }

        return timesheet;
    }

    public List<AcmTimesheet> findByObjectIdAndType(Long objectId, String objectType, int startRow, int maxRows, String sortParams)
    {
        String orderByQuery = "";
        if (sortParams != null && !"".equals(sortParams))
        {
            orderByQuery = " ORDER BY timesheet." + sortParams;
        }

        // Use "WHERE timesheet.id IN" because DISTINCT not work with type CLOB.
        // This query will avoid using DISTINCT with timesheet.details property which is of type CLOB
        Query selectQuery = getEm().createQuery("SELECT timesheet "
                + "FROM AcmTimesheet timesheet "
                + "WHERE timesheet.id IN("
                + "SELECT DISTINCT t.id "
                + "FROM AcmTimesheet t "
                + "LEFT JOIN t.times AS times "
                + "WHERE times.objectId = :objectId "
                + "AND times.type = :objectType"
                + ")"
                + orderByQuery);

        selectQuery.setParameter("objectId", objectId);
        selectQuery.setParameter("objectType", objectType);
        selectQuery.setFirstResult(startRow);
        selectQuery.setMaxResults(maxRows);

        @SuppressWarnings("unchecked")
        List<AcmTimesheet> timesheets = (List<AcmTimesheet>) selectQuery.getResultList();

        return timesheets;
    }

    @Override
    public String getSupportedObjectType()
    {
        return TimesheetConstants.OBJECT_TYPE;
    }

}
