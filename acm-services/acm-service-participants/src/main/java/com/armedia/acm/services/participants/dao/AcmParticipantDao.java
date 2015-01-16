package com.armedia.acm.services.participants.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.participants.model.AcmParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 1/14/15.
 */
public class AcmParticipantDao extends AcmAbstractDao<AcmParticipant>
{

    private final transient Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected Class<AcmParticipant> getPersistenceClass()
    {
        return AcmParticipant.class;
    }

    @Transactional
    public List<AcmParticipant> saveParticipants(List<AcmParticipant> participantList)
    {
        List<AcmParticipant> retval = new ArrayList<>();
        if ( participantList != null )
        {
            for ( AcmParticipant ap : participantList )
            {
                log.debug("saving AcmParticipant, id = " + ap.getId() + ", obj type: " + ap.getObjectType() +
                    ", obj id: " + ap.getObjectId() + "; part type: " + ap.getParticipantType() + "; part id: " +
                    ap.getParticipantLdapId());
                log.debug("Participant with id '" + ap.getId() + "' is known to the EM? " + getEm().contains(ap));
                AcmParticipant merged = getEm().merge(ap);

                // only persist if it has not already been merged....
                if ( ap.getId() == null )
                {
                    log.debug("Persisting since the participant ID is null");
                    getEm().persist(merged);
                }
                else
                {
                    log.debug("Did NOT persist since participant ID is not null");
                }
                retval.add(merged);
                log.debug("saved AcmParticipant, id = " + merged.getId());
            }
        }

        return retval;
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

    @Transactional
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
                log.debug("Removing AcmParticipant, id = " + ap.getId() + ", obj type: " + ap.getObjectType() +
                        ", obj id: " + ap.getObjectId() + "; part type: " + ap.getParticipantType() + "; part id: " +
                        ap.getParticipantLdapId());
                AcmParticipant merged = getEm().merge(ap);
                getEm().remove(merged);
                deleted++;
            }
        }

        return deleted;
    }
}