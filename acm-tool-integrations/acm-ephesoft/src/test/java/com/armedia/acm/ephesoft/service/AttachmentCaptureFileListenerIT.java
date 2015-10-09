package com.armedia.acm.ephesoft.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.capture.ConvertedFileAddedEvent;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileObject;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by nebojsha on 15/9/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-ephesoft-test.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
})
@TransactionConfiguration(defaultRollback = true)
public class AttachmentCaptureFileListenerIT extends EasyMockSupport
{
    @Autowired
    private AttachmentCaptureFileListener attachmentCaptureFileListener;

    @Autowired
    FileObject captureFolder;

    @Autowired
    FileObject errorFolder;

    @Autowired
    FileObject completedFolder;

    private EcmFileService ecmFileService;

    private FileObject mockFileObject;

    @Before
    public void setUp() throws IOException
    {
        ecmFileService = createMock(EcmFileService.class);
        attachmentCaptureFileListener.setEcmFileService(ecmFileService);
        attachmentCaptureFileListener.setAuditPropertyEntityAdapter(createMock(AuditPropertyEntityAdapter.class));
        mockFileObject = createMock(FileObject.class);
    }

    @Test
    public void processValidComplaintAttachments() throws IOException, AcmUserActionFailedException, AcmCreateObjectFailedException
    {
        final String fileName = "321321_complaint_22121.pdf";
        Resource resource = new ClassPathResource("/data/" + fileName);

        if (completedFolder.getChild(fileName) != null && completedFolder.getChild(fileName).exists())
            completedFolder.getChild(fileName).delete();

        File toBeProcessedFile = new File(captureFolder.getURL().getFile() + File.separator + resource.getFile().getName());
        Files.copy(resource.getFile().toPath(), toBeProcessedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);


        ConvertedFileAddedEvent event = new ConvertedFileAddedEvent(new FileChangeEvent(mockFileObject));
        event.setBaseFileName(fileName);
        event.setConvertedFile(toBeProcessedFile);


        assertNotNull(attachmentCaptureFileListener);

        EcmFile ecmFile = new EcmFile();
        ecmFile.setFileName("file_name");
        AcmContainer container = new AcmContainer();
        AcmFolder folder = new AcmFolder();
        folder.setCmisFolderId("cmisFodlerId");
        container.setFolder(folder);
        container.setContainerObjectType("COMPLAINT");
        container.setContainerObjectId(321321l);
        ecmFile.setContainer(container);
        expect(ecmFileService.findById(22121l)).andReturn(ecmFile);

        Capture<Authentication> authenticationCapture = EasyMock.newCapture();
        Capture<AcmMultipartFile> multipartFileCapture = EasyMock.newCapture();
        expect(ecmFileService.upload(eq("file_name"), eq("pdf"), capture(multipartFileCapture), capture(authenticationCapture), eq("cmisFodlerId"), eq("COMPLAINT"), eq(321321l))).andReturn(new EcmFile());

        replayAll();

        attachmentCaptureFileListener.onApplicationEvent(event);

        completedFolder.refresh();
        assertNull(completedFolder.getChild(fileName));

        verifyAll();
    }

    @Test
    public void processValidComplaintAttachmentsParentIdFileId() throws AcmUserActionFailedException, AcmCreateObjectFailedException, IOException
    {
        final String fileName = "321321_22121.pdf";
        Resource resource = new ClassPathResource("/data/" + fileName);

        if (completedFolder.getChild(fileName) != null && completedFolder.getChild(fileName).exists())
            completedFolder.getChild(fileName).delete();

        File toBeProcessedFile = new File(captureFolder.getURL().getFile() + File.separator + resource.getFile().getName());
        Files.copy(resource.getFile().toPath(), toBeProcessedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);


        ConvertedFileAddedEvent event = new ConvertedFileAddedEvent(new FileChangeEvent(mockFileObject));
        event.setBaseFileName(fileName);
        event.setConvertedFile(toBeProcessedFile);


        assertNotNull(attachmentCaptureFileListener);

        EcmFile ecmFile = new EcmFile();
        ecmFile.setFileName("file_name");
        AcmContainer container = new AcmContainer();
        AcmFolder folder = new AcmFolder();
        folder.setCmisFolderId("cmisFodlerId");
        container.setFolder(folder);
        container.setContainerObjectType("COMPLAINT");
        container.setContainerObjectId(321321l);
        ecmFile.setContainer(container);
        expect(ecmFileService.findById(22121l)).andReturn(ecmFile);

        Capture<Authentication> authenticationCapture = EasyMock.newCapture();
        Capture<AcmMultipartFile> multipartFileCapture = EasyMock.newCapture();
        expect(ecmFileService.upload(eq("file_name"), eq("pdf"), capture(multipartFileCapture), capture(authenticationCapture), eq("cmisFodlerId"), eq("COMPLAINT"), eq(321321l))).andReturn(new EcmFile());

        replayAll();

        attachmentCaptureFileListener.onApplicationEvent(event);

        completedFolder.refresh();
        assertNull(completedFolder.getChild(fileName));

        verifyAll();
    }

