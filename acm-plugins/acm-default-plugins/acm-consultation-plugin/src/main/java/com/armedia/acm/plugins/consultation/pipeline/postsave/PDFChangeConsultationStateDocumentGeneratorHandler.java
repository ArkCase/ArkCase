package com.armedia.acm.plugins.consultation.pipeline.postsave;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.form.config.FormsTypeCheckService;
import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStateContants;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatus;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationConstants;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.consultation.service.PDFChangeConsultationStateDocumentGenerator;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PDFChangeConsultationStateDocumentGeneratorHandler extends PDFChangeConsultationStateDocumentGenerator<ConsultationDao, Consultation>
        implements PipelineHandler<ChangeConsultationStatus, ConsultationPipelineContext>
{
    private FormsTypeCheckService formsTypeCheckService;
    private transient final Logger log = LogManager.getLogger(getClass());
    private PDFChangeConsultationStateDocumentGenerator pdfChangeConsultationStateDocumentGenerator;

    @Override
    public void execute(ChangeConsultationStatus form, ConsultationPipelineContext ctx) throws PipelineProcessException
    {
        if (!formsTypeCheckService.getTypeOfForm().equals("frevvo"))
        {
            log.debug("Entering pipeline handler forEntering pipeline handler for consultation with id [{}]",
                    form.getId());

            // ensure the SQL of all prior handlers is visible to this handler
            getDao().getEm().flush();

            try
            {
                generatePdf(ConsultationConstants.OBJECT_TYPE, form.getConsultationId(), ctx);
            }
            catch (Exception e)
            {
                log.warn("Unable to generate pdf document for the consultation with id [{}]", form.getId());
                throw new PipelineProcessException(e);
            }

            log.debug("Exiting pipeline handler for object: [{}]", form.getId());
        }

    }

    @Override
    public void rollback(ChangeConsultationStatus form, ConsultationPipelineContext ctx) throws PipelineProcessException
    {
        if (ctx.hasProperty(ChangeConsultationStateContants.NEW_FILE))
        {
            boolean newFile = (boolean) ctx.getPropertyValue(ChangeConsultationStateContants.NEW_FILE);
            if (newFile)
            {
                if (ctx.hasProperty(ChangeConsultationStateContants.FILE_ID))
                {
                    Long fileId = (Long) ctx.getPropertyValue(ChangeConsultationStateContants.FILE_ID);
                    try
                    {
                        getEcmFileService().deleteFile(fileId);
                    }
                    catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
                    {
                        log.warn("Unable to delete ecm file with id [{}] for the consultation with id [{}]", fileId,
                                form.getId());
                        throw new PipelineProcessException(e);
                    }
                }
            }
        }
    }

    public PDFChangeConsultationStateDocumentGenerator getPdfChangeConsultationStateDocumentGenerator() {
        return pdfChangeConsultationStateDocumentGenerator;
    }

    public void setPdfChangeConsultationStateDocumentGenerator(PDFChangeConsultationStateDocumentGenerator pdfChangeConsultationStateDocumentGenerator) {
        this.pdfChangeConsultationStateDocumentGenerator = pdfChangeConsultationStateDocumentGenerator;
    }

    public FormsTypeCheckService getFormsTypeCheckService()
    {
        return formsTypeCheckService;
    }

    public void setFormsTypeCheckService(FormsTypeCheckService formsTypeCheckService)
    {
        this.formsTypeCheckService = formsTypeCheckService;
    }
}
