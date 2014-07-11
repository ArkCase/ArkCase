package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.AcmObjectState;
import com.armedia.acm.core.AcmObjectType;
import com.armedia.acm.core.AcmParticipantType;
import com.armedia.acm.core.AcmUserAction;
import com.armedia.acm.core.enums.AcmParticipantTypes;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlDefault;
import com.armedia.acm.services.dataaccess.model.enums.AccessControlDecision;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by armdev on 7/9/14.
 */
public class DataAccessDefaultGeneratorImplTest
{
    private DataAccessDefaultGeneratorImpl unit;

    @Before
    public void setUp() throws Exception
    {
        unit = new DataAccessDefaultGeneratorImpl();

        unit.setCrudOperations(Arrays.asList("read", "update", "delete"));
        unit.setAllUserAccessDiscretionaryUpdateAllowed(true);
        unit.setAllUsersAccess(AccessControlDecision.DENY);
        unit.setAllUsersIndicator("*");
        unit.setGeneratorUserName("user");
        unit.setParticipantAccess(AccessControlDecision.GRANT);
        unit.setParticipantAccessDiscretionaryUpdateAllowed(false);
    }

    @Test
    public void generateDefaultAccessFromApplication_noBusinessObjectsThrowsIllegalArg() throws Exception
    {
        AcmApplication app = new AcmApplication();
        try
        {
            unit.generateDefaultAccessFromApplication(app);
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // ok - expected exception
        }
    }

    @Test
    public void generateDefaultAccessFromApplication_noStatesThrowsNullPointer() throws Exception
    {
        AcmApplication app = new AcmApplication();
        AcmObjectType objectType = new AcmObjectType();
        app.setBusinessObjects(Arrays.asList(objectType));

        try
        {
            unit.generateDefaultAccessFromApplication(app);
            fail("should have null pointer exception");
        }
        catch (NullPointerException e)
        {
            // ok - expected
        }
    }

    @Test
    public void generateDefaultAccessFromApplication_defaultAccessors() throws Exception
    {
        // always 1 entry per object type per crud operation per state.  This entry is the default access for people not
        // otherwise on the ACL.
        AcmApplication app = new AcmApplication();

        AcmObjectType objectType = new AcmObjectType();
        objectType.setName("objectType");

        AcmObjectState state = new AcmObjectState();
        state.setName("state");
        objectType.setStates(Arrays.asList(state));

        app.setBusinessObjects(Arrays.asList(objectType));

        int expectedSize = unit.getCrudOperations().size() * app.getBusinessObjects().size() * objectType.getStates().size();

        List<AcmAccessControlDefault> defaultEntries = unit.generateDefaultAccessFromApplication(app);
        assertEquals(expectedSize, defaultEntries.size());

        for ( AcmAccessControlDefault accessControlDefault : defaultEntries )
        {
            assertEquals(unit.getAllUserAccessDiscretionaryUpdateAllowed(), accessControlDefault.getAllowDiscretionaryUpdate());
            assertEquals(unit.getAllUsersAccess().name(), accessControlDefault.getAccessDecision());
            assertEquals(unit.getAllUsersIndicator(), accessControlDefault.getAccessorType());
            assertEquals(unit.getGeneratorUserName(), accessControlDefault.getCreator());
            assertEquals(unit.getGeneratorUserName(), accessControlDefault.getModifier());
        }
    }

