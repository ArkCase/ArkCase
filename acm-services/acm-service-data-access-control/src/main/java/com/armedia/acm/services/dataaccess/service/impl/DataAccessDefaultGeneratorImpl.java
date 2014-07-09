package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.AcmObjectState;
import com.armedia.acm.core.AcmObjectType;
import com.armedia.acm.core.AcmParticipantType;
import com.armedia.acm.core.AcmUserAction;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlDefault;
import com.armedia.acm.services.dataaccess.model.enums.AccessControlDecision;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * This class produces the initial default data access control entries from the AcmApplication object.
 */
public class DataAccessDefaultGeneratorImpl
{
    private List<String> crudOperations;
    private String allUsersIndicator;
    private AccessControlDecision allUsersAccess;
    private Boolean allUserAccessDiscretionaryUpdateAllowed;
    private String generatorUserName;
    private AccessControlDecision participantAccess;
    private Boolean participantAccessDiscretionaryUpdateAllowed;

    public List<AcmAccessControlDefault> generateDefaultAccessFromApplication(AcmApplication acmApplicaton)
    {
        Preconditions.checkNotNull(acmApplicaton, "acmApplication cannot be null");
        Preconditions.checkArgument(acmApplicaton.getBusinessObjects() != null, "Business objects must be defined in " +
                "the ACM application");
        Preconditions.checkArgument(!acmApplicaton.getBusinessObjects().isEmpty(), "Business objects must be defined in " +
                "the ACM application");
        Preconditions.checkState(getCrudOperations() != null, "CRUD operations must be defined in this generator");
        Preconditions.checkState(!getCrudOperations().isEmpty(), "CRUD operations must be defined in this generator");
        Preconditions.checkState(getAllUsersIndicator() != null, "Symbol for 'all other users' must be defined in this generator");
        Preconditions.checkState(getAllUserAccessDiscretionaryUpdateAllowed() != null,
                "Flag indicating whether 'all other users' access can be updated must be defined in this generator");
        Preconditions.checkState(getAllUsersAccess() != null,
                "The access decision for 'all other users' must be defined in this generator");
        Preconditions.checkState(getGeneratorUserName() != null, "The user ID for this generator must be defined.");
        Preconditions.checkState(getParticipantAccess() != null, "The access decision for participants must be defined.");
        Preconditions.checkState(getParticipantAccessDiscretionaryUpdateAllowed() != null,
                "Flag indicating whether participant access can be updated must be defined.");

        List<AcmAccessControlDefault> defaultAccessors = new ArrayList<>();

        addDefaultCrudAccessors(acmApplicaton.getBusinessObjects(), defaultAccessors);

        addParticipantCrudAccessors(acmApplicaton.getBusinessObjects(), defaultAccessors);

        addParticipantActionAccessors(acmApplicaton.getBusinessObjects(), defaultAccessors);

        return defaultAccessors;
    }

    private void addParticipantActionAccessors(List<AcmObjectType> businessObjects, List<AcmAccessControlDefault> defaultAccessors)
    {
        Preconditions.checkNotNull(defaultAccessors, "defaultAccessors cannot be null");

        for (AcmObjectType objectType : businessObjects)
        {
            if ( objectType.getParticipantTypes() == null || objectType.getParticipantTypes().isEmpty() )
            {
                // no participants defined, so nothing to add to the default ACL.
                continue;
            }

            Preconditions.checkArgument(!objectType.getStates().isEmpty(), "Each object type must have at least one state");

            for (AcmObjectState state : objectType.getStates())
            {
                if ( state.getValidActions() == null || state.getValidActions().isEmpty() )
                {
                    // no actions defined, so nothing to add to the default ACL
                    continue;
                }

                for (AcmUserAction action : state.getValidActions() )
                {
                    for (AcmParticipantType participantType : objectType.getParticipantTypes())
                    {
                        AcmAccessControlDefault defaultAccessor = buildAcmAccessControlDefault(objectType, state, action.getActionName());

                        defaultAccessor.setAccessorType(participantType.getName());
                        defaultAccessor.setAccessDecision(getParticipantAccess().name());
                        defaultAccessor.setAllowDiscretionaryUpdate(getParticipantAccessDiscretionaryUpdateAllowed());

                        defaultAccessors.add(defaultAccessor);
                    }
                }
            }
        }
    }

