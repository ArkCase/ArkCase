package com.armedia.acm.plugins.complaint.service;

import static com.armedia.acm.plugins.complaint.model.CloseComplaintConstants.*;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.pdf.PdfServiceException;
import com.armedia.acm.pdf.service.PdfService;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.mule.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

public class PDFCloseComplaintDocumentGenerator<D extends AcmAbstractDao, T extends Complaint>
{
    private final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private D dao;

    private D closeComplaintRequestDao;

    private T businessObject;

    private EcmFileService ecmFileService;

    private EcmFileDao ecmFileDao;

    private PdfService pdfService;

    private CaseFileDao caseFileDao;

    private Logger log = LoggerFactory.getLogger(getClass());

    public void generatePdf(String objectType, Long complaintId) throws ParserConfigurationException
    {
        generatePdf(objectType, complaintId, null);
    }

    public void generatePdf(String objectType, Long complaintId, CloseComplaintPipelineContext ctx) throws ParserConfigurationException
    {
        if (getDao().getSupportedObjectType().equals(objectType))
        {

            T businessObject = (T) getDao().find(complaintId);

            if (businessObject != null)
            {
                Document document = (Document) buildXmlFile(businessObject, ctx);

                Source source = new DOMSource(document);
                String filename = null;

                try
                {
                    Complaint complaint = (Complaint) getDao().find(complaintId);

                    filename = getPdfService().generatePdf(new File(CLOSE_COMPLAINT_STYLESHEET), source);
                    log.debug("Created {} document [{}]", CLOSE_COMPLAINT_DOCUMENT, filename);

                    String arkcaseFilename = String.format(CLOSE_COMPLAINT_FILENAMEFORMAT,
                            businessObject.getId());

                    EcmFile existing = ecmFileDao.findForContainerAttachmentFolderAndFileType(complaint.getContainer().getId(),
                            complaint.getContainer().getAttachmentFolder().getId(), CLOSE_COMPLAINT_DOCUMENT);

                    String targetFolderId = complaint.getContainer().getAttachmentFolder() == null
                            ? complaint.getContainer().getFolder().getCmisFolderId()
                            : complaint.getContainer().getAttachmentFolder().getCmisFolderId();

                    Authentication authentication = ctx.getAuthentication();

                    try (InputStream fis = new FileInputStream(filename))
                    {
                        if (existing == null)
                        {
                            EcmFile ecmFile = ecmFileService.upload(arkcaseFilename,
                                    CLOSE_COMPLAINT_DOCUMENT.toLowerCase(), "Document", fis,
                                    MIME_TYPE_PDF,
                                    arkcaseFilename, authentication, targetFolderId, objectType, businessObject.getId());
                            if (ctx != null)
                            {
                                ctx.addProperty(NEW_FILE, true);
                                ctx.addProperty(FILE_ID, ecmFile.getId());
                            }
                        }
                        else
                        {
                            EcmFile ecmFile = ecmFileService.update(existing, fis, authentication);
                            if (ctx != null)
                            {
                                ctx.addProperty(NEW_FILE, false);
                                ctx.addProperty(FILE_ID, ecmFile.getId());
                                ctx.addProperty(FILE_VERSION, ecmFile.getActiveVersionTag());
                            }
                        }
                    }
                }
                catch (PdfServiceException | AcmCreateObjectFailedException | AcmUserActionFailedException | IOException e)
                {
                    log.error("Unable to create {} document for request [{}]",
                            CLOSE_COMPLAINT_DOCUMENT, businessObject.getId(), e);
                }
                finally
                {
                    FileUtils.deleteQuietly(new File(filename));
                }
            }

        }

    }

    // buildXmlFile
    public Document buildXmlFile(T businessObject, CloseComplaintPipelineContext ctx) throws ParserConfigurationException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        // create <complaint>, the root of the document
        Element rootElem = document.createElement("closeComplaint");
        document.appendChild(rootElem);

        CloseComplaintRequest closeComplaintRequest = ctx.getCloseComplaintRequest();
        Complaint complaint = (Complaint) businessObject;

        if (complaint.getDisposition().getExistingCaseNumber() != null)
        {
            CaseFile existingCase = caseFileDao.findByCaseNumber(complaint.getDisposition().getExistingCaseNumber());
            addElement(document, rootElem, "existingCaseNumber", complaint.getDisposition().getExistingCaseNumber(),
                    true);
            addElement(document, rootElem, "existingCaseSearch", "Search", true);
        }

