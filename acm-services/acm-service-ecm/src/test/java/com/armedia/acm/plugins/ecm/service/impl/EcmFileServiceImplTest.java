package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileUpdatedEvent;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.module.cmis.connectivity.CMISCloudConnectorConnectionManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;

import javax.persistence.PersistenceException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static junit.framework.TestCase.assertTrue;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
    private EcmFileDao mockEcmFileDao;
    private Authentication mockAuthentication;
    private ApplicationEventPublisher mockApplicationEventPublisher;

    @Before
    public void setUp() throws Exception
    {
        unit = new EcmFileServiceImpl();

        mockMuleContextManager = createMock(MuleContextManager.class);
        mockMuleMessage = createMock(MuleMessage.class);
        mockCmisObject = createMock(CmisObject.class);
        mockCmisConfigUtils = createMock(CmisConfigUtils.class);
        mockEcmFileDao = createMock(EcmFileDao.class);
        mockAuthentication = createMock(Authentication.class);
        mockApplicationEventPublisher = createMock(ApplicationEventPublisher.class);

        ecmFileServiceProperties = new Properties();
        ecmFileServiceProperties.setProperty("ecm.defaultCmisId", defaultCmisId);

        unit.setMuleContextManager(mockMuleContextManager);
        unit.setCmisConfigUtils(mockCmisConfigUtils);
        unit.setEcmFileServiceProperties(ecmFileServiceProperties);
        unit.setApplicationEventPublisher(mockApplicationEventPublisher);
        unit.setEcmFileDao(mockEcmFileDao);
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

    @Test
    public void updateFile() throws Exception
    {
        AcmContainer acmContainer = new AcmContainer();
        acmContainer.setContainerObjectType("CASE_FILE");
        acmContainer.setContainerObjectId(101L);

        EcmFile in = new EcmFile();
        in.setFileId(100L);
        in.setFileType("file_type");
        in.setStatus("new_status");
        in.setContainer(acmContainer);

        Capture<EcmFile> saved = Capture.newInstance();
        Capture<EcmFileUpdatedEvent> capturedEvent = Capture.newInstance();
        mockApplicationEventPublisher.publishEvent(capture(capturedEvent));
        expectLastCall();

        expect(mockAuthentication.getName()).andReturn("user").anyTimes();
        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();
        expect(mockEcmFileDao.save(capture(saved))).andReturn(in);


        replayAll();

        in = unit.updateFile(in, mockAuthentication);

        verifyAll();

        assertEquals(in.getFileId(), saved.getValue().getFileId());
        assertEquals(in.getStatus(), saved.getValue().getStatus());


        EcmFileUpdatedEvent event = capturedEvent.getValue();
        assertEquals(in.getFileId(), event.getObjectId());
        assertEquals("FILE", event.getObjectType());
        assertTrue(event.isSucceeded());
    }

    @Test(expected = AcmUserActionFailedException.class)
    public void updateFile_exception() throws Exception
    {
        AcmContainer acmContainer = new AcmContainer();
        acmContainer.setContainerObjectType("CASE_FILE");
        acmContainer.setContainerObjectId(101L);

        EcmFile in = new EcmFile();
        in.setFileId(100L);
        in.setFileType("file_type");
        in.setStatus("new_status");
        in.setContainer(acmContainer);

        Capture<EcmFile> rejected = Capture.newInstance();
        Capture<EcmFileUpdatedEvent> capturedEvent = Capture.newInstance();
        mockApplicationEventPublisher.publishEvent(capture(capturedEvent));
        expectLastCall();

        expect(mockAuthentication.getName()).andReturn("user").anyTimes();
        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();
        try{
            expect(mockEcmFileDao.save(capture(rejected))).andThrow(new PersistenceException("persistence exception"));
        }catch(PersistenceException e){
            //do nothing, AcmUserActionFailedException expected
        }

        replayAll();

        in = unit.updateFile(in, mockAuthentication);

        verifyAll();

        EcmFile ecmFile = rejected.getValue();
        assertEquals(ecmFile.getFileId(), in.getFileId());

        EcmFileUpdatedEvent event = capturedEvent.getValue();
        assertEquals(in.getFileId(), event.getObjectId());
        assertEquals("FILE", event.getObjectType());
        assertFalse(event.isSucceeded());
    }
}
