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
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import java.util.Date;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class AcmTimesheetDao extends AcmAbstractDao<AcmTimesheet>
{

    private Logger LOG = LogManager.getLogger(getClass());

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
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<AcmTimesheet> timesheetCriteriaQuery = builder.createQuery(AcmTimesheet.class);
        Root<AcmTimesheet> acmTimesheetRoot = timesheetCriteriaQuery.from(AcmTimesheet.class);

        Subquery<Long> subquery = timesheetCriteriaQuery.subquery(Long.class);
        Root<AcmTimesheet> rootSubquery = subquery.from(AcmTimesheet.class);
        subquery.select(rootSubquery.get("id")).distinct(true);
        Join<AcmTimesheet, AcmTime> join = rootSubquery.join("times", JoinType.LEFT);
        subquery.where(builder.and(builder.equal(join.get("objectId"), objectId),
                builder.equal(join.get("type"), objectType)));

        timesheetCriteriaQuery.select(acmTimesheetRoot);
        timesheetCriteriaQuery.where(acmTimesheetRoot.<Long> get("id").in(subquery));

        if (sortParams != null && !"".equals(sortParams))
        {
            timesheetCriteriaQuery.orderBy(builder.asc(acmTimesheetRoot.<String> get(sortParams)));
        }

        TypedQuery<AcmTimesheet> selectQuery = getEm().createQuery(timesheetCriteriaQuery).setFirstResult(startRow).setMaxResults(maxRows);
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
