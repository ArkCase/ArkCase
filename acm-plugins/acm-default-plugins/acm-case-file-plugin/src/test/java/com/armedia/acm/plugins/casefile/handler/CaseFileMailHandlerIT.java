package com.armedia.acm.plugins.casefile.handler;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.Message;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;

/**
 * Created by nebojsha on 25.06.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-integration-case-file-test.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml"
})
public class CaseFileMailHandlerIT extends EasyMockSupport
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private Message messageMock;

    @Autowired
    private CaseFileMailHandler handler;
    private CaseFileDao caseFileDaoMock;
    private AcmFolderService acmFolderServiceMock;
    private EcmFileService ecmFileServiceMock;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapterMock;
    private String mailDir;

    @Before
    public void setUp()
    {
        messageMock = createMock(Message.class);
        caseFileDaoMock = createMock(CaseFileDao.class);
        ecmFileServiceMock = createMock(EcmFileService.class);
        acmFolderServiceMock = createMock(AcmFolderService.class);
        auditPropertyEntityAdapterMock = createMock(AuditPropertyEntityAdapter.class);

        handler.setEnabled(true);
        handler.setCaseFileDao(caseFileDaoMock);
        handler.setAcmFolderService(acmFolderServiceMock);
        handler.setEcmFileService(ecmFileServiceMock);
        handler.setAuditPropertyEntityAdapter(auditPropertyEntityAdapterMock);
        mailDir = "received-emails";
        handler.setMailDirectory(mailDir);

    }

    @Test
    public void testHandle()
    {
        try
        {
            String caseNumber = "20150511_123123";
            EasyMock.expect(messageMock.getSubject()).andReturn("aadasdasdasd_" + caseNumber + " some random text here and numbers 123123").anyTimes();
            CaseFile cf = new CaseFile();
            cf.setId(1l);
            EasyMock.expect(caseFileDaoMock.findByCaseNumber(EasyMock.eq(caseNumber))).andReturn(cf);

            auditPropertyEntityAdapterMock.setUserId(EasyMock.eq("mail-service"));
            EasyMock.expectLastCall();

            Capture<OutputStream> osCapture = new Capture<>();
            messageMock.writeTo(EasyMock.capture(osCapture));
            EasyMock.expectLastCall();

            AcmFolder acmFolder = new AcmFolder();
            acmFolder.setCmisFolderId("cmisFolderId");
            EasyMock.expect(acmFolderServiceMock.addNewFolderByPath(EasyMock.eq("CASE_FILE"), EasyMock.eq(1l), EasyMock.eq("received-emails"))).andReturn(acmFolder);

            EasyMock.expect(ecmFileServiceMock.upload(
                    EasyMock.contains("_20150511_123123.eml"),
                    EasyMock.eq("mail"),
                    EasyMock.eq("Document"),
                    EasyMock.anyObject(InputStream.class),
                    EasyMock.eq("message/rfc822"),
                    EasyMock.contains("_20150511_123123.eml"),
                    EasyMock.anyObject(Authentication.class),
                    EasyMock.eq("cmisFolderId"),
                    EasyMock.eq("CASE_FILE"),
                    EasyMock.eq(1l))).andReturn(new EcmFile());


            replayAll();
            handler.handle(messageMock);
        } catch (Exception e)
        {
            log.error("Error:", e);
            fail();
        }
        verifyAll();
    }
}
