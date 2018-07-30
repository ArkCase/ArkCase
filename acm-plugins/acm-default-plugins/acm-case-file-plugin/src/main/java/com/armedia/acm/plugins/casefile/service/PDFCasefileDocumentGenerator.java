package com.armedia.acm.plugins.casefile.service;
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

import static com.armedia.acm.plugins.casefile.model.CaseFileConstants.FILE_ID;
import static com.armedia.acm.plugins.casefile.model.CaseFileConstants.FILE_VERSION;
import static com.armedia.acm.plugins.casefile.model.CaseFileConstants.MIME_TYPE_PDF;
import static com.armedia.acm.plugins.casefile.model.CaseFileConstants.NEW_FILE;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.pdf.PdfServiceException;
import com.armedia.acm.pdf.service.PdfService;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.apache.commons.io.FileUtils;
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
import java.util.List;

public class PDFCasefileDocumentGenerator<D extends AcmAbstractDao, T extends CaseFile>
{
    private final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private D dao;

    private T businessObject;

    private EcmFileService ecmFileService;

    private EcmFileDao ecmFileDao;

    private PdfService pdfService;

    private Logger log = LoggerFactory.getLogger(getClass());

    public void generatePdf(String objectType, Long complaintId) throws ParserConfigurationException
    {
        generatePdf(objectType, complaintId, null);
    }

    public void generatePdf(String objectType, Long complaintId, CaseFilePipelineContext ctx) throws ParserConfigurationException
    {
        if (getDao().getSupportedObjectType().equals(objectType))
        {

            T businessObject = (T) getDao().find(complaintId);

            if (businessObject != null)
            {
                Document document = (Document) buildXmlForPdfDocument(businessObject);
                Source source = new DOMSource(document);
                String filename = null;

                try
                {
                    filename = getPdfService().generatePdf(new File(CaseFileConstants.CASEFILE_STYLESHEET), source);
                    log.debug("Created {} document [{}]", CaseFileConstants.CASEFILE_DOCUMENT, filename);

                    String arkcaseFilename = String.format(CaseFileConstants.CASEFILE_FILENAMEFORMAT,
                            businessObject.getId());

                    EcmFile existing = ecmFileDao.findForContainerAttachmentFolderAndFileType(businessObject.getContainer().getId(),
                            businessObject.getContainer().getAttachmentFolder().getId(), CaseFileConstants.CASEFILE_DOCUMENT);

                    String targetFolderId = businessObject.getContainer().getAttachmentFolder() == null
                            ? businessObject.getContainer().getFolder().getCmisFolderId()
                            : businessObject.getContainer().getAttachmentFolder().getCmisFolderId();

                    Authentication authentication = ctx.getAuthentication();

                    try (InputStream fis = new FileInputStream(filename))
                    {
                        if (existing == null)
                        {
                            EcmFile ecmFile = ecmFileService.upload(arkcaseFilename,
                                    CaseFileConstants.CASEFILE_DOCUMENT, "Document", fis,
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
                            CaseFileConstants.CASEFILE_DOCUMENT, businessObject.getId(), e);
                }
                finally
                {
                    FileUtils.deleteQuietly(new File(filename));
                }
            }

        }

    }

    public Document buildXmlForPdfDocument(T businessObject) throws ParserConfigurationException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        // create <casefile>, the root of the document
        Element rootElem = document.createElement("casefile");
        document.appendChild(rootElem);

        CaseFile caseFile = (CaseFile) businessObject;

        addElement(document, rootElem, "caseTitle", caseFile.getTitle(), true);
        addElement(document, rootElem, "caseType", caseFile.getCaseType(), true);
        addElement(document, rootElem, "caseDetails", caseFile.getDetails() != null ? caseFile.getDetails() : "N/A", false);

        addElement(document, rootElem, "initiator", caseFile.getOriginator().getPerson().getFullName(), true);

        if (!caseFile.getPersonAssociations().isEmpty())
        {
            Element peopleElement = document.createElement("people");
            rootElem.appendChild(peopleElement);

            List<PersonAssociation> people = caseFile.getPersonAssociations();
            for (PersonAssociation person : people)
            {
                Element personAssocElement = document.createElement("person");
                peopleElement.appendChild(personAssocElement);
                addElement(document, personAssocElement, "personType", person.getPersonType(), false);
                addElement(document, personAssocElement, "personName", person.getPerson().getFullName(), false);
            }
        }

        if (!caseFile.getParticipants().isEmpty())
        {
            Element participantsElement = document.createElement("participants");
            rootElem.appendChild(participantsElement);

            List<AcmParticipant> participants = caseFile.getParticipants();
            for (AcmParticipant participant : participants)
            {
                Element participantElement = document.createElement("participant");
                participantsElement.appendChild(participantElement);
                addElement(document, participantElement, "participantType", participant.getParticipantType(), false);
                addElement(document, participantElement, "participantName", participant.getParticipantLdapId(), false);
            }
        }
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

    public PdfService getPdfService()
    {
        return pdfService;
    }

    public void setPdfService(PdfService pdfService)
    {
        this.pdfService = pdfService;
    }

    public DateTimeFormatter getDatePattern()
    {
        return datePattern;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }
}