        addElement(document, rootElem, "closeDate", closeComplaintRequest.getDisposition().getCloseDate().toString(), true);
        addElement(document, rootElem, "complaintNumber", complaint.getComplaintNumber(), true);
        addElement(document, rootElem, "complaintDisposition", complaint.getDisposition().getDispositionType(), true);

        addElement(document, rootElem, "referExternalNameOfAgency", complaint.getDisposition().getReferExternalOrganizationName(), true);
        addElement(document, rootElem, "referExternalDate", "KLAJ DATA!", true);
        addElement(document, rootElem, "referExternalContactName", complaint.getDisposition().getReferExternalContactPersonName(), true);

        addElement(document, rootElem, "referExternalContactType", complaint.getDisposition().getReferExternalContactMethod().getType(),
                true);
        addElement(document, rootElem, "referExternalContactValue", complaint.getDisposition().getReferExternalContactMethod().getValue(),
                true);

        // addElement(document, rootElem, "existingCaseTitle", existingCase.getTitle(),
        // true);
        // addElement(document, rootElem, "existingCaseCreationDate", existingCase.getCreated().toString(),
        // true);
        // addElement(document, rootElem, "existingCasePriority", existingCase.getPriority(),
        // true);

        // if (complaint.getDefaultAddress() != null)
        // {
        // PostalAddress location = complaint.getDefaultAddress();
        // addElement(document, rootElem, "locationType", location.getType(), false);
        // addElement(document, rootElem, "locationAddress", location.getStreetAddress(), false);
        // addElement(document, rootElem, "locationCity", location.getCity(), false);
        // addElement(document, rootElem, "locationState", location.getState(), false);
        // addElement(document, rootElem, "locationZip", location.getZip(), false);
        // addElement(document, rootElem, "locationAddedBy", location.getCreator(), false);
        // }
        // else
        // {
        // addElement(document, rootElem, "locationType", "N/A", false);
        // addElement(document, rootElem, "locationAddress", "N/A", false);
        // addElement(document, rootElem, "locationCity", "N/A", false);
        // addElement(document, rootElem, "locationState", "N/A", false);
        // addElement(document, rootElem, "locationZip", "N/A", false);
        // addElement(document, rootElem, "locationAddedBy", "N/A", false);
        // }
        //
        // if (!complaint.getPersonAssociations().isEmpty())

        // if (!closeComplaintRequest.getParticipants().isEmpty())
        // {
        // Element participantsElement = document.createElement("participants");
        // rootElem.appendChild(participantsElement);
        //
        // List<AcmParticipant> participants = closeComplaintRequest.getParticipants();
        // for (AcmParticipant participant : participants)
        // {
        // Element participantElement = document.createElement("participant");
        // participantsElement.appendChild(participantElement);
        // addElement(document, participantElement, "participantName", participant.getParticipantLdapId(), false);
        // }
        // }
        return document;

    }

    /**
     * A helper method that simplifies this class.
     *
     * @param doc
     *            the DOM Document, used as a factory for
     *            creating Elements.
     * @param parent
     *            the DOM Element to add the child to.
     * @param elemName
     *            the name of the XML element to create.
     * @param elemValue
     *            the text content of the new XML element.
     * @param required
     *            if true, insert 'required="true"' attribute.
     */
    private void addElement(Document doc, Element parent, String elemName,
            String elemValue, boolean required)
    {
        Element elem = doc.createElement(elemName);
        elem.appendChild(doc.createTextNode(elemValue));
        if (required)
        {
            elem.setAttribute("required", "true");
        }
        parent.appendChild(elem);
    }

    public DateTimeFormatter getDatePattern()
    {
        return datePattern;
    }

    public D getDao()
    {
        return dao;
    }

    public void setDao(D dao)
    {
        this.dao = dao;
    }

    public T getBusinessObject()
    {
        return businessObject;
    }

    public void setBusinessObject(T businessObject)
    {
        this.businessObject = businessObject;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public PdfService getPdfService()
    {
        return pdfService;
    }

    public void setPdfService(PdfService pdfService)
    {
        this.pdfService = pdfService;
    }

    public D getCloseComplaintRequestDao()
    {
        return closeComplaintRequestDao;
    }

    public void setCloseComplaintRequestDao(D closeComplaintRequestDao)
    {
        this.closeComplaintRequestDao = closeComplaintRequestDao;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }
}
