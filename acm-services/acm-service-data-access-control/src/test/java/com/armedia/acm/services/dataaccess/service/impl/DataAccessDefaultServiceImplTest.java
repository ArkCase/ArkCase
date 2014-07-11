package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.dataaccess.dao.AcmAccessControlDefaultDao;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlDefault;
import com.armedia.acm.services.dataaccess.model.enums.DefaultAccessControlSavePolicy;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.Date;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Created by armdev on 7/11/14.
 */
public class DataAccessDefaultServiceImplTest extends EasyMockSupport
{
    private DataAccessDefaultServiceImpl unit;
    private AcmAccessControlDefaultDao mockAccessControlDefaultDao;
    private Authentication mockAuthentication;

    @Before
    public void setUp() throws Exception
    {
        mockAccessControlDefaultDao = createMock(AcmAccessControlDefaultDao.class);
        mockAuthentication = createMock(Authentication.class);

        unit = new DataAccessDefaultServiceImpl();

        unit.setAccessControlDefaultDao(mockAccessControlDefaultDao);
    }

    @Test
    public void save_happyPath() throws Exception
    {
        AcmAccessControlDefault in = new AcmAccessControlDefault();
        in.setId(500L);
        in.setAllowDiscretionaryUpdate(true);
        in.setModifier("modifier");
        in.setAccessDecision("GRANT");
        in.setModified(new Date());
        in.setAccessLevel("accessLevel");
        in.setCreated(new Date());
        in.setCreator("creator");
        in.setObjectState("state");
        in.setObjectType("type");
        in.setAccessorType("accessor");

        AcmAccessControlDefault existing = new AcmAccessControlDefault();
        existing.setAccessDecision("DENY");
        existing.setId(in.getId());
        existing.setAllowDiscretionaryUpdate(in.getAllowDiscretionaryUpdate());
        existing.setModifier(in.getModifier());
        existing.setModified(in.getModified());
        existing.setAccessLevel(in.getAccessLevel());
        existing.setCreated(in.getCreated());
        existing.setCreator(in.getCreator());
        existing.setObjectState(in.getObjectState());
        existing.setObjectType(in.getObjectType());

        String updateUser = "updateUser";

        Capture<AcmAccessControlDefault> toSave = new Capture<>();

        expect(mockAuthentication.getName()).andReturn(updateUser);

        expect(mockAccessControlDefaultDao.find(AcmAccessControlDefault.class, in.getId())).andReturn(existing);

        expect(mockAccessControlDefaultDao.save(capture(toSave), eq(DefaultAccessControlSavePolicy.OVERWRITE_EXISTING)))
                .andReturn(existing);

        replayAll();

        unit.save(in.getId(), in, mockAuthentication);

        verifyAll();

        assertEquals(in.getAccessDecision(), toSave.getValue().getAccessDecision());
        assertEquals(updateUser, toSave.getValue().getModifier());
    }

    @Test
    public void save_keyFieldsShouldNotBeUpdated() throws Exception
    {
        AcmAccessControlDefault in = new AcmAccessControlDefault();
        in.setId(500L);
        in.setAllowDiscretionaryUpdate(true);
        in.setModifier("modifier");
        in.setAccessDecision("GRANT");
        in.setModified(new Date());
        in.setAccessLevel("accessLevel");
        in.setCreated(new Date());
        in.setCreator("creator");
        in.setObjectState("state");
        in.setObjectType("type");
        in.setAccessorType("accessor");

        AcmAccessControlDefault existing = new AcmAccessControlDefault();
        existing.setAccessDecision("DENY");
        existing.setId(in.getId());
        existing.setAllowDiscretionaryUpdate(in.getAllowDiscretionaryUpdate());
        existing.setModifier(in.getModifier());
        existing.setModified(in.getModified());
        existing.setAccessLevel("anotherAccessLevel");
        existing.setCreated(in.getCreated());
        existing.setCreator(in.getCreator());
        existing.setObjectState("anotherState");
        existing.setObjectType("anotherType");
        existing.setAccessorType("anotherAccessor");

        String updateUser = "updateUser";

        Capture<AcmAccessControlDefault> toSave = new Capture<>();

        expect(mockAuthentication.getName()).andReturn(updateUser);

        expect(mockAccessControlDefaultDao.find(AcmAccessControlDefault.class, in.getId())).andReturn(existing);

        expect(mockAccessControlDefaultDao.save(capture(toSave), eq(DefaultAccessControlSavePolicy.OVERWRITE_EXISTING)))
                .andReturn(existing);

        replayAll();

        unit.save(in.getId(), in, mockAuthentication);

        verifyAll();

        assertEquals(in.getAccessDecision(), toSave.getValue().getAccessDecision());
        assertEquals(updateUser, toSave.getValue().getModifier());

        assertEquals(existing.getAccessLevel(), toSave.getValue().getAccessLevel());
        assertEquals(existing.getAccessorType(), toSave.getValue().getAccessorType());
        assertEquals(existing.getObjectState(), toSave.getValue().getObjectState());
        assertEquals(existing.getObjectType(), toSave.getValue().getObjectType());
    }

    @Test
    public void save_requestedDefaultAccessDoesNotExist() throws Exception
    {
        AcmAccessControlDefault in = new AcmAccessControlDefault();
        in.setId(500L);

        expect(mockAccessControlDefaultDao.find(AcmAccessControlDefault.class, in.getId())).andReturn(null);

        replayAll();

        try
        {
            unit.save(in.getId(), in, mockAuthentication);
            fail("Should have thrown an exception");
        }
        catch (AcmUserActionFailedException e)
        {
            // ok -- expected
        }

        verifyAll();
    }
}
