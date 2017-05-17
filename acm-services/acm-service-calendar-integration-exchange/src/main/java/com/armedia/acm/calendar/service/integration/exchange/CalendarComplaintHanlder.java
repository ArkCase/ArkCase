package com.armedia.acm.calendar.service.integration.exchange;

import javax.persistence.Query;

import java.util.List;

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
    protected Object getEntity(String objectId, boolean restrictedOnly)
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
            return resultList.get(0);
        } else
        {
            return null;
        }
    }

}
