package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.capture.AbstractCaptureFileEvent;
import com.armedia.acm.files.capture.AbstractCaptureFileListener;
import com.armedia.acm.files.capture.CaptureConstants;
import com.armedia.acm.files.capture.DocumentObject;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import org.apache.commons.io.IOUtils;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.activation.MimetypesFileTypeMap;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by riste.tutureski on 9/2/2015.
 */
public class ComplaintCaptureFileListener extends AbstractCaptureFileListener
{
    private Logger LOG = LoggerFactory.getLogger(getClass());
    private SaveComplaintTransaction saveComplaintTransaction;
    private EcmFileService ecmFileService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private long loadingDocumentsSeconds;

    @Override
    public void onApplicationEvent(AbstractCaptureFileEvent event)
    {
        super.onApplicationEvent(event);

        if (isSupported(event) && isCreatedEvent(event))
        {
            LOG.debug("File is supported for processing. Trying to move files to working directory!");

            // Get XML batch file and all other documents related to the XML batch file
            File xmlBatch = event.getCaptureFile();
            Map<String, DocumentObject> documents = loadDocuments(xmlBatch);

            LOG.debug("Moving files to working directory - START");

            // Move XML batch and documents related to the batch to "working" folder
            File workingXmlBatch = moveFileToWorkingFolder(xmlBatch);
            Map<String, DocumentObject> workingDocuments = moveToWorkingFolder(documents);

            LOG.debug("Moving files to working directory - END");

            if (workingXmlBatch != null)
            {
                LOG.debug("Start processing Capture File Event!");

                // If we moved the batch to "working" folder, create needed complaints
                processBatch(workingDocuments);

                // Move all files related to the batch to "completed" folder after processing
                LOG.debug("Moving files to completed directory - START");

                moveFileToCompletedFolder(workingXmlBatch);
                moveToCompletedFolder(workingDocuments);

                LOG.debug("Moving files to completed directory - END");
            }
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
    private Map<String, DocumentObject> loadDocuments(File xmlBatch)
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
            // List all entries in the map (each entry is complaint)
            for (Map.Entry<String, DocumentObject> entry : documents.entrySet())
            {
                DocumentObject docObject = entry.getValue();

                // Increase "loaded" if document object is not null (complaint itself is one document)
                if (docObject != null && docObject.getDocument() != null)
                {
                    loaded++;
                }

                // Increase "loaded" if provided document (complaint) has attachments related to it
                if (docObject != null && docObject.getAttachments() != null)
                {
                    long count = docObject.getAttachments().stream().filter(element -> element.getDocument() != null).count();
                    loaded += count;
                }
            }
        }

        return loaded;
    }

    /**
     * Process batch - Create complaints and upload attachments
     *
     * @param documents
     */
    public void processBatch( Map<String, DocumentObject> documents)
    {
        if (documents != null)
        {
            // For each DocumentObject, create complaint with attachments
            for (Map.Entry<String, DocumentObject> entry : documents.entrySet())
            {
                if (entry.getValue() != null)
                {
                    createComplaint(entry.getValue());
                }
            }
        }
    }

    /**
     * Create Complaint
     *
     * @param docObject
     */
    private void createComplaint(DocumentObject docObject)
    {
        if (docObject != null && docObject.getEntity() != null)
        {
            DynamicEntity entity = docObject.getEntity();

            String title = entity.<String>get(CaptureConstants.XML_BATCH_DESCRIPTION_KEY);
            List<DynamicEntity> documentLevelFields = entity.<List<DynamicEntity>>get(CaptureConstants.XML_BATCH_DOCUMENT_LEVEL_FIELDS_KEY);

            // Create Complaint object
            Complaint complaint = new Complaint();
            complaint.setComplaintType(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_INCIDENT_CATEGORY_KEY, documentLevelFields));
            complaint.setComplaintTitle(title);
            complaint.setDetails(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_COMPLAINT_DESCRIPTION_KEY, documentLevelFields));
            complaint.setIncidentDate(new Date());
            complaint.setOriginator(getOriginator(documentLevelFields));

            getAuditPropertyEntityAdapter().setUserId(CaptureConstants.XML_BATCH_USER);

            Complaint saved = null;
            try
            {
                // Save complaint
                saved = getSaveComplaintTransaction().saveComplaint(complaint, null);
            }
            catch (PipelineProcessException e)
            {
                LOG.error("Cannot create complaint: " + e.getMessage(), e);
            }

