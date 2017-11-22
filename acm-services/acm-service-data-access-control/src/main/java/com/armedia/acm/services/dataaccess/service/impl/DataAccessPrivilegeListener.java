package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.data.AcmBeforeInsertListener;
import com.armedia.acm.data.AcmBeforeUpdateListener;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;
import com.armedia.acm.services.participants.model.CheckParticipantListModel;
import com.armedia.acm.services.participants.service.ParticipantsBusinessRule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by armdev on 1/6/15.
 */
public class DataAccessPrivilegeListener implements AcmBeforeUpdateListener, AcmBeforeInsertListener
{
    private final transient Logger log = LoggerFactory.getLogger(getClass());
    private AcmAssignedObjectBusinessRule assignmentBusinessRule;
    private AcmAssignedObjectBusinessRule accessControlBusinessRule;
    private ParticipantsBusinessRule participantsBusinessRule;
    private EcmFileParticipantService fileParticipantService;

    @Override
    public void beforeInsert(Object object) throws AcmAccessControlException
    {
        log.trace("inserted: " + object);
        applyAssignmentAndAccessRules(object, null);
    }

    @Override
    public void beforeUpdate(Object object, Object originalObject) throws AcmAccessControlException
    {
        log.trace("updated: " + object);
        applyAssignmentAndAccessRules(object, originalObject);
    }

    public void applyAssignmentAndAccessRules(Object obj, Object originalObject) throws AcmAccessControlException
    {
        if (obj instanceof AcmAssignedObject)
        {
            AcmAssignedObject assignedObject = (AcmAssignedObject) obj;
            applyAssignRules(assignedObject);
            validateParticipantAssignmentRules(assignedObject);
            applyDataAccessRules(assignedObject);
            applyAssignRulesToContainer(obj, originalObject);
            updateParentPointers(assignedObject);
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

    private void applyAssignRulesToContainer(Object obj, Object originalObject) throws AcmAccessControlException
    {
        // set container participants on a new object only
        // when updating an object the updater must call the inheritParticipantsFromAssignedObject, because the
        // replaceChildrenParticipant is transient and it's always false here
        if (obj instanceof AcmAssignedObject && obj instanceof AcmContainerEntity && originalObject == null)
        {
            ((AcmAssignedObject) obj).getParticipants().forEach(participant -> participant.setReplaceChildrenParticipant(true));
            getFileParticipantService().inheritParticipantsFromAssignedObject(
                    ((AcmAssignedObject) obj).getParticipants(),
                    new ArrayList<>(), ((AcmContainerEntity) obj).getContainer());

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

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
    }

    public ParticipantsBusinessRule getParticipantsBusinessRule()
    {
        return participantsBusinessRule;
    }

    public void setParticipantsBusinessRule(ParticipantsBusinessRule participantsRule)
    {
        this.participantsBusinessRule = participantsRule;
    }
}
