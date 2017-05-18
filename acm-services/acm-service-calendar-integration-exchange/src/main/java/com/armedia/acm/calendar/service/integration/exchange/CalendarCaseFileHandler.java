package com.armedia.acm.calendar.service.integration.exchange;

import javax.persistence.Query;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 26, 2017
 *
 */
public class CalendarCaseFileHandler extends CalendarEntityHandlerBase
{

    /*
     * (non-Javadoc)
     * 
     * @see com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandlerBase#getEntityType()
     */
    @Override
    protected String getEntityType()
    {
        return "CASE_FILE";
    }

    @Override
    protected Object getEntity(String objectId, boolean restrictedOnly)
    {
        Query query;
        if (restrictedOnly)
        {
            query = getEm().createQuery("SELECT cf FROM CaseFile cf WHERE cf.id = :objectId AND cf.restricted = :restricted");
            query.setParameter("restricted", true);
        } else
        {
            query = getEm().createQuery("SELECT cf FROM CaseFile cf WHERE cf.id = :objectId");
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
