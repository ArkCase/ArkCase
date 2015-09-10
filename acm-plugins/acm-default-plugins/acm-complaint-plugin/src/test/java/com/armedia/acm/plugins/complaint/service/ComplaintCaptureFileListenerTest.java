package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.capture.CaptureConstants;
import com.armedia.acm.files.capture.CaptureFileAddedEvent;
import com.armedia.acm.files.capture.DocumentObject;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileObject;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import javax.swing.text.Document;
import javax.xml.bind.Unmarshaller;

import static org.easymock.EasyMock.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by riste.tutureski on 9/9/2015.
 */
@RunWith(EasyMockRunner.class)
public class ComplaintCaptureFileListenerTest extends EasyMockSupport
{
    @Mock
    private ComplaintCaptureFileListener complaintCaptureFileListener;

    @Mock
    private AuditPropertyEntityAdapter mockAuditPropertyEntityAdapter;

    @Mock
    private EcmFileService mockEcmFileService;

    @Mock
    private SaveComplaintTransaction mockSaveComplaintTransaction;

    @Mock
    private FileObject mockCaptureFolder;

    @Mock
    private FileObject mockWorkingFolder;

    @Mock
    private FileObject mockCompletedFolder;

    @Mock
    private FileObject mockFileObject;

    @Mock
    private File mockCaptureFile;

    @Mock
    private File mockDocumentFile;

    @Before
    public void setUp() throws Exception
    {
        complaintCaptureFileListener = createMockBuilder(ComplaintCaptureFileListener.class)
                                               .addMockedMethod("getEntity")
                                               .addMockedMethod("getFileDocuments")
                                               .addMockedMethod("moveFileToWorkingFolder")
                                               .addMockedMethod("moveFileToCompletedFolder")
                                               .addMockedMethod("saveComplaintAttachment").createMock();

        complaintCaptureFileListener.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
        complaintCaptureFileListener.setEcmFileService(mockEcmFileService);
        complaintCaptureFileListener.setLoadingDocumentsSeconds(10);
        complaintCaptureFileListener.setSaveComplaintTransaction(mockSaveComplaintTransaction);
        complaintCaptureFileListener.setCaptureFolder(mockCaptureFolder);
        complaintCaptureFileListener.setWorkingFolder(mockWorkingFolder);
        complaintCaptureFileListener.setCompletedFolder(mockCompletedFolder);
        complaintCaptureFileListener.setLOG(LoggerFactory.getLogger(getClass()));
    }

    @Test
    public void proccessBatch_AllDocumentPresent() throws Exception
    {
        CaptureFileAddedEvent event = new CaptureFileAddedEvent(new FileChangeEvent(mockFileObject));
        event.setBaseFileName("base_file_name");
        event.setCaptureFile(mockCaptureFile);

        Map<String, DocumentObject> expectedDocuments = getExpectedDocuments();
        DynamicEntity entity = getEntity();

        expect(complaintCaptureFileListener.getEntity(mockCaptureFile)).andReturn(entity).anyTimes();
        expect(complaintCaptureFileListener.getFileDocuments(mockCaptureFile)).andReturn(expectedDocuments).anyTimes();
        expect(complaintCaptureFileListener.moveFileToWorkingFolder(mockCaptureFile)).andReturn(mockCaptureFile).anyTimes();
        expect(complaintCaptureFileListener.moveFileToWorkingFolder(mockDocumentFile)).andReturn(mockDocumentFile).anyTimes();
        expect(complaintCaptureFileListener.moveFileToCompletedFolder(mockCaptureFile)).andReturn(mockCaptureFile).anyTimes();
        expect(complaintCaptureFileListener.moveFileToCompletedFolder(mockDocumentFile)).andReturn(mockDocumentFile).anyTimes();

        mockAuditPropertyEntityAdapter.setUserId(CaptureConstants.XML_BATCH_USER);
        EasyMock.expectLastCall().anyTimes();

        Capture<Complaint> captureComplaint = new Capture<>();
        Capture<DocumentObject> captureDocumentObject = new Capture<>();
        Capture<String> captureType = new Capture<>();
        expect(mockSaveComplaintTransaction.saveComplaint(capture(captureComplaint), eq(null))).andReturn(new Complaint()).anyTimes();

        complaintCaptureFileListener.saveComplaintAttachment(capture(captureComplaint), capture(captureDocumentObject), capture(captureType));
        EasyMock.expectLastCall().anyTimes();

        replayAll();

        complaintCaptureFileListener.onApplicationEvent(event);

        verifyAll();
    }