    private void addParticipantCrudAccessors(List<AcmObjectType> businessObjects, List<AcmAccessControlDefault> defaultAccessors)
    {
        Preconditions.checkNotNull(defaultAccessors, "defaultAccessors cannot be null");

        for (AcmObjectType objectType : businessObjects)
        {
            if ( objectType.getParticipantTypes() == null || objectType.getParticipantTypes().isEmpty() )
            {
                // no participants defined, so nothing to add to the default ACL.
                continue;
            }

            Preconditions.checkArgument(!objectType.getStates().isEmpty(), "Each object type must have at least one state");

            for (AcmParticipantType participantType : objectType.getParticipantTypes())
            {
                for (String crudOperation : getCrudOperations())
                {
                    for (AcmObjectState state : objectType.getStates())
                    {
                        AcmAccessControlDefault defaultAccessor = buildAcmAccessControlDefault(objectType, state, crudOperation);

                        defaultAccessor.setAccessorType(participantType.getName());
                        defaultAccessor.setAccessDecision(getParticipantAccess().name());
                        defaultAccessor.setAllowDiscretionaryUpdate(getParticipantAccessDiscretionaryUpdateAllowed());

                        defaultAccessors.add(defaultAccessor);
                    }
                }
            }
        }
    }

    private AcmAccessControlDefault buildAcmAccessControlDefault(AcmObjectType objectType, AcmObjectState state, String crudOperation)
    {
        AcmAccessControlDefault defaultAccessor = new AcmAccessControlDefault();
        defaultAccessor.setObjectType(objectType.getName());
        defaultAccessor.setObjectState(state.getName());
        defaultAccessor.setAccessLevel(crudOperation);
        defaultAccessor.setCreator(getGeneratorUserName());
        defaultAccessor.setModifier(getGeneratorUserName());
        return defaultAccessor;
    }

    private void addDefaultCrudAccessors(List<AcmObjectType> businessObjects, List<AcmAccessControlDefault> defaultAccessors)
    {
        Preconditions.checkNotNull(defaultAccessors, "defaultAccessors cannot be null");

        for (AcmObjectType objectType : businessObjects)
        {
            Preconditions.checkArgument(!objectType.getStates().isEmpty(), "Each object type must have at least one state");

            for (AcmObjectState state : objectType.getStates())
            {
                for (String crudOperation : getCrudOperations())
                {
                    AcmAccessControlDefault defaultAccessor = buildAcmAccessControlDefault(objectType, state, crudOperation);
                    defaultAccessor.setAccessorType(getAllUsersIndicator());
                    defaultAccessor.setAccessDecision(getAllUsersAccess().name());
                    defaultAccessor.setAllowDiscretionaryUpdate(isAllUserAccessDiscretionaryUpdateAllowed());
                    defaultAccessors.add(defaultAccessor);
                }
            }
        }
    }

    public List<String> getCrudOperations()
    {
        return crudOperations;
    }

    public void setCrudOperations(List<String> crudOperations)
    {
        this.crudOperations = crudOperations;
    }

    public String getAllUsersIndicator()
    {
        return allUsersIndicator;
    }

    public void setAllUsersIndicator(String allUsersIndicator)
    {
        this.allUsersIndicator = allUsersIndicator;
    }

    public AccessControlDecision getAllUsersAccess()
    {
        return allUsersAccess;
    }

    public void setAllUsersAccess(AccessControlDecision allUsersAccess)
    {
        this.allUsersAccess = allUsersAccess;
    }

    public Boolean isAllUserAccessDiscretionaryUpdateAllowed()
    {
        return allUserAccessDiscretionaryUpdateAllowed;
    }

    public Boolean getAllUserAccessDiscretionaryUpdateAllowed()
    {
        return allUserAccessDiscretionaryUpdateAllowed;
    }

    public void setAllUserAccessDiscretionaryUpdateAllowed(Boolean allUserAccessDiscretionaryUpdateAllowed)
    {
        this.allUserAccessDiscretionaryUpdateAllowed = allUserAccessDiscretionaryUpdateAllowed;
    }

    public String getGeneratorUserName()
    {
        return generatorUserName;
    }

    public void setGeneratorUserName(String generatorUserName)
    {
        this.generatorUserName = generatorUserName;
    }

    public AccessControlDecision getParticipantAccess()
    {
        return participantAccess;
    }

    public void setParticipantAccess(AccessControlDecision participantAccess)
    {
        this.participantAccess = participantAccess;
    }

    public Boolean getParticipantAccessDiscretionaryUpdateAllowed()
    {
        return participantAccessDiscretionaryUpdateAllowed;
    }

    public void setParticipantAccessDiscretionaryUpdateAllowed(Boolean participantAccessDiscretionaryUpdateAllowed)
    {
        this.participantAccessDiscretionaryUpdateAllowed = participantAccessDiscretionaryUpdateAllowed;
    }
}
