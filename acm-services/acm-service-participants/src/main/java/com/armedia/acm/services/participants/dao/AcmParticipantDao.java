package com.armedia.acm.services.participants.dao;

/*-
 * #%L
 * ACM Service: Participants
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
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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

    private final transient Logger log = LogManager.getLogger(getClass());

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

        // some AcmAssignedObjects are not JPA entities (like AcmTask). So they don't contain restricted flag at all
        if (!getEm().getMetamodel().getEntities().stream()
                .anyMatch(entityType -> entityType.getJavaType().equals(assignedObject.getClass())))
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
        List<Field> idFields = FieldUtils.getFieldsListWithAnnotation(clazz, Id.class);
        // hopefully in this case there will only be one.
        if (idFields != null && idFields.size() == 1)
        {
            return idFields.get(0).getName();
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
