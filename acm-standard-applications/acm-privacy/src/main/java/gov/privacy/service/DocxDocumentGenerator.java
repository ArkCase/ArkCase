package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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
import com.armedia.acm.correspondence.utils.WordGenerator;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;

import gov.privacy.model.SARConstants;
import gov.privacy.model.SARDocumentDescriptor;
import gov.privacy.model.SARObject;

/**
 * Document generator implementation for Microsoft Word documents.
 * 
 * @author bojan.milenkoski
 */
public class DocxDocumentGenerator implements DocumentGenerator
{
    private Logger log = LogManager.getLogger(getClass());
    private Random random = new Random();

    private WordGenerator wordGenerator;
    private EcmFileDao ecmFileDao;
    private EcmFileService ecmFileService;

    @Override
    public EcmFile generateAndUpload(SARDocumentDescriptor documentDescriptor, SARObject acmObject, String targetCmisFolderId,
                                     String targetFilename, Map<String, String> substitutions) throws DocumentGeneratorException
    {
        try
        {
            String filename = String.format("%s/acm-%020d.pdf", System.getProperty("java.io.tmpdir"), Math.abs(random.nextLong()));
            log.debug("PDF creation: using [{}] as temporary file name", filename);

            getWordGenerator().generate(new FileSystemResource(documentDescriptor.getTemplate()), new FileOutputStream(filename),
                    substitutions);

            EcmFile existing = ecmFileDao.findForContainerAttachmentFolderAndFileType(acmObject.getContainer().getId(),
                    acmObject.getContainer().getAttachmentFolder().getId(), documentDescriptor.getDoctype());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            try (InputStream fis = new FileInputStream(filename))
            {
                if (existing == null)
                {
                    return ecmFileService.upload(targetFilename, documentDescriptor.getDoctype(), "Document", fis,
                            SARConstants.MIME_TYPE_DOCX, targetFilename, authentication, targetCmisFolderId, acmObject.getObjectType(),
                            acmObject.getId());
                }
                else
                {
                    return ecmFileService.update(existing, fis, authentication);
                }

            }
        }
        catch (IOException | AcmCreateObjectFailedException | AcmUserActionFailedException e)
        {
            throw new DocumentGeneratorException("Failed to generate Word document for objectId: [" + acmObject.getId() + "], objectType: ["
                    + acmObject.getObjectType() + "] and template:[" + documentDescriptor.getTemplate() + "]", e);
        }
    }

    public WordGenerator getWordGenerator()
    {
        return wordGenerator;
    }

    public void setWordGenerator(WordGenerator wordGenerator)
    {
        this.wordGenerator = wordGenerator;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

}