    @Test
    public void proccessBatch_DocumentMissing() throws Exception
    {
        complaintCaptureFileListener.setLoadingDocumentsSeconds(15);

        CaptureFileAddedEvent event = new CaptureFileAddedEvent(new FileChangeEvent(mockFileObject));
        event.setBaseFileName("base_file_name");
        event.setCaptureFile(mockCaptureFile);

        Map<String, DocumentObject> expectedDocuments = getExpectedDocumentsWithMissingOne();
        DynamicEntity entity = getEntity();

        expect(complaintCaptureFileListener.getEntity(mockCaptureFile)).andReturn(entity).anyTimes();
        expect(complaintCaptureFileListener.getFileDocuments(mockCaptureFile)).andReturn(expectedDocuments).anyTimes();
        expect(complaintCaptureFileListener.moveFileToWorkingFolder(mockCaptureFile)).andReturn(mockCaptureFile).anyTimes();
        expect(complaintCaptureFileListener.moveFileToWorkingFolder(mockDocumentFile)).andReturn(mockDocumentFile).anyTimes();
        expect(complaintCaptureFileListener.moveFileToCompletedFolder(mockCaptureFile)).andReturn(mockCaptureFile).anyTimes();
        expect(complaintCaptureFileListener.moveFileToCompletedFolder(mockDocumentFile)).andReturn(mockDocumentFile).anyTimes();

        mockAuditPropertyEntityAdapter.setUserId(CaptureConstants.XML_BATCH_USER);
        EasyMock.expectLastCall().anyTimes();

        Capture<Complaint> captureComplaint = new Capture<>();
        Capture<DocumentObject> captureDocumentObject = new Capture<>();
        Capture<String> captureType = new Capture<>();
        expect(mockSaveComplaintTransaction.saveComplaint(capture(captureComplaint), eq(null))).andReturn(new Complaint()).anyTimes();

        complaintCaptureFileListener.saveComplaintAttachment(capture(captureComplaint), capture(captureDocumentObject), capture(captureType));
        EasyMock.expectLastCall().anyTimes();

        replayAll();

        Date dateStart = new Date();

        complaintCaptureFileListener.onApplicationEvent(event);

        Date dateEnd = new Date();

        verifyAll();

        long waitingSeconds = (dateEnd.getTime() - dateStart.getTime())/1000;
        Assert.assertTrue(waitingSeconds >= 15);
    }

    private DynamicEntity getEntity()
    {
        DynamicEntity entity = null;

        try
        {
            InputStream xmlBatch = getClass().getClassLoader().getResourceAsStream("xml/BI14_batch.xml");
            InputStream oxm = getClass().getClassLoader().getResourceAsStream("xml/ephesoft_complaint_oxm.xml");

            Map<String, Object> properties = new HashMap<>();
            properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, oxm);
            DynamicJAXBContext context = DynamicJAXBContextFactory.createContextFromOXM(getClass().getClassLoader(), properties);

            Unmarshaller unmarshaller = context.createUnmarshaller();

            entity = (DynamicEntity) unmarshaller.unmarshal(xmlBatch);
        }
        catch(Exception e)
        {

        }

        return entity;
    }

    Map<String, DocumentObject> getExpectedDocuments()
    {
        Map<String, DocumentObject> expectedDocuments = new HashMap<>();

        DynamicEntity entity = getEntity();
        List<DynamicEntity> documentsList = entity.<List<DynamicEntity>>get(CaptureConstants.XML_BATCH_DOCUMENTS_KEY);

        DocumentObject doc1 = new DocumentObject();
        DocumentObject doc2 = new DocumentObject();
        DocumentObject doc3 = new DocumentObject();
        DocumentObject doc4 = new DocumentObject();

        doc1.setId("DOC1");
        doc1.setEntity(documentsList.get(0));
        doc1.setDocument(mockDocumentFile);

        doc2.setId("DOC2");
        doc2.setEntity(documentsList.get(1));
        doc2.setDocument(mockDocumentFile);

        doc3.setId("DOC3");
        doc3.setEntity(documentsList.get(2));
        doc3.setDocument(mockDocumentFile);

        doc4.setId("DOC4");
        doc4.setEntity(documentsList.get(3));
        doc4.setDocument(mockDocumentFile);

        doc1.setAttachments(Arrays.asList(doc3));
        doc2.setAttachments(Arrays.asList(doc4));

        expectedDocuments.put("DOC1", doc1);
        expectedDocuments.put("DOC2", doc2);

        return expectedDocuments;
    }

    Map<String, DocumentObject> getExpectedDocumentsWithMissingOne()
    {
        Map<String, DocumentObject> expectedDocuments = new HashMap<>();

        DynamicEntity entity = getEntity();
        List<DynamicEntity> documentsList = entity.<List<DynamicEntity>>get(CaptureConstants.XML_BATCH_DOCUMENTS_KEY);

        DocumentObject doc1 = new DocumentObject();
        DocumentObject doc2 = new DocumentObject();
        DocumentObject doc3 = new DocumentObject();

        doc1.setId("DOC1");
        doc1.setEntity(documentsList.get(0));
        doc1.setDocument(mockDocumentFile);

        doc2.setId("DOC2");
        doc2.setEntity(documentsList.get(1));
        doc2.setDocument(mockDocumentFile);

        doc3.setId("DOC3");
        doc3.setEntity(documentsList.get(2));
        doc3.setDocument(mockDocumentFile);

        doc1.setAttachments(Arrays.asList(doc3));

        expectedDocuments.put("DOC1", doc1);
        expectedDocuments.put("DOC2", doc2);

        return expectedDocuments;
    }

}
