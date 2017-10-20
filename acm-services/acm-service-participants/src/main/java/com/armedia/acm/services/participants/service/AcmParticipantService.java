package com.armedia.acm.services.participants.service;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmParticipantsException;
import com.armedia.acm.services.participants.dao.AcmParticipantDao;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.CheckParticipantListModel;
import com.armedia.acm.services.participants.model.ParticipantTypes;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 01.04.2015.
 */
public class AcmParticipantService
{

    private AcmParticipantDao participantDao;
    private ParticipantsBusinessRule participantsBusinessRule;
    private AcmParticipantEventPublisher acmParticipantEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public AcmParticipant saveParticipant(String userId, String participantType, Long objectId, String objectType)
            throws AcmAccessControlException
    {
        AcmParticipant returnedParticipant = getParticipantByParticipantTypeAndObjectTypeAndId(userId, participantType, objectType,
                objectId);

        if (returnedParticipant != null)
        {
            log.debug("Participant {} already exists and is added on object [{}]:[{}] as a {}", userId, objectType, objectId,
                    participantType);

            return returnedParticipant;
        }

        AcmParticipant participant = new AcmParticipant();
        participant.setParticipantLdapId(userId);
        participant.setParticipantType(participantType);
        participant.setObjectId(objectId);
        participant.setObjectType(objectType);

        CheckParticipantListModel model = new CheckParticipantListModel();
        List<String> errorListAfterRules = applyParticipantRules(participant, model);
        if (errorListAfterRules != null && !errorListAfterRules.isEmpty())
        {
            throw new AcmAccessControlException(errorListAfterRules,
                    "Conflict permissions combination has occurred for the chosen participants");
        }

        AcmParticipant savedParticipant = getParticipantDao().save(participant);

        getAcmParticipantEventPublisher().publishParticipantCreatedEvent(savedParticipant, true);

        log.debug("Added participant [{}] to object type [{}] with object id [{}]", userId, objectType, objectId);

        return savedParticipant;
    }

    private List<String> applyParticipantRules(AcmParticipant participant, CheckParticipantListModel model)
    {
        List<AcmParticipant> allParticipantsFromParentObject = participantDao.findParticipantsForObject(participant.getObjectType(),
                participant.getObjectId());
        if (allParticipantsFromParentObject != null)
        {
            allParticipantsFromParentObject
                    .removeIf(parentObjectParticipant -> parentObjectParticipant.getId().equals(participant.getId()));
            allParticipantsFromParentObject.add(participant);

            model.setParticipantList(allParticipantsFromParentObject);
            model.setObjectType(participant.getObjectType());
            model = participantsBusinessRule.applyRules(model);

            List<String> listOfErrors = new ArrayList<>();
            if (!model.getErrorsList().isEmpty())
            {
                listOfErrors = model.getErrorsList();
            }
            return listOfErrors;
        }
        return null;
    }

    public AcmParticipant getParticipantByParticipantTypeAndObjectTypeAndId(String userId, String participantType, String objectType,
            Long objectId)
    {
        return getParticipantDao().getParticipantByParticipantTypeAndObjectTypeAndId(userId, participantType, objectType, objectId);
    }

    public AcmParticipant changeParticipantRole(AcmParticipant participant, String newRole) throws AcmAccessControlException
    {
        participant.setParticipantType(newRole);
        CheckParticipantListModel model = new CheckParticipantListModel();

        applyParticipantRules(participant, model);

        AcmParticipant updatedParticipant = getParticipantDao().save(participant);

        getAcmParticipantEventPublisher().publishParticipantUpdatedEvent(updatedParticipant, true);

        return updatedParticipant;
    }

    public List<AcmParticipant> listAllParticipantsPerObjectTypeAndId(String objectType, Long objectId)
    {
        return getParticipantDao().findParticipantsForObject(objectType, objectId);
    }

    public void removeParticipant(Long participantId)
    {
        AcmParticipant participant = getParticipantDao().find(participantId);
        if (participant == null)
        {
            return;
        }
        getParticipantDao().deleteParticipant(participantId);
        getAcmParticipantEventPublisher().publishParticipantDeletedEvent(participant, true);
    }

    public void removeParticipant(AcmParticipant participant)
    {
        if (participant == null)
        {
            return;
        }
        removeParticipant(participant.getId());
    }

    public void removeParticipant(String userId, String participantType, String objectType, Long objectId)
    {
        AcmParticipant participant = getParticipantByParticipantTypeAndObjectTypeAndId(userId, participantType, objectType, objectId);
        removeParticipant(participant);
    }

    public AcmParticipant findParticipant(Long id)
    {
        return getParticipantDao().find(id);
    }

