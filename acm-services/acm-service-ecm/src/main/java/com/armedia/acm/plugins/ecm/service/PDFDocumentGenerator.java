package com.armedia.acm.plugins.ecm.service;
/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.configuration.service.FileConfigurationService;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.pdf.PdfServiceException;
import com.armedia.acm.pdf.service.PdfService;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class PDFDocumentGenerator<T>
{
    private final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String MIME_TYPE_PDF = "application/pdf";
    private String NEW_FILE = "NEW_FILE";
    private String FILE_ID = "FILE_ID";
    private String FILE_VERSION = "FILE_VERSION";

    private static final String PDF_STYLESHEETS_LOCATION = "pdf-stylesheets";

    private T businessObject;

    private EcmFileService ecmFileService;

    private EcmFileDao ecmFileDao;

    private PdfService pdfService;

    private FileConfigurationService fileConfigurationService;

    private FolderAndFilesUtils folderAndFilesUtils;

    private Logger log = LogManager.getLogger(getClass());

    public void generatePdf(String objectType, Long objectId, AbstractPipelineContext ctx, Authentication authentication,
            T businessObject, AcmContainer container,
            String stylesheet, String documentName, String fileNameFormat)
            throws ParserConfigurationException, PipelineProcessException
    {
        if (businessObject != null)
        {
            Document document = buildXmlForPdfDocument(businessObject, ctx);
            Source source = new DOMSource(document);
            String filename = null;

            try
            {
                InputStream xslStream = fileConfigurationService.getInputStreamFromConfiguration(PDF_STYLESHEETS_LOCATION + "/" + stylesheet);
                URI baseURI = fileConfigurationService.getLocationUriFromConfiguration(PDF_STYLESHEETS_LOCATION);
                filename = getPdfService().generatePdf(xslStream, baseURI, source);
                log.debug("Created {} document [{}]", documentName, filename);

                String arkcaseFilename = String.format(fileNameFormat, objectId);
                String uniqueFileName = folderAndFilesUtils.createUniqueIdentificator(arkcaseFilename);

                AcmFolder targetFolder = container.getAttachmentFolder() == null
                        ? container.getFolder()
                        : container.getAttachmentFolder();

                String targetFolderId = targetFolder.getCmisFolderId();
                Long targetFolderArkCaseId = targetFolder.getId();

                EcmFile existing = ecmFileDao.findForContainerAttachmentFolderAndFileType(container.getId(),
                        targetFolderArkCaseId, documentName);

                try (InputStream fis = new FileInputStream(filename))
                {
                    if (existing == null)
                    {
                        EcmFile ecmFile = ecmFileService.upload(arkcaseFilename,
                                documentName, "Document", fis, MIME_TYPE_PDF,
                                uniqueFileName, authentication, targetFolderId, objectType, objectId);
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
            catch (PdfServiceException | AcmCreateObjectFailedException | AcmUserActionFailedException | IOException | URISyntaxException e)
            {
                log.error("Unable to create {} document for object [{}]",
                        documentName, objectId, e);
            }
            finally
            {
                FileUtils.deleteQuietly(new File(filename));
            }
        }
    }

    public abstract Document buildXmlForPdfDocument(T businessObject, AbstractPipelineContext ctx)
            throws ParserConfigurationException, PipelineProcessException;

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
    public void addElement(Document doc, Element parent, String elemName,
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

    /**
     * A helper method which appends the participants to the document.
     *
     * @param participants
     *            List of AcmParticipants.
     * @param document
     *            The document on which we append the participants.
     * @param rootElem
     *            Root elemen of the document.
     */
    public void addParticipantsInXmlDocument(List<AcmParticipant> participants, Document document, Element rootElem, String elementName,
            String elementType)
    {
        if (!participants.isEmpty())
        {
            Element participantsElement = document.createElement("participants");
            rootElem.appendChild(participantsElement);
            for (AcmParticipant participant : participants)
            {
                Element participantElement = document.createElement("participant");
                participantsElement.appendChild(participantElement);
                if (!elementName.isEmpty())
                {
                    addElement(document, participantElement, "participantName", participant.getParticipantLdapId(), false);
                }
                if (!elementType.isEmpty())
                {
                    addElement(document, participantElement, "participantType", participant.getParticipantType(), false);
                }
            }
        }
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

    public FileConfigurationService getFileConfigurationService()
    {
        return fileConfigurationService;
    }

    public void setFileConfigurationService(FileConfigurationService fileConfigurationService)
    {
        this.fileConfigurationService = fileConfigurationService;
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

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }
}
