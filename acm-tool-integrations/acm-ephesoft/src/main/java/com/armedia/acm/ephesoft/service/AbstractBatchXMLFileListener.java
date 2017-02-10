package com.armedia.acm.ephesoft.service;

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.files.FileConstants;
import com.armedia.acm.files.FileEvent;
import com.armedia.acm.files.FileEventListener;
import com.armedia.acm.files.capture.CaptureConstants;
import com.armedia.acm.files.capture.DocumentObject;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.web.api.MDCConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;

import javax.activation.MimetypesFileTypeMap;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by riste.tutureski on 10/8/2015.
 */
public abstract class AbstractBatchXMLFileListener extends FileEventListener
{
    private Logger LOG = LoggerFactory.getLogger(getClass());
    private EcmFileService ecmFileService;
    private String OXMFilePath;

    @Override
    public void onApplicationEvent(FileEvent event)
    {
        super.onApplicationEvent(event);

        if (isSupported(event) && isFileAddedEvent(event))
        {
            LOG.debug("File is supported for processing. Trying to move files to working directory!");

            // Get XML batch file and all other documents related to the XML batch file
            File xmlBatch = event.getFile();
            Map<String, DocumentObject> documents = loadDocuments(xmlBatch);

            LOG.debug("Moving files to working directory - START");

            // Move XML batch and documents related to the batch to "working" folder
            File workingXmlBatch = moveFileToFolder(xmlBatch, getWorkingFolder());
            Map<String, DocumentObject> workingDocuments = moveDocumentsToFolder(documents, getWorkingFolder());

            LOG.debug("Moving files to working directory - END");

            if (workingXmlBatch != null)
            {
                LOG.debug("Start processing File Event!");

                // If we moved the batch to "working" folder, create needed orders
                processBatch(workingDocuments, getEntity(workingXmlBatch));

                // Move all files related to the batch to "completed" folder after processing
                LOG.debug("Moving files to completed directory - START");

                moveFileToFolder(workingXmlBatch, getCompletedFolder());
                moveDocumentsToFolder(workingDocuments, getCompletedFolder());

                LOG.debug("Moving files to completed directory - END");
            } else
            {
                moveFileToFolder(xmlBatch, getErrorFolder());
                moveDocumentsToFolder(documents, getErrorFolder());
            }
        }
    }

    /**
     * This method will return dynamic entity that is created for given XML file and XSD schema.
     * <p/>
     * The XML file is the XML representation of the batch file that Ephesoft will send to this system.
     *
     * @param xmlBatch
     * @return
     */
    public DynamicEntity getEntity(File xmlBatch)
    {
        DynamicEntity entity = null;

        try
        {
            InputStream oxm = new FileInputStream(getOXMFilePath());

            Map<String, Object> properties = new HashMap<>();
            properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, oxm);
            DynamicJAXBContext context = DynamicJAXBContextFactory.createContextFromOXM(getClass().getClassLoader(), properties);

            Unmarshaller unmarshaller = context.createUnmarshaller();

            entity = (DynamicEntity) unmarshaller.unmarshal(xmlBatch);
        } catch (Exception e)
        {
            LOG.warn("Error while creating DynamicEntity from XML batch {}", xmlBatch != null ? xmlBatch.getName() : "null", e);
        }