            if (saved != null)
            {
                // Save documents and attachments for complaint
                saveComplaintAttachments(saved, docObject);
            }
        }
    }

    /**
     * Create person association information from XML batch information
     *
     * @param documentLevelFields
     * @return
     */
    private PersonAssociation getOriginator(List<DynamicEntity> documentLevelFields)
    {
        PersonAssociation pa = new PersonAssociation();
        Person p = new Person();

        // Create complainant address
        PostalAddress a = getComplainantAddress(documentLevelFields);
        if (a != null)
        {
            p.getAddresses().add(a);
        }

        // Create complainant contact
        ContactMethod c = getComplainantContact(documentLevelFields);
        if (c != null)
        {
            p.getContactMethods().add(c);
        }

        // Create employer
        Organization o = getEmployer(documentLevelFields);
        if (o != null)
        {
            // Create employer address
            PostalAddress oa = getEmployerAddress(documentLevelFields);
            if (oa != null)
            {
                o.getAddresses().add(oa);
            }

            // Create employer contact
            ContactMethod oc = getEmployerContact(documentLevelFields);
            if (oc != null)
            {
                o.getContactMethods().add(oc);
            }

            p.getOrganizations().add(o);
        }

        // Set first and last name of the complainant
        p.setGivenName(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_COMPLAINANT_FIRST_NAME_KEY, documentLevelFields));
        p.setFamilyName(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_COMPLAINANT_LAST_NAME_KEY, documentLevelFields));

        pa.setPerson(p);
        pa.setPersonType("Initiator");

        return pa;
    }

    /**
     * Create postal address information for complainant from XML batch information
     *
     * @param documentLevelFields
     * @return
     */
    private PostalAddress getComplainantAddress(List<DynamicEntity> documentLevelFields)
    {
        PostalAddress postalAddress = null;

        if (documentLevelFields != null)
        {
            postalAddress = new PostalAddress();

            postalAddress.setType("Home");
            postalAddress.setStreetAddress(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_COMPLAINANT_STREET_ADDRESS_KEY, documentLevelFields));
            postalAddress.setCity(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_COMPLAINANT_CITY_KEY, documentLevelFields));
            postalAddress.setState(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_COMPLAINANT_STATE_KEY, documentLevelFields));
            postalAddress.setZip(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_COMPLAINANT_ZIP_CODE_KEY, documentLevelFields));
        }

        return postalAddress;
    }

    /**
     * Create contact information for complainant from XML batch information
     *
     * @param documentLevelFields
     * @return
     */
    private ContactMethod getComplainantContact(List<DynamicEntity> documentLevelFields)
    {
        ContactMethod contactMethod = null;

        if (documentLevelFields != null)
        {
            contactMethod = new ContactMethod();

            contactMethod.setType("Phone");
            contactMethod.setValue(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_COMPLAINANT_PHONE_KEY, documentLevelFields));
        }

        return contactMethod;
    }

    /**
     * Get employer information from XML batch information
     *
     * @param documentLevelFields
     * @return
     */
    private Organization getEmployer(List<DynamicEntity> documentLevelFields)
    {
        Organization organization = null;

        if (documentLevelFields != null)
        {
            organization = new Organization();

            organization.setOrganizationType("Corporation");
            organization.setOrganizationValue(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_EMPLOYER_NAME_KEY, documentLevelFields));
        }

        return organization;
    }

    /**
     * Create employer address information from XML batch information
     *
     * @param documentLevelFields
     * @return
     */
    private PostalAddress getEmployerAddress(List<DynamicEntity> documentLevelFields)
    {
        PostalAddress postalAddress = null;

        if (documentLevelFields != null)
        {
            postalAddress = new PostalAddress();

            postalAddress.setType("Business");
            postalAddress.setStreetAddress(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_EMPLOYER_STREET_ADDRESS_KEY, documentLevelFields));
            postalAddress.setCity(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_EMPLOYER_CITY_KEY, documentLevelFields));
            postalAddress.setState(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_EMPLOYER_STATE_KEY, documentLevelFields));
            postalAddress.setZip(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_EMPLOYER_ZIP_CODE_KEY, documentLevelFields));
        }

        return postalAddress;
    }

    /**
     * Create employer contact information from XML batch information
     *
     * @param documentLevelFields
     * @return
     */
    private ContactMethod getEmployerContact(List<DynamicEntity> documentLevelFields)
    {
        ContactMethod contactMethod = null;

        if (documentLevelFields != null)
        {
            contactMethod = new ContactMethod();

            contactMethod.setType("Phone");
            contactMethod.setValue(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_EMPLOYER_PHONE_KEY, documentLevelFields));
        }

        return contactMethod;
    }

    /**
     * Save all documents as attachments related to given complaint
     *
     * @param complaint
     * @param docObject
     */
    private void saveComplaintAttachments(Complaint complaint, DocumentObject docObject)
    {
        if (docObject != null)
        {
            // Save Complaint PDF document as attachment in Alfresco
            if (docObject.getDocument() != null)
            {
                saveComplaintAttachment(complaint, docObject, complaint.getObjectType().toLowerCase());
            }

            // Save other documents as attachments
            if (docObject.getAttachments() != null)
            {
                docObject.getAttachments().stream().forEach(doc -> saveComplaintAttachment(complaint, doc, "attachment"));
            }
        }
    }

    /**
     * Save document as attachment for given complaint
     *
     * @param complaint
     * @param docObject
     * @param type
     */
    public void saveComplaintAttachment(Complaint complaint, DocumentObject docObject, String type)
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
            // EcmFileService - we need userID which in this case is set to CaptureConstants.XML_BATCH_USER value)
            String contentType = mimetypesFileTypeMap.getContentType(docObject.getDocument());
            Authentication auth = new AcmAuthentication(null, null, null, true, CaptureConstants.XML_BATCH_USER);

            // Create multipart file object - used "upload" service require it and using this service method is the best
            // way to upload file for given object - it creates AcmContainer object that we need for uploading
            AcmMultipartFile file = new AcmMultipartFile(
                    docObject.getDocument().getName(),
                    docObject.getDocument().getName(),
                    contentType,
                    false,
                    docObject.getDocument().length(),
                    bytes,
                    cloneIS,
                    true);

            // Upload file
            getEcmFileService().upload(file.getOriginalFilename(), type, file, auth, complaint.getContainer().getFolder().getCmisFolderId(), complaint.getObjectType(), complaint.getComplaintId());
        }
        catch(Exception e)
        {
            LOG.error("Cannot save attachment." + e.getMessage(), e);
        }
    }

    /**
     * This will return the value for given field name from the list of fields
     *
     * @param name
     * @param documentLevelFields
     * @return
     */
    private String getDocumentLevelFieldValue(String name, List<DynamicEntity> documentLevelFields)
    {
        String retval = null;

        if (name != null && documentLevelFields != null)
        {
            // Try to find DynamicEntity object for given name
            Optional<DynamicEntity> found = documentLevelFields.stream().filter(element -> name.equals(element.<String>get(CaptureConstants.XML_BATCH_NAME_KEY))).findFirst();
            if (found != null && found.isPresent())
            {
                retval = found.get().<String>get(CaptureConstants.XML_BATCH_VALUE_KEY);
            }
        }

        return retval;
    }

    /**
     * Move all documents related to current batch to working folder
     *
     * @param documents
     * @return
     */
    private Map<String, DocumentObject> moveToWorkingFolder(Map<String, DocumentObject> documents)
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
                File workingDocument = moveFileToWorkingFolder(value.getDocument());

                if (value.getAttachments() != null)
                {
                    // Moving attachments
                    moveAttachmentsToWorkingFolder(value);
                }

                value.setDocument(workingDocument);
                workingDocuments.put(key, new DocumentObject(key, workingDocument, value.getAttachments(), value.getEntity()));
            }
        }

        return workingDocuments;
    }

    /**
     * Move attachments for given complaint to working folder
     *
     * @param docObject
     */
    private void moveAttachmentsToWorkingFolder(DocumentObject docObject)
    {
        if (docObject != null && docObject.getAttachments() != null)
        {
            List<DocumentObject> workingAttachments = new ArrayList<DocumentObject>();
            docObject.getAttachments().stream().forEach(element -> {
                File workingAttachment = moveFileToWorkingFolder(element.getDocument());
                if (workingAttachment != null)
                {
                    workingAttachments.add(new DocumentObject(element.getId(), workingAttachment, null, element.getEntity()));
                }
            });

            docObject.setAttachments(workingAttachments);
        }
    }

    /**
     * Move all documents and attachments to completed folder
     *
     * @param documents
     */
    private void moveToCompletedFolder(Map<String, DocumentObject> documents)
    {
        if (documents != null)
        {
            // Move each document to complated folder
            for (Map.Entry<String, DocumentObject> entry : documents.entrySet())
            {
                String key = entry.getKey();
                DocumentObject value = entry.getValue();

                moveFileToCompletedFolder(value.getDocument());

                if (value.getAttachments() != null)
                {
                    // Move each attachment to completed folder
                    value.getAttachments().stream().forEach(element -> moveFileToCompletedFolder(element.getDocument()));
                }
            }
        }
    }

    @Override
    public String getBatchClassNameValue()
    {
        return ComplaintConstants.XML_BATCH_CLASS_NAME_VALUE;
    }

    @Override
    public String getDocumentObjectID()
    {
        return ComplaintConstants.XML_BATCH_COMPLAINT_DOC_ID;
    }

    @Override
    public String getOXMFilePath() { return ComplaintConstants.OXM_FILE_PATH; }

    public SaveComplaintTransaction getSaveComplaintTransaction()
    {
        return saveComplaintTransaction;
    }

    public void setSaveComplaintTransaction(SaveComplaintTransaction saveComplaintTransaction)
    {
        this.saveComplaintTransaction = saveComplaintTransaction;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public long getLoadingDocumentsSeconds()
    {
        return loadingDocumentsSeconds;
    }

    public void setLoadingDocumentsSeconds(int loadingDocumentsSeconds)
    {
        this.loadingDocumentsSeconds = loadingDocumentsSeconds;
    }

    @Override
    public Logger getLOG()
    {
        return LOG;
    }

    @Override
    public void setLOG(Logger LOG)
    {
        super.setLOG(LOG);
        this.LOG = LOG;
    }
}
