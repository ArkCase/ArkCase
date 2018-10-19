/**
 *
 */
package gov.foia.pipeline.postsave;

import static gov.foia.model.FOIAConstants.FILE_ID;
import static gov.foia.model.FOIAConstants.NEW_FILE;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIADocumentDescriptor;
import gov.foia.model.FOIARequest;
import gov.foia.service.DocumentGenerator;
import gov.foia.service.DocumentGeneratorException;
import gov.foia.service.FOIADocumentGeneratorService;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 18, 2016
 */
public class RequestDocumentHandler implements PipelineHandler<FOIARequest, CaseFilePipelineContext>
{
    private EcmFileService ecmFileService;
    private DocumentGenerator documentGenerator;
    private FOIADocumentGeneratorService documentGeneratorService;
    private FOIARequestDao requestDao;

    /**
     * Logger instance.
     */
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(FOIARequest request, CaseFilePipelineContext ctx) throws PipelineProcessException
    {
        log.debug("Entering pipeline handler for object: [{}]", request);

        if (ctx.isNewCase())
        {
            // ensure the data changes of all prior handlers is visible to this handler
            requestDao.getEm().flush();

            FOIARequest businessObject = requestDao.find(request.getId());
            FOIADocumentDescriptor documentDescriptor = documentGeneratorService.getDocumentDescriptor(businessObject, FOIAConstants.REQ);
            String arkcaseFilename = String.format(documentDescriptor.getFilenameFormat(), businessObject.getId());
            String targetFolderId = businessObject.getContainer().getAttachmentFolder() == null
                    ? businessObject.getContainer().getFolder().getCmisFolderId()
                    : businessObject.getContainer().getAttachmentFolder().getCmisFolderId();
            try
            {
                EcmFile ecmFile = documentGenerator.generateAndUpload(documentDescriptor, businessObject, targetFolderId, arkcaseFilename,
                        documentGeneratorService.getReportSubstitutions(businessObject));

                if (ctx != null)
                {
                    ctx.addProperty(NEW_FILE, true);
                    ctx.addProperty(FILE_ID, ecmFile.getId());
                }
            }
            catch (DocumentGeneratorException e)
            {
                throw new PipelineProcessException(e);
            }
        }

        log.debug("Exiting pipeline handler for object: [{}]", request);
    }

    @Override
    public void rollback(FOIARequest request, CaseFilePipelineContext ctx) throws PipelineProcessException
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

    public FOIARequestDao getRequestDao()
    {
        return requestDao;
    }

    public void setRequestDao(FOIARequestDao requestDao)
    {
        this.requestDao = requestDao;
    }

    public FOIADocumentGeneratorService getDocumentGeneratorService()
    {
        return documentGeneratorService;
    }

    public void setDocumentGeneratorService(FOIADocumentGeneratorService documentGeneratorService)
    {
        this.documentGeneratorService = documentGeneratorService;
    }
}
