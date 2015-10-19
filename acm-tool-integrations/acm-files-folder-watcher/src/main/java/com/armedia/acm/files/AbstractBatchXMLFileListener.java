package com.armedia.acm.files;

import com.armedia.acm.files.capture.CaptureConstants;
import com.armedia.acm.files.capture.DocumentObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by riste.tutureski on 10/8/2015.
 */
public abstract class AbstractBatchXMLFileListener extends FileEventListener
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

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
                processBatch(workingDocuments);

                // Move all files related to the batch to "completed" folder after processing
                LOG.debug("Moving files to completed directory - START");

                moveFileToFolder(workingXmlBatch, getCompletedFolder());
                moveDocumentsToFolder(workingDocuments, getCompletedFolder());

                LOG.debug("Moving files to completed directory - END");
            }
            else
            {
                moveFileToFolder(xmlBatch, getErrorFolder());
                moveDocumentsToFolder(documents, getErrorFolder());
            }
        }
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
     * Take OXM file destination
     *
     * @return - path in string representation
     */
    public abstract String getOXMFilePath();

    /**
     * Take how many seconds we will trying to check if all documents are loaded.
     *
     * @return - number of seconds
     */
    public abstract Long getLoadingDocumentsSeconds();

    /**
     * Take all documents for given XML batch file
     *
     * @param xmlBatch - XML batch file
     * @return - map where key is document identifier, value is document object
     */
    public abstract Map<String, DocumentObject> getFileDocuments(File xmlBatch);

    /**
     * Process batch file - create objects and save them to database
     *
     * @param documents - documents with their attachments
     */
    public abstract void processBatch( Map<String, DocumentObject> documents);

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
        }
        catch (Exception e)
        {
            // Sometime this is normal scenario - when one file is added as attachment in multiple objects
            LOG.warn("Cannot move file to directory. Maybe already moved. Reason: {}", e.getMessage());
        }

        return null;
    }

    /**
     * This method should return File object that is prepared for moving.
     * In some implementations that we have now, the file can be already moved by other object so, we should
     * try to give him ability to find already moved file.
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
            File parentFolder = new File(folder.getURL().toURI());
            movingFile = new File(parentFolder, file.getName());
        }
        catch (URISyntaxException | FileSystemException e)
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
            for (Map.Entry<String, DocumentObject> entry : documents.entrySet())
            {
                String key = entry.getKey();
                DocumentObject value = entry.getValue();

                // Moving document
                File workingDocument = moveFileToFolder(value.getDocument(), folder);

                if (value.getAttachments() != null)
                {
                    // Moving attachments
                    moveAttachmentsToFolder(value, folder);
                }

                value.setDocument(workingDocument);
                workingDocuments.put(key, new DocumentObject(key, workingDocument, value.getAttachments(), value.getEntity()));
            }
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
            docObject.getAttachments().stream().forEach(element -> {
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
     * This method will return map of the documents and attachments described in the XML batch file.<br><br>
     * To be sure that all documents will be present when XML batch file is loaded, retry mechanism is
     * executed if required documents described in the XML batch file are not on the file system.
     * This retry mechanism will check every second if all documents are loaded. The repeat is made "X" seconds,
     * where "X" is defined in the properties files (default value is 10 seconds). If all documents are loaded
     * before finishing "X" seconds, the retry process will be stopped.
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
                }
                else
                {
                    LOG.warn("Found " + loadedDocumentsCount + " documents from total documents " + expectedDocumentsCount);
                    LOG.debug("Attempt: " + (i + 1) + " from total attempts " + getLoadingDocumentsSeconds());
                }

                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException ie)
                {
                    LOG.error("Error while thread sleep: " + ie.getMessage(), ie);
                }
            }
        }

        return documents;
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
                List<DynamicEntity> documents = entity.<List<DynamicEntity>>get(CaptureConstants.XML_BATCH_DOCUMENTS_KEY);

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
                    long count = docObject.getAttachments().stream().filter(element -> {
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

    public Logger getLOG()
    {
        return LOG;
    }

    public void setLOG(Logger LOG)
    {
        this.LOG = LOG;
    }
}
