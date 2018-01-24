package com.armedia.acm.services.participants.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FlushModeType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        if (participantList != null)
        {
            for (AcmParticipant ap : participantList)
            {
                log.debug("saving AcmParticipant, id = " + ap.getId() + ", obj type: " + ap.getObjectType() +
                        ", obj id: " + ap.getObjectId() + "; part type: " + ap.getParticipantType() + "; part id: " +
                        ap.getParticipantLdapId());
                log.debug("Participant with id '" + ap.getId() + "' is known to the EM? " + getEm().contains(ap));
                AcmParticipant merged = getEm().merge(ap);

                // only persist if it has not already been merged....
                if (ap.getId() == null)
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

    public boolean hasObjectAccess(String userId, Long objectId, String objectType, String objectAction, String accessType)
    {
        String jpql = "" + "SELECT count(app.id) AS numFound " + "FROM AcmParticipantPrivilege app "
                + "WHERE ( app.participant.participantLdapId = :userid OR app.participant.participantLdapId = '*' )"
                + "AND app.participant.objectId = :objectId " + "AND app.participant.objectType = :objectType "
                + "AND app.objectAction = :action " + "AND app.accessType = :accessType";

        Query find = getEm().createQuery(jpql);
        find.setParameter("userid", userId);
        find.setParameter("objectId", objectId);
        find.setParameter("objectType", objectType);
        find.setParameter("action", objectAction);
        find.setParameter("accessType", accessType);

        find.setFlushMode(FlushModeType.COMMIT);

        // count query will always have exactly one result row.
        Long count = (Long) find.getSingleResult();
        return count > 0;
    }

    public boolean hasObjectAccessViaGroup(Set<String> userGroups, Long objectId, String objectType,
            String objectAction, String accessType)
    {
        TypedQuery<AcmParticipant> findParticipants = getEm().createQuery(
                "SELECT app.participant FROM AcmParticipantPrivilege app " +
                        "WHERE app.participant.objectId = :objectId " +
                        "AND app.participant.objectType = :objectType " +
                        "AND app.objectAction = :action " +
                        "AND app.accessType = :accessType",
                AcmParticipant.class);

        findParticipants.setParameter("objectId", objectId);
        findParticipants.setParameter("objectType", objectType);
        findParticipants.setParameter("action", objectAction);
        findParticipants.setParameter("accessType", accessType);

        findParticipants.setFlushMode(FlushModeType.COMMIT);

        List<AcmParticipant> participants = findParticipants.getResultList();

        return participants.stream()
                .anyMatch(participant -> userGroups.contains(participant.getParticipantLdapId()));
    }

    public List<AcmParticipant> findParticipantsForObject(String objectType, Long objectId)
    {
        return findParticipantsForObject(objectType, objectId, FlushModeType.AUTO);
    }

    public List<AcmParticipant> findParticipantsForObject(String objectType, Long objectId, FlushModeType flushModeType)
    {

        String jpql = "SELECT ap " + "FROM AcmParticipant ap " + "WHERE ap.objectId = :objectId " + "AND ap.objectType = :objectType";

        TypedQuery<AcmParticipant> query = getEm().createQuery(jpql, AcmParticipant.class);
        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);

        query.setFlushMode(flushModeType);

        List<AcmParticipant> retval = query.getResultList();

        return retval;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Boolean getOriginalRestrictedFlag(AcmAssignedObject assignedObject)
    {
        if (assignedObject.getId() == null)
        {
            return assignedObject.getRestricted();
        }

        String jpql = "SELECT e FROM " + assignedObject.getClass().getSimpleName() + " e WHERE e."
                + getIdFieldName(assignedObject.getClass()) + " = :id AND e.restricted = :restricted";

        TypedQuery<? extends AcmAssignedObject> query = getEm().createQuery(jpql, assignedObject.getClass());
        query.setParameter("id", assignedObject.getId());
        query.setParameter("restricted", assignedObject.getRestricted());
        query.setFlushMode(FlushModeType.COMMIT);

        List<? extends AcmAssignedObject> retval = query.getResultList();

        return retval.size() == 0 ? !assignedObject.getRestricted() : assignedObject.getRestricted();
    }

    private String getIdFieldName(Class<?> clazz)
    {
        for (Field field : clazz.getDeclaredFields())
        {
            if (field.getAnnotation(Id.class) != null)
            {
                return field.getName();
            }
        }

        throw new RuntimeException("Didn't find primary key database column name for class: " + clazz.getSimpleName());
    }

    @Transactional
    public int removeAllOtherParticipantsForObject(String objectType, Long objectId, List<AcmParticipant> keepTheseParticipants)
    {
        // a simple delete query will not cascade deletes to related objects. So we need to delete
        // one by one :-(

        List<Long> keepTheseIds = new ArrayList<>(keepTheseParticipants.size());
        for (AcmParticipant keep : keepTheseParticipants)
        {
            keepTheseIds.add(keep.getId());
        }

        List<AcmParticipant> current = findParticipantsForObject(objectType, objectId);

        int deleted = 0;

        for (AcmParticipant ap : current)
        {
            if (!keepTheseIds.contains(ap.getId()))
            {
                log.debug("Removing AcmParticipant, id = " + ap.getId() + ", obj type: " + ap.getObjectType() + ", obj id: "
                        + ap.getObjectId() + "; part type: " + ap.getParticipantType() + "; part id: " + ap.getParticipantLdapId());
                AcmParticipant merged = getEm().merge(ap);
                getEm().remove(merged);
                deleted++;
            }
        }

        return deleted;
    }

    public AcmParticipant getParticipantByLdapIdParticipantTypeObjectTypeObjectId(String userId, String participantType, String objectType,
            Long objectId, FlushModeType flushModeType)
    {
        TypedQuery<AcmParticipant> query = getEm().createQuery(
                "SELECT par FROM AcmParticipant par " +
                        "WHERE par.participantType =:participantType " +
                        "AND par.objectId =:objectId " +
                        "AND par.objectType =:objectType " +
                        "AND par.participantLdapId =:userId",
                AcmParticipant.class);
        query.setParameter("participantType", participantType);
        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);

        query.setFlushMode(flushModeType);

        query.setParameter("userId", userId);

        List<AcmParticipant> results = query.getResultList();
        AcmParticipant acmParticipant = null;
        if (!results.isEmpty())
        {
            acmParticipant = results.get(0);
        }
        return acmParticipant;
    }

    @Transactional
    public void deleteParticipant(Long id)
    {
        AcmParticipant participant = getEm().find(getPersistenceClass(), id);
        if (participant != null)
        {
            getEm().remove(participant);
        }
    }
}