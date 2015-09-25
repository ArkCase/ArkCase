package com.armedia.acm.files.capture;

import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by riste.tutureski on 9/2/2015.
 */
public abstract class AbstractCaptureFileListener implements ApplicationListener<AbstractCaptureFileEvent>
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private FileObject captureFolder;
    private FileObject workingFolder;
    private FileObject completedFolder;

    @Override
    public void onApplicationEvent(AbstractCaptureFileEvent event)
    {
        LOG.debug("Capture File Event was raised!");
    }

    /**
     * Check if provided event is CaptureFileAddedEvent
     *
     * @param event
     * @return
     */
    public boolean isCreatedEvent(AbstractCaptureFileEvent event)
    {
        if (event instanceof CaptureFileAddedEvent)
        {
            return true;
        }

        return false;
    }

    /**
     * <i>Still don't have any CaptureFileChangedEvent. So far, return just false</i>
     *
     * @param event
     * @return
     */
    public boolean isChangedEvent(AbstractCaptureFileEvent event)
    {
        // Still don't have any CaptureFileChangedEvent. So far, return just false
        return false;
    }

    /**
     * <i>Still don't have any CaptureFileDeletedEvent. So far, return just false</i>
     *
     * @param event
     * @return
     */
    public boolean isDeletedEvent(AbstractCaptureFileEvent event)
    {
        // Still don't have any CaptureFileDeletedEvent. So far, return just false
        return false;
    }

    /**
     * Check if the object is supported or implemented listener
     *
     * @param event
     * @return
     */
    public boolean isSupported(AbstractCaptureFileEvent event)
    {
        try
        {
            if (event != null && event.getCaptureFile() != null)
            {
                DynamicEntity entity = getEntity(event.getCaptureFile());

                if (entity != null)
                {
                    String batchName = entity.<String>get(CaptureConstants.XML_BATCH_CLASS_NAME_KEY);

                    if (batchName != null && batchName.equals(getBatchClassNameValue()))
                    {
                        return true;
                    }
                }
            }
        } catch (Exception e)
        {
            LOG.warn("Cannot check if the file {} is supported: ", event.getBaseFileName());
        }

        return false;
    }

    /**
     * This method will return dynamic entity that is created for given XML file and XSD schema.
     * <p>
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
            String userHome = System.getProperty("user.home");
            InputStream oxm = new FileInputStream(userHome + getOXMFilePath());

            Map<String, Object> properties = new HashMap<>();
            properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, oxm);
            DynamicJAXBContext context = DynamicJAXBContextFactory.createContextFromOXM(getClass().getClassLoader(), properties);

            Unmarshaller unmarshaller = context.createUnmarshaller();

            entity = (DynamicEntity) unmarshaller.unmarshal(xmlBatch);
        } catch (Exception e)
        {
            LOG.warn("Error while creating DynamicEntity from XML batch {}", xmlBatch != null ? xmlBatch.getName() : "null");
        }

        return entity;
    }

    /**
     * Moving captured file to working directory
     *
     * @param file
     * @return
     */
    public File moveFileToWorkingFolder(File file)
    {
        try
        {
            File parentFolder = new File(getWorkingFolder().getURL().toURI());
            File workingFile = new File(parentFolder, file.getName());

            FileUtils.moveFile(file, workingFile);

            return workingFile;
        } catch (Exception e)
        {
            LOG.error("Cannot move file to working directory: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Moving working file to completed directory
     *
     * @param file
     * @return
     */
    public File moveFileToCompletedFolder(File file)
    {
        try
        {
            File parentFolder = new File(getCompletedFolder().getURL().toURI());
            File completedFile = new File(parentFolder, file.getName());

            FileUtils.moveFile(file, completedFile);

            return completedFile;
        } catch (Exception e)
        {
            LOG.error("Cannot move file to completed directory: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Return all documents related to give batch XML file
     *
     * @param xmlBatch
     * @return
     */
    public Map<String, DocumentObject> getFileDocuments(File xmlBatch)
    {
        final Map<String, DocumentObject> documents = new HashMap<String, DocumentObject>();

        try
        {
            DynamicEntity entity = getEntity(xmlBatch);
            List<DynamicEntity> documentsList = entity.<List<DynamicEntity>>get(CaptureConstants.XML_BATCH_DOCUMENTS_KEY);

            documentsList.stream().forEach(element -> {
                try
                {
                    String id = element.<String>get(CaptureConstants.XML_BATCH_IDENTIFIER_KEY);
                    String type = element.<String>get(CaptureConstants.XML_BATCH_TYPE_KEY);
                    String capturePath = getCaptureFolder().getURL().toString().replace("file:///", "");
                    File tempDocument = new File(element.<String>get(CaptureConstants.XML_BATCH_MULTI_PAGE_PDF_FILE_KEY));
                    File document = new File(capturePath + File.separator + tempDocument.getName());

                    // If document not exist on the file system, do not create File object, just set null
                    if (document != null && !document.exists())
                    {
                        document = null;
                    }

                    DocumentObject doc = new DocumentObject(id, document, null, element);

                    if (CaptureConstants.XML_BATCH_ATTACHMENT.equals(type))
                    {
                        addAttachmentToDocumentObject(documents, doc);
                    } else
                    {
                        documents.put(id, doc);
                    }
                } catch (Exception e)
                {
                    LOG.error("Cannot take document: " + e.getMessage(), e);
                }
            });
        } catch (Exception e)
        {
            LOG.error("Cannot take documents for given batch file: " + e.getMessage(), e);
        }

        return documents;
    }

    /**
     * Add document as attachment to the form document, if document is of type "Attachment"
     *
     * @param documents
     * @param document
     */
    private void addAttachmentToDocumentObject(Map<String, DocumentObject> documents, DocumentObject document)
    {
        try
        {
            List<DynamicEntity> documentLevelFields = document.getEntity().<List<DynamicEntity>>get(CaptureConstants.XML_BATCH_DOCUMENT_LEVEL_FIELDS_KEY);

            if (documentLevelFields != null)
            {
                Optional<DynamicEntity> found = documentLevelFields.stream().filter(element -> getDocumentObjectID().equals(element.<String>get(CaptureConstants.XML_BATCH_NAME_KEY))).findFirst();
                if (found != null && found.isPresent())
                {
                    addAttachment(documents, document, found.get());
                }
            }
        } catch (Exception e)
        {
            LOG.error("Cannot add attachment to document: " + e.getMessage(), e);
        }
    }

    /**
     * Add document to the attachments
     *
     * @param documents
     * @param document
     * @param documentLevelField
     */
    private void addAttachment(Map<String, DocumentObject> documents, DocumentObject document, DynamicEntity documentLevelField)
    {
        if (documents != null && document != null && documentLevelField != null)
        {
            String value = documentLevelField.<String>get(CaptureConstants.XML_BATCH_VALUE_KEY);
            DocumentObject docObject = documents.get(value);

            if (docObject != null)
            {
                if (docObject.getAttachments() == null)
                {
                    docObject.setAttachments(new ArrayList<DocumentObject>());
                }

                docObject.getAttachments().add(document);
                documents.put(value, docObject);
            }
        }
    }

    /**
     * This method should be implemented in the appropriate implementation of this abstract class.
     *
     * @return
     */
    public abstract String getBatchClassNameValue();

    public abstract String getDocumentObjectID();

    /**
     * This method should be implemented in the approptiate implementation of this abstract class.
     *
     * @return
     */
    public abstract String getOXMFilePath();

    public FileObject getCaptureFolder()
    {
        return captureFolder;
    }

    public void setCaptureFolder(FileObject captureFolder)
    {
        this.captureFolder = captureFolder;
    }

    public FileObject getWorkingFolder()
    {
        return workingFolder;
    }

    public void setWorkingFolder(FileObject workingFolder)
    {
        this.workingFolder = workingFolder;
    }

    public FileObject getCompletedFolder()
    {
        return completedFolder;
    }

    public void setCompletedFolder(FileObject completedFolder)
    {
        this.completedFolder = completedFolder;
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
