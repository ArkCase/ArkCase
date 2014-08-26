package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.services.dataaccess.dao.AcmAccessControlEntryDao;
import com.armedia.acm.services.dataaccess.model.AcmAccess;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlDefault;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlEntry;
import com.armedia.acm.services.dataaccess.model.enums.EntryAccessControlSavePolicy;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class DataAccessEntryServiceImplTest extends EasyMockSupport {
    private DataAccessEntryServiceImpl unit;
    private AcmAccessControlEntryDao mockAccessControlEntryDao;
    private Authentication mockAuthentication;

    @Before
    public void setUp() throws Exception {
        mockAccessControlEntryDao = createMock(AcmAccessControlEntryDao.class);
        mockAuthentication = createMock(Authentication.class);
        unit = new DataAccessEntryServiceImpl();
        unit.setAccessControlEntryDao(mockAccessControlEntryDao);
    }

    @Test
    public void save_happyPath() throws Exception {
        AcmAccessControlEntry in = new AcmAccessControlEntry();
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

        AcmAccessControlEntry existing = new AcmAccessControlEntry();
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

        Capture<AcmAccessControlEntry> toSave = new Capture<>();

        expect(mockAuthentication.getName()).andReturn(updateUser);

        expect(mockAccessControlEntryDao.find(in.getId())).andReturn(existing);

        expect(mockAccessControlEntryDao.save(capture(toSave), eq(EntryAccessControlSavePolicy.OVERWRITE_EXISTING)))
                .andReturn(existing);

        replayAll();

        unit.save(in.getId(), in, mockAuthentication);

        verifyAll();

        assertEquals(in.getAccessDecision(), toSave.getValue().getAccessDecision());
        assertEquals(updateUser, toSave.getValue().getModifier());
    }

    @Test
    public void save_keyFieldsShouldNotBeUpdated() throws Exception {
        AcmAccessControlEntry in = new AcmAccessControlEntry();
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

        AcmAccessControlEntry existing = new AcmAccessControlEntry();
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

        Capture<AcmAccessControlEntry> toSave = new Capture<>();

        expect(mockAuthentication.getName()).andReturn(updateUser);

        expect(mockAccessControlEntryDao.find(in.getId())).andReturn(existing);

        expect(mockAccessControlEntryDao.save(capture(toSave), eq(EntryAccessControlSavePolicy.OVERWRITE_EXISTING)))
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
    public void getAcmReadAccessAsExpected() {
        List<AcmAccessControlEntry> results = new ArrayList<AcmAccessControlEntry>();
        AcmAccessControlEntry entry = new AcmAccessControlEntry();
        entry.setId(1L);
        entry.setAccessDecision("GRANT");
        entry.setAccessorId("ALLOW-ROLE");
        results.add(entry);

        entry = new AcmAccessControlEntry();
        entry.setId(2L);
        entry.setAccessDecision("DENY");
        entry.setAccessorId("DENY-ROLE");
        results.add(entry);

        expect(mockAccessControlEntryDao.findByFields(anyLong(), anyString(), anyString(), anyString())).andReturn(results);

        replayAll();

        AcmAccess acmAccess = unit.getAcmReadAccess(1L, "TASK", "DRAFT");

        verifyAll();

        assertTrue(acmAccess.getDenyAcls().contains("DENY-ROLE"));
        assertTrue(acmAccess.getAllowAcls().contains("ALLOW-ROLE"));
    }
}
