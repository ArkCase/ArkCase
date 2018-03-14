package com.armedia.acm.services.dataaccess.annotations;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.dataaccess.service.impl.AcmAssignedObjectBusinessRule;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Aspect
@Component
public class DecoratedAssignedObjectParticipantAspect
{

    private final String USER_VALIDATION_TYPE = "user";
    private final String TYPE_VALIDATION_TYPE = "type";

    private AcmAssignedObjectBusinessRule assignmentBusinessRule;
    private AcmDataService springAcmDataService;

    /**
     * Around aspect targeting annotation: @{@link DecoratedAssignedObjectParticipants}
     * Handled Responses:
     * - {@link AcmAssignedObject}
     * - {@link List<@AcmParticipant>}
     */
    @Around(value = "@annotation(decoratedAssignedObjectParticipants)")
    public Object aroundDecoratingMethod(ProceedingJoinPoint pjp, DecoratedAssignedObjectParticipants decoratedAssignedObjectParticipants)
            throws Throwable
    {
        Object ret = pjp.proceed();
        // Check if the returned Object is AcmAssignableObject
        try
        {
            if (AcmAssignedObject.class.isAssignableFrom(ret.getClass()))
            {

                AcmAssignedObject assignedObject = (AcmAssignedObject) ret;
                // Decorate the AssignableObject
                assignedObject = decorateAssignableObjectParticipants(assignedObject);
                return assignedObject;
            } // check if the returned Object is a list of AcmParticipants and the participants list is not empty
            else if (List.class.isAssignableFrom(ret.getClass()) && ((List) ret).size() > 0)
            {

                // check if the instance of the annotation exists and the items in the list is instance of
                // AcmParticipant
                if (decoratedAssignedObjectParticipants != null && AcmParticipant.class.isAssignableFrom(((List) ret).get(0).getClass()))
                {
                    // Get function parameters and annotation parameters and
                    // map them to objectType and objectId so we can get participants parent AcmAssignableObject
                    Object[] args = pjp.getArgs();
                    AcmObject entity = getAssignedObjectForParticipants(args, decoratedAssignedObjectParticipants);
                    if (entity != null && AcmAssignedObject.class.isAssignableFrom(entity.getClass()))
                    {
                        AcmAssignedObject assignedObject = (AcmAssignedObject) entity;
                        // Decorate the whole Object and return the participants list
                        assignedObject = decorateAssignableObjectParticipants(assignedObject);
                        return assignedObject.getParticipants();
                    }
                }
            }
        }
        catch (Exception ex)
        {
            return ret;
        }
        return ret;
    }

    // Method for Finding the ObjectType and ObjectId and return AcmAssignedObject for participants
    private AcmObject getAssignedObjectForParticipants(Object[] args,
            DecoratedAssignedObjectParticipants decoratedAssignedObjectParticipants) throws Exception
    {
        Object objectType;
        Object objectId;
        if (decoratedAssignedObjectParticipants.objectType().equals(""))
        {
            objectType = args[decoratedAssignedObjectParticipants.objectTypeIndex()];
        }
        else
        {
            objectType = decoratedAssignedObjectParticipants.objectType();
        }
        if (decoratedAssignedObjectParticipants.objectId() == -1)
        {
            objectId = args[decoratedAssignedObjectParticipants.objectIdIndex()];
        }
        else
        {
            objectId = decoratedAssignedObjectParticipants.objectId();
        }
        // Get the objects Dao
        AcmAbstractDao<AcmObject> dao = springAcmDataService.getDaoByObjectType(objectType.toString());
        // find participant's parent AcmAssignableObject by id
        return dao.find(((Number) objectId).longValue());
    }

