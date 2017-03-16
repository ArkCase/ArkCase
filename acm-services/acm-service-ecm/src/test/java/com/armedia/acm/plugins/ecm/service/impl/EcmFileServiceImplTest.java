package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.module.cmis.connectivity.CMISCloudConnectorConnectionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * Created by armdev on 3/11/15.
 */
public class EcmFileServiceImplTest extends EasyMockSupport
{
    private final String defaultCmisId = "defaultCmisId";
    private EcmFileServiceImpl unit;

    private MuleContextManager mockMuleContextManager;
    private MuleMessage mockMuleMessage;
    private CmisObject mockCmisObject;
    private CmisConfigUtils mockCmisConfigUtils;
    private Properties ecmFileServiceProperties;

    @Before
    public void setUp() throws Exception
    {
        unit = new EcmFileServiceImpl();

        mockMuleContextManager = createMock(MuleContextManager.class);
        mockMuleMessage = createMock(MuleMessage.class);
        mockCmisObject = createMock(CmisObject.class);
        mockCmisConfigUtils = createMock(CmisConfigUtils.class);

        ecmFileServiceProperties = new Properties();
        ecmFileServiceProperties.setProperty("ecm.defaultCmisId", defaultCmisId);

        unit.setMuleContextManager(mockMuleContextManager);
        unit.setCmisConfigUtils(mockCmisConfigUtils);
        unit.setEcmFileServiceProperties(ecmFileServiceProperties);
    }

    @Test
    public void createFolder() throws Exception
    {
        String path = "/some/path";
        String id = "id";
        CMISCloudConnectorConnectionManager cmisConfig = new CMISCloudConnectorConnectionManager();
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put(EcmFileConstants.CONFIGURATION_REFERENCE, cmisConfig);

        expect(mockMuleContextManager.send("vm://createFolder.in", path, messageProps)).andReturn(mockMuleMessage);
        expect(mockMuleMessage.getPayload(CmisObject.class)).andReturn(mockCmisObject);
        expect(mockCmisObject.getId()).andReturn(id);
        expect(mockCmisConfigUtils.getCmisConfiguration(defaultCmisId)).andReturn(cmisConfig);



        replayAll();

        String folderId = unit.createFolder(path);

        verifyAll();

        assertEquals(id, folderId);
    }
}
