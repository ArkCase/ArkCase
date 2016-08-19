package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleMessage;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * Created by armdev on 3/11/15.
 */
public class EcmFileServiceImplTest extends EasyMockSupport
{
    private EcmFileServiceImpl unit;

    private MuleContextManager mockMuleContextManager;
    private MuleMessage mockMuleMessage;
    private CmisObject mockCmisObject;

    @Before
    public void setUp() throws Exception
    {
        unit = new EcmFileServiceImpl();

        mockMuleContextManager = createMock(MuleContextManager.class);
        mockMuleMessage = createMock(MuleMessage.class);
        mockCmisObject = createMock(CmisObject.class);

        unit.setMuleContextManager(mockMuleContextManager);
    }

    @Test
    public void createFolder() throws Exception
    {
        String path = "/some/path";
        String id = "id";

        expect(mockMuleContextManager.send("vm://createFolder.in", path)).andReturn(mockMuleMessage);
        expect(mockMuleMessage.getPayload(CmisObject.class)).andReturn(mockCmisObject);
        expect(mockCmisObject.getId()).andReturn(id);

        replayAll();

        String folderId = unit.createFolder(path);

        verifyAll();

        assertEquals(id, folderId);
    }
}