    /**
     * Validates the {@link AcmParticipant}s for some object.
     *
     * @param participants
     *            the list of {@link AcmParticipant}s to validate
     * @throws AcmParticipantsException
     *             when the {@link AcmParticipant}s are not valid.
     */
    public void validateParticipants(List<AcmParticipant> participants) throws AcmParticipantsException
    {
        // missing participant id
        List<AcmParticipant> missingParticipantLdapIds = participants.stream().filter(participant -> participant.getReceiverLdapId() == null
                && !participant.getParticipantType().equals(ParticipantTypes.ASSIGNEE)).collect(Collectors.toList());

        if (missingParticipantLdapIds.size() > 0)
        {
            String errorMessage = "Missing participant LDAP id!";
            List<String> errorList = missingParticipantLdapIds.stream()
                    .map(participant -> "ParticipantLdapId: " + participant.getParticipantType()).collect(Collectors.toList());
            throw new AcmParticipantsException(errorList, errorMessage);
        }

        // missing participantTypes
        List<AcmParticipant> missingParticipantTypes = participants.stream().filter(participant -> participant.getParticipantType() == null)
                .collect(Collectors.toList());

        if (missingParticipantTypes.size() > 0)
        {
            String errorMessage = "Missing participant type!";
            List<String> errorList = missingParticipantTypes.stream()
                    .map(participant -> "ParticipantLdapId: " + participant.getParticipantLdapId()).collect(Collectors.toList());
            throw new AcmParticipantsException(errorList, errorMessage);
        }

        // multiple assignees
        List<AcmParticipant> assignees = participants.stream()
                .filter(participant -> participant.getParticipantType() == ParticipantTypes.ASSIGNEE).collect(Collectors.toList());
        if (assignees.size() > 1)
        {
            String errorMessage = "Multiple assignees found!";
            List<String> errorList = assignees.stream().map(participant -> "ParticipantLdapId: " + participant.getParticipantLdapId())
                    .collect(Collectors.toList());
            throw new AcmParticipantsException(errorList, errorMessage);
        }

        // multiple owning groups
        List<AcmParticipant> owningGroups = participants.stream()
                .filter(participant -> participant.getParticipantType() == ParticipantTypes.OWNING_GROUP).collect(Collectors.toList());
        if (owningGroups.size() > 1)
        {
            String errorMessage = "Multiple owning groups found!";
            List<String> errorList = owningGroups.stream().map(participant -> "ParticipantLdapId: " + participant.getParticipantLdapId())
                    .collect(Collectors.toList());
            throw new AcmParticipantsException(errorList, errorMessage);
        }

        // search for duplicate participants LDAPIds. One participant cannot have different roles for an object
        Set<String> allLdapIds = new HashSet<>();
        Set<String> duplicateParticipantLdapIds = participants.stream().map(participant -> participant.getParticipantLdapId())
                .filter(participantLdapId -> !allLdapIds.add(participantLdapId)).collect(Collectors.toSet());
        if (duplicateParticipantLdapIds.size() > 0)
        {
            String errorMessage = "Participants in multiple roles found!";
            List<String> errorList = duplicateParticipantLdapIds.stream()
                    .map(participantLdapId -> "ParticipantLdapId: " + participantLdapId).collect(Collectors.toList());
            throw new AcmParticipantsException(errorList, errorMessage);
        }
    }

    public List<AcmParticipant> getParticipantsFromParentObject(String objectType, Long objectId)
    {
        Reflections reflections = new Reflections("com.armedia");
        Set<Class<? extends AcmAssignedObject>> classes = reflections.getSubTypesOf(AcmAssignedObject.class);

        for (Class<? extends AcmAssignedObject> class1 : classes)
        {
            AcmAssignedObject assignedObject = null;
            try
            {
                assignedObject = class1.newInstance();
            }
            catch (Exception e)
            {
                // should never happen
                log.error("Cannot create new instance of class: " + class1.getName(), e);
                return new ArrayList<>();
            }
            if (assignedObject.getObjectType().equals(objectType))
            {
                return assignedObject.getParticipants();
            }
        }

        log.warn("No participants found for objectType: " + objectType + " objectId: " + objectId);

        return new ArrayList<>();
    }

    public AcmParticipantDao getParticipantDao()
    {
        return participantDao;
    }

    public void setParticipantDao(AcmParticipantDao participantDao)
    {
        this.participantDao = participantDao;
    }

    public ParticipantsBusinessRule getParticipantsBusinessRule()
    {
        return participantsBusinessRule;
    }

    public void setParticipantsBusinessRule(ParticipantsBusinessRule participantsBusinessRule)
    {
        this.participantsBusinessRule = participantsBusinessRule;
    }

    public AcmParticipantEventPublisher getAcmParticipantEventPublisher()
    {
        return acmParticipantEventPublisher;
    }

    public void setAcmParticipantEventPublisher(AcmParticipantEventPublisher acmParticipantEventPublisher)
    {
        this.acmParticipantEventPublisher = acmParticipantEventPublisher;
    }
}
