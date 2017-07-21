package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;

import javax.persistence.Query;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 11, 2017
 *
 */
public class CalendarComplaintHanlder extends CalendarEntityHandlerBase
{

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandlerBase#getEntityType()
     */
    @Override
    protected String getEntityType()
    {
        return "COMPLAINT";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandlerBase#getEntity(java.lang.String,
     * boolean)
     */
    @Override
    protected AcmContainerEntity getEntity(String objectId, boolean restrictedOnly)
    {
        Query query;
        if (restrictedOnly)
        {
            query = getEm().createQuery("SELECT co FROM Complaint co WHERE co.complaintId = :objectId AND co.restricted = :restricted");
            query.setParameter("restricted", true);
        } else
        {
            query = getEm().createQuery("SELECT co FROM Complaint co WHERE co.complaintId = :objectId");
        }
        query.setParameter("objectId", Long.valueOf(objectId));
        List<?> resultList = query.getResultList();
        if (!resultList.isEmpty())
        {
            return (AcmContainerEntity) resultList.get(0);
        } else
        {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandlerBase#getEntities(java.lang.Integer)
     */
    @Override
    protected List<AcmContainerEntity> getEntities(Integer daysClosed)
    {
        Query query;
        // TECHNICAL DEBT: to be discussed with the reviewers.
        // The number of closed case files will increase over time, and the purging process will get longer.
        // We need a way to exclude the already purged case files from the result of the query.
        // The question is how to do that, shall we add a field to the CaseFile, or is there another
        // less intrusive approach?
        if (daysClosed == null)
        {
            query = getEm().createQuery("SELECT co FROM Complaint co WHERE co.status = :status");
        } else
        {
            query = getEm().createQuery("SELECT co FROM Complaint co WHERE co.status = :status AND co.modified < :modified");
            query.setParameter("modified", calculateModifiedDate(daysClosed));
        }
        query.setParameter("status", "CLOSED");
        List<?> resultList = query.getResultList();

        return resultList.stream().map(item -> AcmContainerEntity.class.cast(item)).collect(Collectors.toList());
    }

}