    private AcmAssignedObject decorateAssignableObjectParticipants(AcmAssignedObject assignedObject) throws Exception
    {
        List<AcmParticipant> originalParticipants = assignedObject.getParticipants();

        // get a new list of participants, not to change the original participants references
        List<AcmParticipant> copyParticipants = getParticipantsCopyWithAppliedAssignmentRules(assignedObject, originalParticipants);

        // loop through the participants to validate and decorate editable user, editable type and deletable
        for (int i = 0; i < copyParticipants.size(); i++)
        {
            // validate and decorate participant editable user
            validateParticipantsEditable(assignedObject, originalParticipants, i, USER_VALIDATION_TYPE);
            // reset copy participants
            copyParticipants = getParticipantsCopyWithAppliedAssignmentRules(assignedObject, originalParticipants);
            // validate and decorate participant editable type
            validateParticipantsEditable(assignedObject, originalParticipants, i, TYPE_VALIDATION_TYPE);
            // reset copy participants
            copyParticipants = getParticipantsCopyWithAppliedAssignmentRules(assignedObject, originalParticipants);
            // validate and decorate deletable participant
            validateParticipantsDeletable(assignedObject, originalParticipants, i);
            // reset copy participants
            copyParticipants = getParticipantsCopyWithAppliedAssignmentRules(assignedObject, originalParticipants);
        }

        assignedObject.setParticipants(originalParticipants);
        return assignedObject;
    }

    // Validate and Decorate participant editable fields depending on validation type
    // @String ValidationType:
    // 1. User (participantLdapId)
    // 2. Type(participantType)
    private void validateParticipantsEditable(AcmAssignedObject assignedObjectWithCopyParticipants,
            List<AcmParticipant> originalParticipants, int index, String validationType)
    {
        int copyParticipantsSize = assignedObjectWithCopyParticipants.getParticipants().size();
        AcmParticipant copyParticipant = assignedObjectWithCopyParticipants.getParticipants().get(index);

        String oldObj = "";
        String newObj = UUID.randomUUID().toString();

        if (validationType.equals(USER_VALIDATION_TYPE))
        {
            oldObj = copyParticipant.getParticipantLdapId();
            copyParticipant.setParticipantLdapId(newObj);
        }
        else if (validationType.equals(TYPE_VALIDATION_TYPE))
        {
            oldObj = copyParticipant.getParticipantType();
            copyParticipant.setParticipantType(newObj);
        }

        getAssignmentBusinessRule().applyRules(assignedObjectWithCopyParticipants);

        Boolean changeParticipantEditable;
        // check if size is equals and no new participant is added
        if (assignedObjectWithCopyParticipants.getParticipants().size() != copyParticipantsSize)
        {
            // new participants added editable fields should be changed
            changeParticipantEditable = true;
        }
        else
        {
            // Check if we should check for participant editable fields
            changeParticipantEditable = shouldChangeOriginalParticipant(assignedObjectWithCopyParticipants, copyParticipant, newObj,
                    validationType);
        }
        if (changeParticipantEditable)
        {
            // find and change the original participant editable fields
            changeOriginalParticipant(originalParticipants, copyParticipant, oldObj, validationType);
        }
    }

    // Compare original participant with the copy participant and change editable fields to false
    private void changeOriginalParticipant(List<AcmParticipant> originalParticipants, AcmParticipant copyParticipant, String oldObj,
            String validationType)
    {
        originalParticipants.stream()
                .filter(origParticipant -> validationType.equals(USER_VALIDATION_TYPE)
                        && origParticipant.getParticipantLdapId().equals(oldObj)
                        && origParticipant.getParticipantType().equals(copyParticipant.getParticipantType())
                        || validationType.equals(TYPE_VALIDATION_TYPE)
                                && origParticipant.getParticipantLdapId().equals(copyParticipant.getParticipantLdapId())
                                && origParticipant.getParticipantType().equals(oldObj))
                .forEach(origParticipant -> {
                    if (validationType.equals(USER_VALIDATION_TYPE))
                    {
                        origParticipant.setEditableUser(false);
                    }
                    else
                    {
                        origParticipant.setEditableType(false);
                    }
                    origParticipant.setDeletable(false);
                });
    }

