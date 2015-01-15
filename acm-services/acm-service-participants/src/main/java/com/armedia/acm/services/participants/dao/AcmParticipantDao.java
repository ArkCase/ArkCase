package com.armedia.acm.services.participants.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.participants.model.AcmParticipant;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 1/14/15.
 */
public class AcmParticipantDao extends AcmAbstractDao<AcmParticipant>
{
    @Override
    protected Class<AcmParticipant> getPersistenceClass()
    {
        return AcmParticipant.class;
    }

    public void saveParticipants(List<AcmParticipant> participantList)
    {
        if ( participantList != null )
        {
            for ( AcmParticipant ap : participantList )
            {
                getEm().persist(ap);
            }
        }
    }

    public List<AcmParticipant> findParticipantsForObject(String objectType, Long objectId)
    {

        String jpql = "SELECT ap " +
                "FROM AcmParticipant ap " +
                "WHERE ap.objectId = :objectId " +
                "AND ap.objectType = :objectType";

        Query query = getEm().createQuery(jpql);
        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);

        List<AcmParticipant> retval = query.getResultList();

        return retval;
    }

    public int removeAllOtherParticipantsForObject(String objectType, Long objectId, List<AcmParticipant> keepTheseParticipants)
    {
        // a simple delete query will not cascade deletes to related objects.  So we need to delete
        // one by one :-(

        List<Long> keepTheseIds = new ArrayList<>(keepTheseParticipants.size());
        for ( AcmParticipant keep : keepTheseParticipants )
        {
            keepTheseIds.add(keep.getId());
        }

        List<AcmParticipant> current = findParticipantsForObject(objectType, objectId);

        int deleted = 0;

        for ( AcmParticipant ap : current )
        {
            if ( ! keepTheseIds.contains(ap.getId()) )
            {
                getEm().remove(ap);
                deleted++;
            }
        }

        return deleted;
    }
}