    @Test
    public void generateDefaultAccessFromApplication_participantCrudAccessors() throws Exception
    {
        // 1 additional entry per object type per crud operation per state per participant.  This entry is the default
        // access for participants named on the ACL.
        AcmApplication app = new AcmApplication();

        AcmObjectType objectType = new AcmObjectType();
        objectType.setName("objectType");

        AcmObjectState state = new AcmObjectState();
        state.setName("state");
        objectType.setStates(Arrays.asList(state));

        AcmParticipantType partOne = new AcmParticipantType();
        partOne.setName("partOne");
        partOne.setRequiredOnACL(true);
        partOne.setType(AcmParticipantTypes.SINGLE_USER);

        AcmParticipantType partTwo = new AcmParticipantType();
        partTwo.setName("partTwo");
        partTwo.setRequiredOnACL(true);
        partTwo.setType(AcmParticipantTypes.SINGLE_USER);

        objectType.setParticipantTypes(Arrays.asList(partOne, partTwo));

        app.setBusinessObjects(Arrays.asList(objectType));

        int expectedDefaultAccessors = unit.getCrudOperations().size() * app.getBusinessObjects().size() *
                objectType.getStates().size();

        int expectedParticipantCrudAccessors = expectedDefaultAccessors * objectType.getParticipantTypes().size();

        int expectedSize = expectedDefaultAccessors + expectedParticipantCrudAccessors;

        List<AcmAccessControlDefault> defaultEntries = unit.generateDefaultAccessFromApplication(app);

        assertEquals(expectedSize, defaultEntries.size());

        List<String> participantNames = Arrays.asList(partOne.getName(), partTwo.getName());

        for ( AcmAccessControlDefault accessControlDefault : defaultEntries )
        {
            if ( accessControlDefault.getAccessorType().equals(unit.getAllUsersIndicator() ))
            {
                assertEquals(unit.getAllUserAccessDiscretionaryUpdateAllowed(), accessControlDefault.getAllowDiscretionaryUpdate());
                assertEquals(unit.getAllUsersAccess().name(), accessControlDefault.getAccessDecision());
                assertEquals(unit.getAllUsersIndicator(), accessControlDefault.getAccessorType());
            }
            else
            {
                assertEquals(unit.getParticipantAccessDiscretionaryUpdateAllowed(), accessControlDefault.getAllowDiscretionaryUpdate());
                assertEquals(unit.getParticipantAccess().name(), accessControlDefault.getAccessDecision());
                assertTrue(participantNames.contains(accessControlDefault.getAccessorType()));
            }
            assertEquals(unit.getGeneratorUserName(), accessControlDefault.getCreator());
            assertEquals(unit.getGeneratorUserName(), accessControlDefault.getModifier());
        }
    }

    @Test
    public void generateDefaultAccessFromApplication_participantActionAccessors() throws Exception
    {
        // 1 additional entry per participant per action.
        AcmApplication app = new AcmApplication();

        AcmObjectType objectType = new AcmObjectType();
        objectType.setName("objectType");

        AcmObjectState state = new AcmObjectState();
        state.setName("state");
        objectType.setStates(Arrays.asList(state));

        AcmUserAction actionOne = new AcmUserAction();
        actionOne.setActionName("actionOne");

        AcmUserAction actionTwo = new AcmUserAction();
        actionTwo.setActionName("actionTwo");

        state.setValidActions(Arrays.asList(actionOne, actionTwo));

        AcmParticipantType partOne = new AcmParticipantType();
        partOne.setName("partOne");
        partOne.setRequiredOnACL(true);
        partOne.setType(AcmParticipantTypes.SINGLE_USER);

        AcmParticipantType partTwo = new AcmParticipantType();
        partTwo.setName("partTwo");
        partTwo.setRequiredOnACL(true);
        partTwo.setType(AcmParticipantTypes.SINGLE_USER);

        objectType.setParticipantTypes(Arrays.asList(partOne, partTwo));

        app.setBusinessObjects(Arrays.asList(objectType));

        int expectedDefaultAccessors = unit.getCrudOperations().size() * app.getBusinessObjects().size() *
                objectType.getStates().size();

        int expectedParticipantCrudAccessors = expectedDefaultAccessors * objectType.getParticipantTypes().size();

        int expectedActionAccessors = objectType.getParticipantTypes().size() * state.getValidActions().size();

        int expectedSize = expectedDefaultAccessors + expectedParticipantCrudAccessors + expectedActionAccessors;

        List<AcmAccessControlDefault> defaultEntries = unit.generateDefaultAccessFromApplication(app);

        assertEquals(expectedSize, defaultEntries.size());

        List<String> participantNames = Arrays.asList(partOne.getName(), partTwo.getName());

        for ( AcmAccessControlDefault accessControlDefault : defaultEntries )
        {
            if ( accessControlDefault.getAccessorType().equals(unit.getAllUsersIndicator() ))
            {
                assertEquals(unit.getAllUserAccessDiscretionaryUpdateAllowed(), accessControlDefault.getAllowDiscretionaryUpdate());
                assertEquals(unit.getAllUsersAccess().name(), accessControlDefault.getAccessDecision());
                assertEquals(unit.getAllUsersIndicator(), accessControlDefault.getAccessorType());
            }
            else
            {
                assertEquals(unit.getParticipantAccessDiscretionaryUpdateAllowed(), accessControlDefault.getAllowDiscretionaryUpdate());
                assertEquals(unit.getParticipantAccess().name(), accessControlDefault.getAccessDecision());
                assertTrue(participantNames.contains(accessControlDefault.getAccessorType()));
            }
            assertEquals(unit.getGeneratorUserName(), accessControlDefault.getCreator());
            assertEquals(unit.getGeneratorUserName(), accessControlDefault.getModifier());
        }
    }

}
