package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.data.AcmBeforeInsertListener;
import com.armedia.acm.data.AcmBeforeUpdateListener;
import com.armedia.acm.services.dataaccess.service.EntityParticipantsChangedEventPublisher;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;
import com.armedia.acm.services.participants.model.CheckParticipantListModel;
import com.armedia.acm.services.participants.service.AcmParticipantService;
import com.armedia.acm.services.participants.service.ParticipantsBusinessRule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.FlushModeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 1/6/15.
 */
public class DataAccessPrivilegeListener implements AcmBeforeUpdateListener, AcmBeforeInsertListener
{
    private final transient Logger log = LoggerFactory.getLogger(getClass());
    private AcmAssignedObjectBusinessRule assignmentBusinessRule;
    private AcmAssignedObjectBusinessRule accessControlBusinessRule;
    private ParticipantsBusinessRule participantsBusinessRule;
    private AcmParticipantService participantService;
    private EntityParticipantsChangedEventPublisher entityParticipantsChangedEventPublisher;

    @Override
    public void beforeInsert(Object object) throws AcmAccessControlException
    {
        log.trace("inserted: " + object);
        applyAssignmentAndAccessRules(object);
    }

    @Override
    public void beforeUpdate(Object object) throws AcmAccessControlException
    {
        log.trace("updated: " + object);
        applyAssignmentAndAccessRules(object);
    }

    public void applyAssignmentAndAccessRules(Object obj) throws AcmAccessControlException
    {
        if (obj instanceof AcmAssignedObject)
        {
            AcmAssignedObject assignedObject = (AcmAssignedObject) obj;
            applyAssignRules(assignedObject);
            applyDataAccessRules(assignedObject);
            updateParentPointers(assignedObject);
            validateParticipantAssignmentRules(assignedObject);
            handleParticipantsChanged(assignedObject);
        }
    }

    private void handleParticipantsChanged(AcmAssignedObject assignedObject)
    {
        List<AcmParticipant> originalParticipants = new ArrayList<>();
        if (assignedObject.getId() != null)
        {
            originalParticipants = getParticipantService().listAllParticipantsPerObjectTypeAndId(assignedObject.getObjectType(),
                    assignedObject.getId(), FlushModeType.COMMIT);
        }

        // publish EntityParticipantsChangedEvent if the participants are not equal
        boolean hasEqualParticipants = true;

        if (assignedObject.getParticipants().size() != originalParticipants.size())
        {
            hasEqualParticipants = false;
        }
        else
        {
            for (AcmParticipant assignedObjectParticipant : assignedObject.getParticipants())
            {
                boolean found = false;
                for (AcmParticipant originalParticipant : originalParticipants)
                {

                    if (assignedObjectParticipant.getParticipantLdapId().equals(originalParticipant.getParticipantLdapId()) &&
                            assignedObjectParticipant.getParticipantType().equals(originalParticipant.getParticipantType()))
                    {
                        found = true;
                        break;
                    }
                }

                if (!found)
                {
                    hasEqualParticipants = false;
                    break;
                }
            }
        }

        if (!hasEqualParticipants)
        {
            getEntityParticipantsChangedEventPublisher().publishEvent(assignedObject, originalParticipants);
        }
    }

    private void validateParticipantAssignmentRules(AcmAssignedObject assignedObject) throws AcmAccessControlException
    {
        log.trace("Validating participants assingments for entity : [{}]", assignedObject);

        CheckParticipantListModel model = new CheckParticipantListModel();
        model.setParticipantList(assignedObject.getParticipants());
        model.setObjectType(assignedObject.getObjectType());

        model = participantsBusinessRule.applyRules(model);

        log.trace("Finished validating participants assingments for entity : [{}]", assignedObject);
        if (model.getErrorsList() != null && !model.getErrorsList().isEmpty())
        {
            throw new AcmAccessControlException(model.getErrorsList(),
                    "Conflict permissions combination has occurred for entity's participants");
        }
    }

    private void updateParentPointers(AcmAssignedObject assignedObject)
    {
        for (AcmParticipant participant : assignedObject.getParticipants())
        {
            participant.setObjectType(assignedObject.getObjectType());
            participant.setObjectId(assignedObject.getId());

            log.trace("participant '" + participant.getParticipantLdapId() + "'");
            for (AcmParticipantPrivilege priv : participant.getPrivileges())
            {
                log.trace("\t privilege: " + priv.getAccessType() + " " + priv.getObjectAction());
                priv.setParticipant(participant);
            }
        }
    }

    private void applyDataAccessRules(AcmAssignedObject assignedObject)
    {
        getAccessControlBusinessRule().applyRules(assignedObject);
    }

    private void applyAssignRules(AcmAssignedObject assignedObject)
    {
        getAssignmentBusinessRule().applyRules(assignedObject);
    }

    public void setAssignmentBusinessRule(AcmAssignedObjectBusinessRule assignmentBusinessRule)
    {
        this.assignmentBusinessRule = assignmentBusinessRule;
    }

    public AcmAssignedObjectBusinessRule getAssignmentBusinessRule()
    {
        return assignmentBusinessRule;
    }

    public void setAccessControlBusinessRule(AcmAssignedObjectBusinessRule accessControlBusinessRule)
    {
        this.accessControlBusinessRule = accessControlBusinessRule;
    }

    public AcmAssignedObjectBusinessRule getAccessControlBusinessRule()
    {
        return accessControlBusinessRule;
    }

    public ParticipantsBusinessRule getParticipantsBusinessRule()
    {
        return participantsBusinessRule;
    }

    public void setParticipantsBusinessRule(ParticipantsBusinessRule participantsRule)
    {
        this.participantsBusinessRule = participantsRule;
    }

    public AcmParticipantService getParticipantService()
    {
        return participantService;
    }

    public void setParticipantService(AcmParticipantService participantService)
    {
        this.participantService = participantService;
    }

    public EntityParticipantsChangedEventPublisher getEntityParticipantsChangedEventPublisher()
    {
        return entityParticipantsChangedEventPublisher;
    }

    public void setEntityParticipantsChangedEventPublisher(EntityParticipantsChangedEventPublisher entityParticipantsChangedEventPublisher)
    {
        this.entityParticipantsChangedEventPublisher = entityParticipantsChangedEventPublisher;
    }
}
