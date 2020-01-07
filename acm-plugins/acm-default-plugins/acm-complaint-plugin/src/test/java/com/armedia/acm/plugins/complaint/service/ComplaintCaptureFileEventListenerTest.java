package com.armedia.acm.plugins.complaint.service;

/*-
 * #%L
 * ACM Default Plugin: Complaints
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.FileAddedEvent;
import com.armedia.acm.files.FileConstants;
import com.armedia.acm.files.capture.DocumentObject;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileObject;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;

import javax.xml.bind.Unmarshaller;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by riste.tutureski on 10/9/2015.
 */
@RunWith(EasyMockRunner.class)
public class ComplaintCaptureFileEventListenerTest extends EasyMockSupport
{
    private ComplaintCaptureFileEventListener complaintCaptureFileEventListener;

    @Mock
    private AuditPropertyEntityAdapter mockAuditPropertyEntityAdapter;

    @Mock
    private EcmFileService mockEcmFileService;

    @Mock
    private SaveComplaintTransaction mockSavaSaveComplaintTransaction;

    @Mock
    private FileObject mockWatchFolder;

    @Mock
    private FileObject mockWorkingFolder;

    @Mock
    private FileObject mockCompletedFolder;

    @Mock
    private FileObject mockErrorFolder;

    @Mock
    private FileObject mockFileObject;

    @Mock
    private File mockFile;

    @Mock
    private File mockDocumentFile;

    private Long loadingDocumentsSeconds = 10L;

