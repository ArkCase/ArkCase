package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.data.AcmBeforeInsertListener;
import com.armedia.acm.data.AcmBeforeUpdateListener;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by armdev on 1/6/15.
 */
public class DataAccessPrivilegeListener implements AcmBeforeUpdateListener, AcmBeforeInsertListener
{
    private final transient Logger log = LoggerFactory.getLogger(getClass());
    private AcmAssignedObjectBusinessRule assignmentBusinessRule;
    private AcmAssignedObjectBusinessRule accessControlBusinessRule;

    @Override
    public void beforeInsert(Object object)
    {
        log.debug("inserted: " + object);
        applyAssignmentAndAccessRules(object);
    }

    @Override
    public void beforeUpdate(Object object)
    {
        log.debug("updated: " + object);
        applyAssignmentAndAccessRules(object);
    }

    public void applyAssignmentAndAccessRules(Object obj)
    {
        if ( obj instanceof AcmAssignedObject )
        {
            AcmAssignedObject assignedObject = (AcmAssignedObject) obj;
            applyAssignRules(assignedObject);
            applyDataAccessRules(assignedObject);
            updateParentPointers(assignedObject);
        }
    }

    private void updateParentPointers(AcmAssignedObject assignedObject)
    {
        for ( AcmParticipant participant : assignedObject.getParticipants() )
        {
            participant.setObjectType(assignedObject.getObjectType());
            participant.setObjectId(assignedObject.getId());

            log.debug("participant '" + participant.getParticipantLdapId() + "'");
            for (AcmParticipantPrivilege priv : participant.getPrivileges() )
            {
                log.debug("\t privilege: " + priv.getAccessType() + " " + priv.getObjectAction());
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
}
