/**
 *
 */
package gov.privacy.pipeline.postsave;

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

import static gov.privacy.model.SARConstants.FILE_ID;
import static gov.privacy.model.SARConstants.NEW_FILE;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.privacy.dao.SARDao;
import gov.privacy.model.SARConstants;
import gov.privacy.model.SARDocumentDescriptor;
import gov.privacy.model.SubjectAccessRequest;
import gov.privacy.service.DocumentGenerator;
import gov.privacy.service.SARDocumentGeneratorService;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class RequestDocumentHandler implements PipelineHandler<SubjectAccessRequest, CaseFilePipelineContext>
{
    private EcmFileService ecmFileService;
    private DocumentGenerator documentGenerator;
    private SARDocumentGeneratorService documentGeneratorService;
    private SARDao requestDao;

    /**
     * Logger instance.
     */
    private transient final Logger log = LogManager.getLogger(getClass());

    @Override
    public void execute(SubjectAccessRequest request, CaseFilePipelineContext ctx)
            throws PipelineProcessException
    {
        log.debug("Entering pipeline handler for object: [{}]", request);

        if (ctx.isNewCase())
        {
            // ensure the data changes of all prior handlers is visible to this handler
            requestDao.getEm().flush();

            SubjectAccessRequest businessObject = requestDao.find(request.getId());
            SARDocumentDescriptor documentDescriptor = documentGeneratorService.getDocumentDescriptor(businessObject, SARConstants.REQ);
            String arkcaseFilename = String.format(documentDescriptor.getFilenameFormat(), businessObject.getId());
            String targetFolderId = businessObject.getContainer().getAttachmentFolder() == null
                    ? businessObject.getContainer().getFolder().getCmisFolderId()
                    : businessObject.getContainer().getAttachmentFolder().getCmisFolderId();
            try
            {
                EcmFile ecmFile = null;
                try
                {
                    ecmFile = documentGenerator.generateAndUpload(documentDescriptor, businessObject, targetFolderId, arkcaseFilename,
                            documentGeneratorService.getReportSubstitutions(businessObject));
                }
                catch (Exception e)
                {
                    throw new PipelineProcessException(e);
                }

                if (ctx != null)
                {
                    ctx.addProperty(NEW_FILE, true);
                    ctx.addProperty(FILE_ID, ecmFile.getId());
                }
            }
            catch (Exception e)
            {
                throw new PipelineProcessException(e);
            }
        }

        log.debug("Exiting pipeline handler for object: [{}]", request);
    }

    @Override
    public void rollback(SubjectAccessRequest request, CaseFilePipelineContext ctx) throws PipelineProcessException
    {
        if (ctx.hasProperty(NEW_FILE))
        {
            boolean newFile = (boolean) ctx.getPropertyValue(NEW_FILE);
            if (newFile)
            {
                if (ctx.hasProperty(FILE_ID))
                {
                    Long fileId = (Long) ctx.getPropertyValue(FILE_ID);
                    try
                    {
                        getEcmFileService().deleteFile(fileId);
                    }
                    catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
                    {
                        log.warn("Unable to delete ecm file with id [{}]", fileId);
                        throw new PipelineProcessException(e);
                    }
                }
            }
            else
            {
                Long fileId = (Long) ctx.getPropertyValue(FILE_ID);
                EcmFile ecmFile = getEcmFileService().findById(fileId);
                Long currentFileVersion = Long.parseLong(ecmFile.getActiveVersionTag());
                log.warn("Trying to delete [{}] version of EcmFile with id [{}].", currentFileVersion, fileId);
            }
        }
    }

    public DocumentGenerator getDocumentGenerator()
    {
        return documentGenerator;
    }

    public void setDocumentGenerator(DocumentGenerator documentGenerator)
    {
        this.documentGenerator = documentGenerator;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService fileService)
    {
        this.ecmFileService = fileService;
    }

    public SARDao getRequestDao()
    {
        return requestDao;
    }

    public void setRequestDao(SARDao requestDao)
    {
        this.requestDao = requestDao;
    }

    public SARDocumentGeneratorService getDocumentGeneratorService()
    {
        return documentGeneratorService;
    }

    public void setDocumentGeneratorService(SARDocumentGeneratorService documentGeneratorService)
    {
        this.documentGeneratorService = documentGeneratorService;
    }
}
