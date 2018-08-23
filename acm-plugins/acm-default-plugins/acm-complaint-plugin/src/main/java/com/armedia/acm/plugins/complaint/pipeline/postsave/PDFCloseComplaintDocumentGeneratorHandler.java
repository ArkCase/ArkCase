package com.armedia.acm.plugins.complaint.pipeline.postsave;

import com.armedia.acm.form.config.FormsTypeCheckService;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.plugins.complaint.service.PDFCloseComplaintDocumentGenerator;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PDFCloseComplaintDocumentGeneratorHandler extends PDFCloseComplaintDocumentGenerator<ComplaintDao, Complaint>
        implements PipelineHandler<CloseComplaintRequest, CloseComplaintPipelineContext>
{
    private FormsTypeCheckService formsTypeCheckService;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(CloseComplaintRequest form, CloseComplaintPipelineContext ctx) throws PipelineProcessException
    {
        if (!formsTypeCheckService.getTypeOfForm().equals("frevvo"))
        {
            log.debug("Entering pipeline handler forEntering pipeline handler for complaint with id [{}] and title [{}]",
                    ctx.getComplaint().getId(),
                    ctx.getComplaint().getTitle());

            // ensure the SQL of all prior handlers is visible to this handler
            getDao().getEm().flush();

            try
            {
                generatePdf("COMPLAINT", form.getComplaintId(), ctx);
            }
            catch (Exception e)
            {
                log.warn("Unable to generate pdf document for the complaint with id [{}] and title [{}]", ctx.getComplaint().getId(),
                        ctx.getComplaint().getTitle());
                throw new PipelineProcessException(e);
            }

            log.debug("Exiting pipeline handler for object: [{}]", ctx.getComplaint().getId());
        }
    }

    @Override
    public void rollback(CloseComplaintRequest form, CloseComplaintPipelineContext pipelineContext)
    {
        // nothing to do here, there is no rollback action to be executed
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