    // Check if current participant editable user or type should be changed
    private Boolean shouldChangeOriginalParticipant(AcmAssignedObject assignedObjectWithCopyParticipants, AcmParticipant copyParticipant,
            String newObj, String validationType)
    {
        AcmParticipant changedParticipant = null;
        // check validation type
        // if validation type is user check for participantLdapId
        if (validationType.equals(USER_VALIDATION_TYPE))
        {
            changedParticipant = assignedObjectWithCopyParticipants.getParticipants().stream()
                    .filter(p -> p.getParticipantLdapId().equals(newObj)
                            && p.getParticipantType().equals(copyParticipant.getParticipantType()))
                    .findFirst().orElse(null);
        }
        else // else validation type is "type" and check for participantType
        {
            changedParticipant = assignedObjectWithCopyParticipants.getParticipants().stream()
                    .filter(p -> p.getParticipantType().equals(newObj)
                            && p.getParticipantLdapId().equals(copyParticipant.getParticipantLdapId()))
                    .findFirst().orElse(null);
        }
        if (changedParticipant == null) // check if the changed participant is updated and exists
        {
            return true; // changed participand does not exists so the validationType should be changed
        }
        return false;
    }

    private void validateParticipantsDeletable(AcmAssignedObject assignedObjectWithCopyParticipants,
            List<AcmParticipant> originalParticipants, int index)
    {
        if (!assignedObjectWithCopyParticipants.getParticipants().get(index).isDeletable())
        {
            return;
        }
        int copyParticipantsSize = assignedObjectWithCopyParticipants.getParticipants().size();
        List<AcmParticipant> participantsWithDeletedParticipant = assignedObjectWithCopyParticipants.getParticipants();
        AcmParticipant deletedParticipant = participantsWithDeletedParticipant.remove(index);
        assignedObjectWithCopyParticipants.setParticipants(participantsWithDeletedParticipant);
        Boolean changeParticipantDeletable = false;
        getAssignmentBusinessRule().applyRules(assignedObjectWithCopyParticipants);

        if (assignedObjectWithCopyParticipants.getParticipants().size() != copyParticipantsSize - 1)
        {
            changeParticipantDeletable = true;
        }
        else
        {
            AcmParticipant changedParticipant = assignedObjectWithCopyParticipants.getParticipants().stream()
                    .filter(p -> p.getParticipantType().equals(deletedParticipant.getParticipantType())
                            && p.getParticipantLdapId().equals(deletedParticipant.getParticipantLdapId()))
                    .findFirst().orElse(null);
            if (changedParticipant != null)
            {
                changeParticipantDeletable = true;
            }
        }
        if (changeParticipantDeletable)
        {
            originalParticipants.stream()
                    .filter(origParticipant -> origParticipant.getParticipantLdapId().equals(deletedParticipant.getParticipantLdapId())
                            && origParticipant.getParticipantType().equals(deletedParticipant.getParticipantType()))
                    .forEach(origParticipant -> {
                        origParticipant.setDeletable(false);
                    });
        }
    }

    // Apply rules to participants and return copy list
    private List<AcmParticipant> getParticipantsCopyWithAppliedAssignmentRules(AcmAssignedObject assignedObject,
            List<AcmParticipant> originalParticipants)
    {
        List<AcmParticipant> copyParticipants = new ArrayList<>();
        originalParticipants.forEach(participant -> copyParticipants.add(AcmParticipant.createRulesTestParticipant(participant)));
        assignedObject.setParticipants(copyParticipants);

        // apply the Drools rules on the copy so new participants are added in the list
        getAssignmentBusinessRule().applyRules(assignedObject);

        return copyParticipants;
    }

    public AcmAssignedObjectBusinessRule getAssignmentBusinessRule()
    {
        return assignmentBusinessRule;
    }

    public void setAssignmentBusinessRule(AcmAssignedObjectBusinessRule assignmentBusinessRule)
    {
        this.assignmentBusinessRule = assignmentBusinessRule;
    }

    public AcmDataService getSpringAcmDataService()
    {
        return springAcmDataService;
    }

    public void setSpringAcmDataService(AcmDataService springAcmDataService)
    {
        this.springAcmDataService = springAcmDataService;
    }
}