    @Before
    public void setUp() throws Exception
    {
        complaintCaptureFileEventListener = createMockBuilder(ComplaintCaptureFileEventListener.class)
                .addMockedMethod("getEntity")
                .addMockedMethod("getFileDocuments")
                .addMockedMethod("moveFileToFolder")
                .addMockedMethod("saveAttachment").createMock();

        complaintCaptureFileEventListener.setWatchFolder(mockWatchFolder);
        complaintCaptureFileEventListener.setWorkingFolder(mockWorkingFolder);
        complaintCaptureFileEventListener.setCompletedFolder(mockCompletedFolder);
        complaintCaptureFileEventListener.setErrorFolder(mockErrorFolder);
        complaintCaptureFileEventListener.setLoadingDocumentsSeconds(loadingDocumentsSeconds);
        complaintCaptureFileEventListener.setSaveComplaintTransaction(mockSavaSaveComplaintTransaction);
        complaintCaptureFileEventListener.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);
        complaintCaptureFileEventListener.setEcmFileService(mockEcmFileService);
        complaintCaptureFileEventListener.setLOG(LogManager.getLogger(getClass()));
    }

    @Test
    public void proccessBatch_AllDocumentPresent() throws Exception
    {
        String fileName = "batch_file_name.extension";

        FileAddedEvent event = new FileAddedEvent(new FileChangeEvent(mockFileObject));
        event.setFileName(fileName);
        event.setFile(mockFile);
        event.setType("CAPTURE");

        Complaint complaint1 = new Complaint();
        complaint1.setComplaintId(101L);

        Complaint complaint2 = new Complaint();
        complaint2.setComplaintId(102L);

        Map<String, DocumentObject> expectedDocuments = getExpectedDocuments();
        DynamicEntity entity = getEntity();

        expect(complaintCaptureFileEventListener.getEntity(mockFile)).andReturn(entity).anyTimes();
        expect(complaintCaptureFileEventListener.getFileDocuments(mockFile)).andReturn(expectedDocuments).anyTimes();
        expect(complaintCaptureFileEventListener.moveFileToFolder(mockFile, mockWorkingFolder)).andReturn(mockFile).anyTimes();
        expect(complaintCaptureFileEventListener.moveFileToFolder(mockDocumentFile, mockWorkingFolder)).andReturn(mockDocumentFile)
                .anyTimes();
        expect(complaintCaptureFileEventListener.moveFileToFolder(mockFile, mockCompletedFolder)).andReturn(mockFile).anyTimes();
        expect(complaintCaptureFileEventListener.moveFileToFolder(mockDocumentFile, mockCompletedFolder)).andReturn(mockDocumentFile)
                .anyTimes();

        mockAuditPropertyEntityAdapter.setUserId(FileConstants.XML_BATCH_USER);
        EasyMock.expectLastCall().anyTimes();

        Capture<Complaint> capturedComplaint1 = EasyMock.newCapture();
        Capture<Complaint> capturedComplaint2 = EasyMock.newCapture();

        Capture<Authentication> capturedAuthentication = EasyMock.newCapture();
        expect(mockSavaSaveComplaintTransaction.saveComplaint(capture(capturedComplaint1), capture(capturedAuthentication)))
                .andReturn(complaint1);
        expect(mockSavaSaveComplaintTransaction.saveComplaint(capture(capturedComplaint2), capture(capturedAuthentication)))
                .andReturn(complaint1);

        Capture<Long> complaintId = EasyMock.newCapture();
        Capture<DocumentObject> docObject = EasyMock.newCapture();
        Capture<String> cmisFolderId = EasyMock.newCapture();
        complaintCaptureFileEventListener.saveAttachment(capture(cmisFolderId), capture(complaintId), eq("COMPLAINT"), capture(docObject),
                eq("complaint"));
        EasyMock.expectLastCall().anyTimes();
        complaintCaptureFileEventListener.saveAttachment(capture(cmisFolderId), capture(complaintId), eq("COMPLAINT"), capture(docObject),
                eq("attachment"));
        EasyMock.expectLastCall().anyTimes();

        replayAll();

        complaintCaptureFileEventListener.onApplicationEvent(event);

        verifyAll();

        assertNotNull(capturedComplaint1.getValue());
        assertNotNull(capturedComplaint2.getValue());
    }

    @Test
    public void proccessBatch_OneDocumentMissing() throws Exception
    {
        String fileName = "batch_file_name.extension";

        FileAddedEvent event = new FileAddedEvent(new FileChangeEvent(mockFileObject));
        event.setFileName(fileName);
        event.setFile(mockFile);
        event.setType("CAPTURE");

        Complaint complaint1 = new Complaint();
        complaint1.setComplaintId(101L);

        Complaint complaint2 = new Complaint();
        complaint2.setComplaintId(102L);

        Map<String, DocumentObject> expectedDocuments = getExpectedDocumentsOneMissing();
        DynamicEntity entity = getEntity();

        expect(complaintCaptureFileEventListener.getEntity(mockFile)).andReturn(entity).anyTimes();
        expect(complaintCaptureFileEventListener.getFileDocuments(mockFile)).andReturn(expectedDocuments).anyTimes();
        expect(complaintCaptureFileEventListener.moveFileToFolder(mockFile, mockWorkingFolder)).andReturn(mockFile).anyTimes();
        expect(complaintCaptureFileEventListener.moveFileToFolder(mockDocumentFile, mockWorkingFolder)).andReturn(mockDocumentFile)
                .anyTimes();
        expect(complaintCaptureFileEventListener.moveFileToFolder(mockFile, mockCompletedFolder)).andReturn(mockFile).anyTimes();
        expect(complaintCaptureFileEventListener.moveFileToFolder(mockDocumentFile, mockCompletedFolder)).andReturn(mockDocumentFile)
                .anyTimes();

        mockAuditPropertyEntityAdapter.setUserId(FileConstants.XML_BATCH_USER);
        EasyMock.expectLastCall().anyTimes();

        Capture<Complaint> capturedComplaint1 = EasyMock.newCapture();
        Capture<Complaint> capturedComplaint2 = EasyMock.newCapture();

        Capture<Authentication> capturedAuthentication = EasyMock.newCapture();
        expect(mockSavaSaveComplaintTransaction.saveComplaint(capture(capturedComplaint1), capture(capturedAuthentication)))
                .andReturn(complaint1);
        expect(mockSavaSaveComplaintTransaction.saveComplaint(capture(capturedComplaint2), capture(capturedAuthentication)))
                .andReturn(complaint2);

        Capture<Long> complaintId = EasyMock.newCapture();
        Capture<DocumentObject> docObject = EasyMock.newCapture();
        Capture<String> cmisFolderId = EasyMock.newCapture();
        complaintCaptureFileEventListener.saveAttachment(capture(cmisFolderId), capture(complaintId), eq("COMPLAINT"), capture(docObject),
                eq("complaint"));
        EasyMock.expectLastCall().anyTimes();
        complaintCaptureFileEventListener.saveAttachment(capture(cmisFolderId), capture(complaintId), eq("COMPLAINT"), capture(docObject),
                eq("attachment"));
        EasyMock.expectLastCall().anyTimes();

        replayAll();

        Date startProcessDate = new Date();

        complaintCaptureFileEventListener.onApplicationEvent(event);

        Date endProcessDate = new Date();

        verifyAll();

        assertNotNull(capturedComplaint1.getValue());
        assertNotNull(capturedComplaint2.getValue());

        assertTrue(endProcessDate.getTime() - startProcessDate.getTime() >= loadingDocumentsSeconds);
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
        catch (Exception e)
        {

        }

        return entity;
    }

    Map<String, DocumentObject> getExpectedDocuments()
    {
        Map<String, DocumentObject> expectedDocuments = new HashMap<>();

        DynamicEntity entity = getEntity();
        List<DynamicEntity> documentsList = entity.<List<DynamicEntity>> get(FileConstants.XML_BATCH_DOCUMENTS_KEY);

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

    Map<String, DocumentObject> getExpectedDocumentsOneMissing()
    {
        Map<String, DocumentObject> expectedDocuments = new HashMap<>();

        DynamicEntity entity = getEntity();
        List<DynamicEntity> documentsList = entity.<List<DynamicEntity>> get(FileConstants.XML_BATCH_DOCUMENTS_KEY);

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

        // Document "DOC4" is missing

        doc1.setAttachments(Arrays.asList(doc3));

        expectedDocuments.put("DOC1", doc1);
        expectedDocuments.put("DOC2", doc2);

        return expectedDocuments;
    }

}
