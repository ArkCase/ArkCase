package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import static gov.foia.model.FOIAConstants.MIME_TYPE_PDF;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.core.exceptions.CorrespondenceMergeFieldVersionException;
import com.armedia.acm.pdf.PdfServiceException;
import com.armedia.acm.pdf.service.PdfService;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import gov.foia.model.FOIADocumentDescriptor;
import gov.foia.model.FOIAObject;

/**
 * Created by dmiller on 8/18/2016.
 */
public class PDFDocumentGenerator implements DocumentGenerator
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private EcmFileService ecmFileService;
    private EcmFileDao ecmFileDao;
    private PdfService pdfService;
    private FolderAndFilesUtils folderAndFilesUtils;

    @Override
    public EcmFile generateAndUpload(FOIADocumentDescriptor documentDescriptor, FOIAObject acmObject, String targetCmisFolderId,
            String targetFilename, Map<String, String> substitutions)
            throws DocumentGeneratorException, CorrespondenceMergeFieldVersionException
    {
        String filename = null;

        try
        {
            filename = getPdfService().generatePdf(documentDescriptor.getTemplate(), substitutions);
            log.debug("Created {} document [{}]", documentDescriptor.getDoctype(), filename);

            String arkcaseFilename = String.format(documentDescriptor.getFilenameFormat(), acmObject.getId());

            EcmFile existing = ecmFileDao.findForContainerAttachmentFolderAndFileType(acmObject.getContainer().getId(),
                    acmObject.getContainer().getAttachmentFolder().getId(), documentDescriptor.getDoctype());

            String targetFolderId = acmObject.getContainer().getAttachmentFolder() == null
                    ? acmObject.getContainer().getFolder().getCmisFolderId()
                    : acmObject.getContainer().getAttachmentFolder().getCmisFolderId();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            try (FileInputStream fis = new FileInputStream(filename))
            {
                if (existing == null)
                {
                    return ecmFileService.upload(arkcaseFilename, documentDescriptor.getDoctype(), "Document", fis, MIME_TYPE_PDF,
                            arkcaseFilename, authentication, targetFolderId, acmObject.getObjectType(), acmObject.getId());
                }
                else
                {
                    return ecmFileService.update(existing, fis, authentication);
                }
            }
        }
        catch (PdfServiceException | AcmCreateObjectFailedException | AcmUserActionFailedException | IOException e)
        {
            throw new DocumentGeneratorException("Failed to generate Word document for objectId: [" + acmObject.getId() + "], objectType: ["
                    + acmObject.getObjectType() + "] and template:[" + documentDescriptor.getTemplate() + "]", e);
        }
        finally
        {
            FileUtils.deleteQuietly(new File(filename));
        }
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

    public FolderAndFilesUtils getFolderAndFilesUtils() {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils) {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }
}
