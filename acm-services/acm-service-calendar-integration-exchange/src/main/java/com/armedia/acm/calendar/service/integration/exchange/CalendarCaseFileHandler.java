package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;

import javax.persistence.Query;

import java.util.List;
import java.util.stream.Collectors;

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
    protected AcmContainerEntity getEntity(String objectId, boolean restrictedOnly)
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
            query = getEm().createQuery("SELECT cf FROM CaseFile cf WHERE cf.status = :status");
        } else
        {
            query = getEm().createQuery("SELECT cf FROM CaseFile cf WHERE cf.status = :status AND cf.modified < :modified");
            query.setParameter("modified", calculateModifiedDate(daysClosed));
        }
        query.setParameter("status", "CLOSED");
        List<?> resultList = query.getResultList();

        return resultList.stream().map(item -> AcmContainerEntity.class.cast(item)).collect(Collectors.toList());
    }

}
