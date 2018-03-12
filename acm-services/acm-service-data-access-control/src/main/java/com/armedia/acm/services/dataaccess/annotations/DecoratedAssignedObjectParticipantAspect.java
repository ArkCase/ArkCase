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
            } // check if the returned Object is a list of AcmParticipants
            else if (List.class.isAssignableFrom(ret.getClass()) && ((List) ret).size() > 0)
            {

                // check if the instance of the annotation exists
                if (decoratedAssignedObjectParticipants != null && AcmParticipant.class.isAssignableFrom(((List) ret).get(0).getClass()))
                {
                    // Get function parameters and annotation parameters and
                    // map them to objectType and objectId so we can get participants parent AcmAssignableObject
                    Object[] args = pjp.getArgs();
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
                    AcmObject entity = dao.find(((Number) objectId).longValue());
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

    private AcmAssignedObject decorateAssignableObjectParticipants(AcmAssignedObject assignedObject) throws Exception
    {
        List<AcmParticipant> originalParticipants = assignedObject.getParticipants();

        // get a new list of participants, not to change the original participants references
        List<AcmParticipant> copyParticipants = getParticipantsCopyWithAppliedAssignmentRules(assignedObject, originalParticipants);

        // check for editable
        for (int i = 0; i < copyParticipants.size(); i++)
        {
            // Decorate editableUserParticipants
            validateParticipantsEditableUser(assignedObject, originalParticipants, i);
            // reset copy participants
            copyParticipants = getParticipantsCopyWithAppliedAssignmentRules(assignedObject, originalParticipants);
            // Decorate editableTypeParticipants
            validateParticipantsEditableType(assignedObject, originalParticipants, i);
            // reset copy participants
            copyParticipants = getParticipantsCopyWithAppliedAssignmentRules(assignedObject, originalParticipants);
            // Decorate deletableParticipants
            validateParticipantsDeletable(assignedObject, originalParticipants, i);
            // reset copy participants
            copyParticipants = getParticipantsCopyWithAppliedAssignmentRules(assignedObject, originalParticipants);
        }

        assignedObject.setParticipants(originalParticipants);
        return assignedObject;
    }

    private void validateParticipantsEditable(AcmAssignedObject assignedObjectWithCopyParticipants,
            List<AcmParticipant> originalParticipants, int index, String validationType)
    {
        int copyParticipantsSize = assignedObjectWithCopyParticipants.getParticipants().size();
        AcmParticipant copyParticipant = assignedObjectWithCopyParticipants.getParticipants().get(index);

        String oldObj = "";
        String newObj = "";
        Boolean changeParticipantEditable = false;

        if (validationType.equals("user"))
        {
            newObj = UUID.randomUUID().toString();
            oldObj = copyParticipant.getParticipantLdapId();
            copyParticipant.setParticipantLdapId(newObj);
        }
        else if (validationType.equals("type"))
        {
            newObj = UUID.randomUUID().toString();
            oldObj = copyParticipant.getParticipantType();
            copyParticipant.setParticipantType(newObj);
        }
        String finalNewObj = newObj;
        String finalOldObj = oldObj;
        getAssignmentBusinessRule().applyRules(assignedObjectWithCopyParticipants);

        if (assignedObjectWithCopyParticipants.getParticipants().size() != copyParticipantsSize)
        { // check if size is equals and no new participant is added
          // set editable and deletable flags on the original participants references
            changeParticipantEditable = true;
        }
        else
        {
            AcmParticipant changedParticipant = null;
            if (validationType.equals("user"))
            {
                changedParticipant = assignedObjectWithCopyParticipants.getParticipants().stream()
                        .filter(p -> p.getParticipantLdapId().equals(finalNewObj)
                                && p.getParticipantType().equals(copyParticipant.getParticipantType()))
                        .findFirst().orElse(null);
            }
            else
            {
                changedParticipant = assignedObjectWithCopyParticipants.getParticipants().stream()
                        .filter(p -> p.getParticipantType().equals(finalNewObj)
                                && p.getParticipantLdapId().equals(copyParticipant.getParticipantLdapId()))
                        .findFirst().orElse(null);
            }
            if (changedParticipant == null)
            { // check if the changed participant is updated and exists
              // set editable and deletable flags on the original participants references
                changeParticipantEditable = true;
            }
        }
        if (changeParticipantEditable)
        {
            originalParticipants.stream()
                    .filter(origParticipant -> validationType.equals("user") && origParticipant.getParticipantLdapId().equals(finalOldObj)
                            && origParticipant.getParticipantType().equals(copyParticipant.getParticipantType())
                            || validationType.equals("type")
                                    && origParticipant.getParticipantLdapId().equals(copyParticipant.getParticipantLdapId())
                                    && origParticipant.getParticipantType().equals(finalOldObj))
                    .forEach(origParticipant -> {
                        if (validationType.equals("user"))
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

    private void validateParticipantsEditableUser(AcmAssignedObject assignedObjectWithCopyParticipants,
            List<AcmParticipant> originalParticipants, int index)
    {
        validateParticipantsEditable(assignedObjectWithCopyParticipants, originalParticipants, index, "user");
    }

    private void validateParticipantsEditableType(AcmAssignedObject assignedObjectWithCopyParticipants,
            List<AcmParticipant> originalParticipants, int index)
    {
        validateParticipantsEditable(assignedObjectWithCopyParticipants, originalParticipants, index, "type");
    }

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