        return entity;
    }

    /**
     * Take how many seconds we will trying to check if all documents are loaded.
     *
     * @return - number of seconds
     */
    public abstract Long getLoadingDocumentsSeconds();

    /**
     * Process batch file - create objects and save them to database
     *
     * @param documents - documents with their attachments
     * @param batchEntity - parent batch dynamic entity
     */
    public abstract void processBatch(Map<String, DocumentObject> documents, DynamicEntity batchEntity);

    /**
     * Check if entity is attachment or object for processing
     *
     * @param entity
     * @return
     */
    public abstract boolean isAttachment(DynamicEntity entity);

    /**
     * Return attachments only these who will pass the filer. For different integrations, the way how we are taking
     * attachments is different. If no filter needed, just return provided list of attachments
     *
     * @param docObject - document object where attachments should be attached
     * @param attachments - attachments that should be attached to the object
     * @return - list of document objects
     */
    public abstract List<DocumentObject> filterAttachments(DocumentObject docObject, List<DocumentObject> attachments);

    /**
     * Save all documents as attachments related to given object
     *
     * @param cmisFolderId - cmis folder id
     * @param objectId - object id
     * @param objectType - type of the object
     * @param docObject - document object where the file is
     * @param objectFileType - the type of the file that representing the object itself
     * @param attachmentFileType - the type of the file that representing attachment for given object
     */
    public void saveAttachments(String cmisFolderId, Long objectId, String objectType, DocumentObject docObject, String objectFileType,
            String attachmentFileType)
    {
        if (docObject != null)
        {
            // Save Object PDF document as attachment in Alfresco
            if (docObject.getDocument() != null)
            {
                saveAttachment(cmisFolderId, objectId, objectType, docObject, objectFileType);
            }

            // Save other documents as attachments
            if (docObject.getAttachments() != null)
            {
                docObject.getAttachments().stream()
                        .forEach(doc -> saveAttachment(cmisFolderId, objectId, objectType, doc, attachmentFileType));
            }
        }
    }

    /**
     * Save document as attachment for given order
     *
     * @param cmisFolderId - cmis folder id
     * @param objectId - object id
     * @param objectType - type of the object
     * @param docObject - document object where the file is
     * @param fileType - type of the file
     */
    public void saveAttachment(String cmisFolderId, Long objectId, String objectType, DocumentObject docObject, String fileType)
    {
        try
        {
            // This will help us to recognize content type
            MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();

            // Take the input stream for given file
            InputStream originalIS = new BufferedInputStream(new FileInputStream(docObject.getDocument()));
            byte[] bytes = IOUtils.toByteArray(originalIS);

            // Create clone of the input stream and close the original. We need this to be able to release
            // original input stream to be able to move files through folders
            InputStream cloneIS = new ByteArrayInputStream(bytes);
            originalIS.close();

            // Take content type and create authentication object (we need authentication object for
            // EcmFileService - we need userID which in this case is set to FileConstants.XML_BATCH_USER value)
            String contentType = mimetypesFileTypeMap.getContentType(docObject.getDocument());
            Authentication auth = new AcmAuthentication(null, null, null, true, FileConstants.XML_BATCH_USER);

            // Create multipart file object - used "upload" service require it and using this service method is the best
            // way to upload file for given object - it creates AcmContainer object that we need for uploading
            AcmMultipartFile file = new AcmMultipartFile(docObject.getDocument().getName(), docObject.getDocument().getName(), contentType,
                    false, docObject.getDocument().length(), bytes, cloneIS, true);

            // since this code is run via a batch job, there is no authenticated user, so we need to specify the user
            // to be used for CMIS connections.  Similar to the requirement to 'getAuditPropertyEntityAdapter().setUserId',
            // only this user has to be a real Alfresco user.
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

            // Upload file
            getEcmFileService().upload(file.getOriginalFilename(), fileType, file, auth, cmisFolderId, objectType, objectId);
        } catch (Exception e)
        {
            LOG.error("Cannot save attachment. Reason: {}", e.getMessage(), e);
        }
    }

    /**
     * Take all documents for given XML batch file
     *
     * @param xmlBatch - XML batch file
     * @return - map where key is document identifier, value is document object
     */
    public Map<String, DocumentObject> getFileDocuments(File xmlBatch)
    {
        final Map<String, DocumentObject> documents = new HashMap<String, DocumentObject>();

        try
        {
            DynamicEntity entity = getEntity(xmlBatch);
            List<DynamicEntity> documentsList = entity.<List<DynamicEntity>> get(FileConstants.XML_BATCH_DOCUMENTS_KEY);

            List<DocumentObject> attachments = getAllAttachments(documentsList);
            documentsList.stream().forEach(element ->
            {
                DocumentObject doc = getDocumentObject(element);
                if (doc != null && doc.getId() != null && !isAttachment(doc.getEntity()))
                {
                    addAttachments(doc, attachments);
                    documents.put(doc.getId(), doc);
                }
            });
        } catch (Exception e)
        {
            LOG.error("Cannot take documents for given batch file. Reason: {}", e.getMessage(), e);
        }

        return documents;
    }

    /**
     * Add attachments to the object
     *
     * @param docObject - the object where attachments should be attached
     * @param attachments - attachments that should be attached to the object
     */
    public void addAttachments(DocumentObject docObject, List<DocumentObject> attachments)
    {
        if (docObject != null && attachments != null)
        {
            List<DocumentObject> filteredAttachments = filterAttachments(docObject, attachments);

            if (docObject.getAttachments() == null)
            {
                docObject.setAttachments(new ArrayList<>());
            }

            if (filteredAttachments != null)
            {
                docObject.getAttachments().addAll(filteredAttachments);
            }
        }
    }

    /**
     * Move file to given folder
     *
     * @param file - the file that should be moved
     * @param folder - the folder where file should be moved
     * @return - moved file
     */
    public File moveFileToFolder(File file, FileObject folder)
    {
        try
        {
            File movedFile = getFileForMoving(file, folder);
            FileUtils.moveFile(file, movedFile);

            return movedFile;
        } catch (Exception e)
        {
            // Sometime this is normal scenario - when one file is added as attachment in multiple objects
            LOG.warn("Cannot move file to directory. Maybe already moved. Reason: {}", e.getMessage());
        }

        return null;
    }

    /**
     * This method should return File object that is prepared for moving. In some implementations that we have now, the
     * file can be already moved by other object so, we should try to give him ability to find already moved file.
     *
     * @param file
     * @param folder
     * @return
     */
    private File getFileForMoving(File file, FileObject folder)
    {
        File movingFile = null;

        try
        {
            File parentFolder = new File(new URI(folder.toString().replace(" ", "%20")));
            movingFile = new File(parentFolder, file.getName());
        } catch (URISyntaxException e)
        {
            LOG.warn("Cannot prepare the file for moving. Reason: {}", e.getMessage());
        }

        return movingFile;
    }

    /**
     * Move all documents related to current batch to specified folder
     *
     * @param documents
     * @return
     */
    public Map<String, DocumentObject> moveDocumentsToFolder(Map<String, DocumentObject> documents, FileObject folder)
    {
        Map<String, DocumentObject> workingDocuments = new HashMap<String, DocumentObject>();

        if (documents != null)
        {
            // Move each document to working folder
            documents.forEach((key, value) ->
            {
                // Moving document
                File workingDocument = moveFileToFolder(value.getDocument(), folder);

                if (value.getAttachments() != null)
                {
                    // Moving attachments
                    moveAttachmentsToFolder(value, folder);
                }

                value.setDocument(workingDocument);
                workingDocuments.put(key, new DocumentObject(key, workingDocument, value.getAttachments(), value.getEntity()));
            });
        }

        return workingDocuments;
    }

    /**
     * Move attachments for given object to working folder
     *
     * @param docObject
     */
    private void moveAttachmentsToFolder(DocumentObject docObject, FileObject folder)
    {
        if (docObject != null && docObject.getAttachments() != null)
        {
            List<DocumentObject> workingAttachments = new ArrayList<DocumentObject>();
            docObject.getAttachments().stream().forEach(element ->
            {
                File workingAttachment = moveFileToFolder(element.getDocument(), folder);
                if (workingAttachment == null)
                {
                    // The file might be already moved by other object. Give ability to be found there
                    workingAttachment = getFileForMoving(element.getDocument(), folder);
                }
                workingAttachments.add(new DocumentObject(element.getId(), workingAttachment, null, element.getEntity()));
            });

            docObject.setAttachments(workingAttachments);
        }
    }

    /**
     * This method will return map of the documents and attachments described in the XML batch file.<br>
     * <br>
     * To be sure that all documents will be present when XML batch file is loaded, retry mechanism is executed if
     * required documents described in the XML batch file are not on the file system. This retry mechanism will check
     * every second if all documents are loaded. The repeat is made "X" seconds, where "X" is defined in the properties
     * files (default value is 10 seconds). If all documents are loaded before finishing "X" seconds, the retry process
     * will be stopped.
     *
     * @param xmlBatch
     * @return
     */
    public Map<String, DocumentObject> loadDocuments(File xmlBatch)
    {
        Map<String, DocumentObject> documents = null;
        long expectedDocumentsCount = getExpectedDocumentsCount(xmlBatch);

        // Try "X" seconds to see if all documents are imported. If not, after "X" seconds
        // continue with what we have
        for (int i = 0; i < getLoadingDocumentsSeconds(); i++)
        {
            documents = getFileDocuments(xmlBatch);
            if (documents != null)
            {
                // Check if expected count of documents is identical with loaded. If yes, just stop this process.
                long loadedDocumentsCount = getLoadedDocumentsCount(documents);
                if (expectedDocumentsCount == loadedDocumentsCount)
                {
                    LOG.debug("All requested documents are loaded!");
                    break;
                } else
                {
                    LOG.warn("Found " + loadedDocumentsCount + " documents from total documents " + expectedDocumentsCount);
                    LOG.debug("Attempt: " + (i + 1) + " from total attempts " + getLoadingDocumentsSeconds());
                }

                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException ie)
                {
                    LOG.error("Error while thread sleep: " + ie.getMessage(), ie);
                }
            }
        }

        return documents;
    }

    /**
     * Create DocumentObject for given information from XML file
     *
     * @param element - DynamicEntity object created from XML file
     * @return - DocumentObject
     */
    public DocumentObject getDocumentObject(DynamicEntity element)
    {
        DocumentObject doc = null;
        try
        {
            String id = element.<String> get(FileConstants.XML_BATCH_IDENTIFIER_KEY);
            File watchFolder = new File(new URI(getWatchFolder().toString().replace(" ", "%20")));
            String ephesoftFileName = element.<String> get(FileConstants.XML_BATCH_MULTI_PAGE_PDF_FILE_KEY);
            LOG.debug("File name from Ephesoft (may contain Ephesoft server path info): {}", ephesoftFileName);
            String winSeparator = "\\";
            if (ephesoftFileName.contains(winSeparator))
            {
                ephesoftFileName = ephesoftFileName.substring(ephesoftFileName.lastIndexOf(winSeparator) + 1);
            } else if (ephesoftFileName.contains("/"))
            {
                ephesoftFileName = ephesoftFileName.substring(ephesoftFileName.lastIndexOf("/") + 1);
            }

            LOG.debug("Filename part of Ephesoft file: {}", ephesoftFileName);

            File document = new File(watchFolder, ephesoftFileName);

            LOG.info("Expected path to file: {}", document.getCanonicalPath());
            LOG.info("File {} exists? {}", document.getCanonicalPath(), document.exists());

            // If document not exist on the file system, do not create File object, just set null
            if (document != null && !document.exists())
            {
                document = null;
            }

            doc = new DocumentObject(id, document, null, element);
        } catch (FileSystemException | URISyntaxException e)
        {
            LOG.error("Cannot take document. Reason: {}", e.getMessage(), e);
        } catch (IOException e)
        {
            LOG.error("Cannot take document. Reason: {}", e.getMessage(), e);
        }

        return doc;
    }

    /**
     * This method will return all attachments
     *
     * @param documentsList
     * @return
     */
    public List<DocumentObject> getAllAttachments(List<DynamicEntity> documentsList)
    {
        List<DocumentObject> retval = new ArrayList<>();

        if (documentsList != null)
        {
            documentsList.stream().forEach(element ->
            {
                DocumentObject doc = getDocumentObject(element);
                if (doc != null && doc.getId() != null && isAttachment(element))
                {
                    retval.add(doc);
                }
            });
        }

        return retval;
    }

    /**
     * The method will return number of documents provided in the XML batch file
     *
     * @param xmlBatch
     * @return
     */
    private long getExpectedDocumentsCount(File xmlBatch)
    {
        if (xmlBatch != null)
        {
            // Take dynamic entity from XML file
            DynamicEntity entity = getEntity(xmlBatch);

            if (entity != null)
            {
                // Take documents in the list
                List<DynamicEntity> documents = entity.<List<DynamicEntity>> get(CaptureConstants.XML_BATCH_DOCUMENTS_KEY);

                if (documents != null)
                {
                    // Return the size of the list which is number of expected documents
                    return documents.size();
                }
            }
        }

        return 0;
    }

    /**
     * This method will return current loaded documents
     *
     * @param documents
     * @return
     */
    private long getLoadedDocumentsCount(Map<String, DocumentObject> documents)
    {
        long loaded = 0;

        if (documents != null)
        {
            // There can be the case one document to be present to multiple objects as attachment. We will keep
            // temporary identifiers list and exclude that count if already identifier exist in this list
            List<String> documentIdentifiers = new ArrayList<>();

            // List all entries in the map (each entry is object)
            for (Map.Entry<String, DocumentObject> entry : documents.entrySet())
            {
                DocumentObject docObject = entry.getValue();

                // Increase "loaded" if document object is not null (object itself is one document)
                if (docObject != null && docObject.getDocument() != null && !documentIdentifiers.contains(docObject.getId()))
                {
                    documentIdentifiers.add(docObject.getId());
                    loaded++;
                }

                // Increase "loaded" if provided document (object) has attachments related to it
                if (docObject != null && docObject.getAttachments() != null)
                {
                    long count = docObject.getAttachments().stream().filter(element ->
                    {
                        if (element.getDocument() != null && !documentIdentifiers.contains(element.getId()))
                        {
                            documentIdentifiers.add(element.getId());
                            return true;
                        }
                        return false;
                    }).count();
                    loaded += count;
                }
            }
        }

        return loaded;
    }

    /**
     * This will return the value for given field name from the list of fields
     *
     * @param name
     * @param documentLevelFields
     * @return
     */
    public String getDocumentLevelFieldValue(String name, List<DynamicEntity> documentLevelFields)
    {
        String retval = null;

        if (name != null && documentLevelFields != null)
        {
            // Try to find DynamicEntity object for given name
            Optional<DynamicEntity> found = documentLevelFields.stream()
                    .filter(element -> name.equals(element.<String> get(CaptureConstants.XML_BATCH_NAME_KEY))).findFirst();
            if (found != null && found.isPresent())
            {
                retval = found.get().<String> get(CaptureConstants.XML_BATCH_VALUE_KEY);
            }
        }

        return retval;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public String getOXMFilePath()
    {
        return OXMFilePath;
    }

    public void setOXMFilePath(String OXMFilePath)
    {
        this.OXMFilePath = OXMFilePath;
    }

    public Logger getLOG()
    {
        return LOG;
    }

    public void setLOG(Logger LOG)
    {
        this.LOG = LOG;
    }
}
