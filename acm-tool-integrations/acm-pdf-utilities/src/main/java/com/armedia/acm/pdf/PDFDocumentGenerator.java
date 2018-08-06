package com.armedia.acm.pdf;
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.pdf.service.PdfService;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.format.DateTimeFormatter;

public abstract class PDFDocumentGenerator<T>
{
    private final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String MIME_TYPE_PDF = "application/pdf";
    private String NEW_FILE = "NEW_FILE";
    private String FILE_ID = "FILE_ID";
    private String FILE_VERSION = "FILE_VERSION";

    private T businessObject;

    private EcmFileService ecmFileService;

    private EcmFileDao ecmFileDao;

    private PdfService pdfService;

    private Logger log = LoggerFactory.getLogger(getClass());

    public void generatePdf(String objectType, Long objectId, AbstractPipelineContext ctx, Authentication authentication,
            T businessObject, AcmContainer container,
            String stylesheet, String documentName, String fileNameFormat)
            throws ParserConfigurationException
    {
        if (businessObject != null)
        {
            Document document = buildXmlForPdfDocument(businessObject);
            Source source = new DOMSource(document);
            String filename = null;

            try
            {
                filename = getPdfService().generatePdf(new File(stylesheet), source);
                log.debug("Created {} document [{}]", documentName, filename);

                String arkcaseFilename = String.format(fileNameFormat,
                        objectId);

                EcmFile existing = ecmFileDao.findForContainerAttachmentFolderAndFileType(container.getId(),
                        container.getAttachmentFolder().getId(), documentName);

                String targetFolderId = container.getAttachmentFolder() == null
                        ? container.getFolder().getCmisFolderId()
                        : container.getAttachmentFolder().getCmisFolderId();

                try (InputStream fis = new FileInputStream(filename))
                {
                    if (existing == null)
                    {
                        EcmFile ecmFile = ecmFileService.upload(arkcaseFilename,
                                documentName, "Document", fis,
                                MIME_TYPE_PDF,
                                arkcaseFilename, authentication, targetFolderId, objectType, objectId);
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
                        documentName, objectId, e);
            }
            finally
            {
                FileUtils.deleteQuietly(new File(filename));
            }
        }
    }

    public abstract Document buildXmlForPdfDocument(T businessObject) throws ParserConfigurationException;

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