    @Test
    public void processValidComplaintAttachmentsFileId() throws AcmUserActionFailedException, AcmCreateObjectFailedException, IOException
    {
        final String fileName = "22121.pdf";
        Resource resource = new ClassPathResource("/data/" + fileName);

        if (completedFolder.getChild(fileName) != null && completedFolder.getChild(fileName).exists())
            completedFolder.getChild(fileName).delete();

        File toBeProcessedFile = new File(captureFolder.getURL().getFile() + File.separator + resource.getFile().getName());
        Files.copy(resource.getFile().toPath(), toBeProcessedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);


        ConvertedFileAddedEvent event = new ConvertedFileAddedEvent(new FileChangeEvent(mockFileObject));
        event.setBaseFileName(fileName);
        event.setConvertedFile(toBeProcessedFile);


        assertNotNull(attachmentCaptureFileListener);

        EcmFile ecmFile = new EcmFile();
        ecmFile.setFileName("file_name");
        AcmContainer container = new AcmContainer();
        AcmFolder folder = new AcmFolder();
        folder.setCmisFolderId("cmisFodlerId");
        container.setFolder(folder);
        container.setContainerObjectType("COMPLAINT");
        container.setContainerObjectId(321321l);
        ecmFile.setContainer(container);
        expect(ecmFileService.findById(22121l)).andReturn(ecmFile);

        Capture<Authentication> authenticationCapture = EasyMock.newCapture();
        Capture<AcmMultipartFile> multipartFileCapture = EasyMock.newCapture();
        expect(ecmFileService.upload(eq("file_name"), eq("pdf"), capture(multipartFileCapture), capture(authenticationCapture), eq("cmisFodlerId"), eq("COMPLAINT"), eq(321321l))).andReturn(new EcmFile());

        replayAll();

        attachmentCaptureFileListener.onApplicationEvent(event);

        completedFolder.refresh();
        assertNull(completedFolder.getChild(fileName));

        verifyAll();
    }

    @Test
    public void processValidCaseFileAttachments() throws AcmUserActionFailedException, AcmCreateObjectFailedException, IOException
    {
        final String fileName = "12313_case_file_32131.pdf";
        Resource resource = new ClassPathResource("/data/" + fileName);

        if (completedFolder.getChild(fileName) != null && completedFolder.getChild(fileName).exists())
            completedFolder.getChild(fileName).delete();

        File toBeProcessedFile = new File(captureFolder.getURL().getFile() + File.separator + resource.getFile().getName());
        Files.copy(resource.getFile().toPath(), toBeProcessedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        ConvertedFileAddedEvent event = new ConvertedFileAddedEvent(new FileChangeEvent(mockFileObject));
        event.setBaseFileName(fileName);
        event.setConvertedFile(toBeProcessedFile);

        assertNotNull(attachmentCaptureFileListener);

        EcmFile ecmFile = new EcmFile();
        ecmFile.setFileName("file_name");
        AcmContainer container = new AcmContainer();
        AcmFolder folder = new AcmFolder();
        folder.setCmisFolderId("cmisFodlerId");
        container.setFolder(folder);
        container.setContainerObjectType("CASE_FILE");
        container.setContainerObjectId(12313l);
        ecmFile.setContainer(container);
        expect(ecmFileService.findById(32131l)).andReturn(ecmFile);

        Capture<Authentication> authenticationCapture = EasyMock.newCapture();
        Capture<AcmMultipartFile> multipartFileCapture = EasyMock.newCapture();
        expect(ecmFileService.upload(eq("file_name"), eq("pdf"), capture(multipartFileCapture), capture(authenticationCapture), eq("cmisFodlerId"), eq("CASE_FILE"), eq(12313l))).andReturn(new EcmFile());

        replayAll();

        attachmentCaptureFileListener.onApplicationEvent(event);

        completedFolder.refresh();
        assertNull(completedFolder.getChild(fileName));

        verifyAll();
    }

    @Test
    public void processNotValidComplaintAttachments() throws IOException, AcmUserActionFailedException, AcmCreateObjectFailedException
    {
        final String fileName = "321321_complaint_22121.pdf";
        if (errorFolder.getChild(fileName) != null && errorFolder.getChild(fileName).exists())
            errorFolder.getChild(fileName).delete();

        Resource resource = new ClassPathResource("/data/" + fileName);


        File toBeProcessedFile = new File(captureFolder.getURL().getFile() + File.separator + resource.getFile().getName());
        Files.copy(resource.getFile().toPath(), toBeProcessedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);


        ConvertedFileAddedEvent event = new ConvertedFileAddedEvent(new FileChangeEvent(mockFileObject));
        event.setBaseFileName(fileName);
        event.setConvertedFile(toBeProcessedFile);


        assertNotNull(attachmentCaptureFileListener);

        EcmFile ecmFile = new EcmFile();
        ecmFile.setFileName("file_name");
        AcmContainer container = new AcmContainer();
        AcmFolder folder = new AcmFolder();
        folder.setCmisFolderId("cmisFodlerId");
        container.setFolder(folder);
        container.setContainerObjectType("COMPLAINT");
        container.setContainerObjectId(321321l);
        ecmFile.setContainer(container);
        expect(ecmFileService.findById(22121l)).andReturn(null);

        replayAll();

        attachmentCaptureFileListener.onApplicationEvent(event);

        errorFolder.refresh();
        assertNotNull(errorFolder.getChild(fileName));

        verifyAll();
    }

}