package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.ephesoft.service.AbstractBatchXMLFileListener;
import com.armedia.acm.files.FileConstants;
import com.armedia.acm.files.FileEvent;
import com.armedia.acm.files.capture.CaptureConstants;
import com.armedia.acm.files.capture.DocumentObject;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by riste.tutureski on 9/2/2015.
 */
public class ComplaintCaptureFileEventListener extends AbstractBatchXMLFileListener
{
    private Logger LOG = LoggerFactory.getLogger(getClass());
    private Long loadingDocumentsSeconds;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private SaveComplaintTransaction saveComplaintTransaction;

    @Override
    public Long getLoadingDocumentsSeconds()
    {
        return loadingDocumentsSeconds;
    }

    @Override
    public void processBatch(Map<String, DocumentObject> documents, DynamicEntity parentBatch)
    {
        if (documents != null)
        {
            // For each DocumentObject, create complaint with attachments
            documents.entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry -> createComplaint(entry.getValue()));
        }
    }

    @Override
    public boolean isAttachment(DynamicEntity entity)
    {
        if (entity != null)
        {
            String type = entity.<String> get(CaptureConstants.XML_BATCH_TYPE_KEY);

            if (type != null)
            {
                return FileConstants.XML_BATCH_ATTACHMENT.equalsIgnoreCase(type);
            }
        }

        return false;
    }

    @Override
    public List<DocumentObject> filterAttachments(DocumentObject docObject, List<DocumentObject> attachments)
    {
        List<DocumentObject> filteredAttachments = new ArrayList<>();

        if (docObject != null && docObject.getId() != null && attachments != null)
        {
            filteredAttachments = attachments.stream().filter(attachment -> checkFilter(docObject, attachment))
                    .collect(Collectors.toList());
        }

        return filteredAttachments;
    }

    @Override
    public boolean isSupported(FileEvent event)
    {
        if (super.isSupported(event))
        {
            try
            {
                if (event != null && event.getFile() != null)
                {
                    DynamicEntity entity = getEntity(event.getFile());

                    if (entity != null)
                    {
                        String batchName = entity.<String> get(CaptureConstants.XML_BATCH_CLASS_NAME_KEY);

                        if (ComplaintConstants.XML_BATCH_CLASS_NAME_VALUE.equalsIgnoreCase(batchName))
                        {
                            return true;
                        }
                    }
                }
            } catch (Exception e)
            {
                LOG.warn("Cannot check if the file {} is supported", event.getFileName());
            }
        }

        return false;
    }

    /**
     * Check if the provided attachment is for provided object
     *
     * @param docObject
     * @param attachment
     * @return
     */
    private boolean checkFilter(DocumentObject docObject, DocumentObject attachment)
    {
        if (attachment.getEntity() != null)
        {
            List<DynamicEntity> documentLevelFields = attachment.getEntity()
                    .<List<DynamicEntity>> get(FileConstants.XML_BATCH_DOCUMENT_LEVEL_FIELDS_KEY);
            String value = getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_COMPLAINT_DOC_ID, documentLevelFields);
            if (value != null && value.equalsIgnoreCase(docObject.getId()))
            {
                return true;
            }
        }
        return false;
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

            String title = entity.<String> get(CaptureConstants.XML_BATCH_DESCRIPTION_KEY);
            List<DynamicEntity> documentLevelFields = entity
                    .<List<DynamicEntity>> get(CaptureConstants.XML_BATCH_DOCUMENT_LEVEL_FIELDS_KEY);

            // Create Complaint object
            Complaint complaint = new Complaint();
            complaint.setComplaintType(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_INCIDENT_CATEGORY_KEY, documentLevelFields));
            complaint.setComplaintTitle(title);
            complaint.setDetails(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_COMPLAINT_DESCRIPTION_KEY, documentLevelFields));
            complaint.setIncidentDate(new Date());
            complaint.setOriginator(getOriginator(documentLevelFields));

            getAuditPropertyEntityAdapter().setUserId(CaptureConstants.XML_BATCH_USER);

            try
            {
                // Save complaint
                Complaint saved = getSaveComplaintTransaction().saveComplaint(complaint, null);

                String cmisFolderId = saved != null && saved.getContainer() != null && saved.getContainer().getAttachmentFolder() != null
                        ? saved.getContainer().getAttachmentFolder().getCmisFolderId() : null;
                String objectFileType = saved != null && saved.getObjectType() != null ? saved.getObjectType().toLowerCase() : null;

                if (cmisFolderId != null && objectFileType != null)
                {
                    // Save documents and attachments for complaint
                    saveAttachments(cmisFolderId, saved.getId(), saved.getObjectType(), docObject, objectFileType, "attachment");
                }
            } catch (Exception e)
            {
                LOG.error("Cannot create complaint or uploading attachments: {}", e.getMessage(), e);
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
            postalAddress.setStreetAddress(
                    getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_COMPLAINANT_STREET_ADDRESS_KEY, documentLevelFields));
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
            organization
                    .setOrganizationValue(getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_EMPLOYER_NAME_KEY, documentLevelFields));
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
            postalAddress.setStreetAddress(
                    getDocumentLevelFieldValue(ComplaintConstants.XML_BATCH_EMPLOYER_STREET_ADDRESS_KEY, documentLevelFields));
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

    public void setLoadingDocumentsSeconds(Long loadingDocumentsSeconds)
    {
        this.loadingDocumentsSeconds = loadingDocumentsSeconds;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public SaveComplaintTransaction getSaveComplaintTransaction()
    {
        return saveComplaintTransaction;
    }

    public void setSaveComplaintTransaction(SaveComplaintTransaction saveComplaintTransaction)
    {
        this.saveComplaintTransaction = saveComplaintTransaction;
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

    @Override
    public String getEventType()
    {
        return ComplaintConstants.XML_BATCH_EVENT_TYPE;
    }
}
