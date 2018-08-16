package com.armedia.acm.plugins.complaint.pipeline.postsave;

import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.plugins.complaint.service.PDFCloseComplaintDocumentGenerator;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

public class PDFCloseComplaintDocumentGeneratorHandler implements PipelineHandler<CloseComplaintRequest, CloseComplaintPipelineContext>
{
    private PDFCloseComplaintDocumentGenerator pdfCloseComplaintDocumentGenerator;

    @Override
    public void execute(CloseComplaintRequest form, CloseComplaintPipelineContext ctx) throws PipelineProcessException
    {
        pdfCloseComplaintDocumentGenerator.generatePdf("COMPLAINT", form.getComplaintId(), ctx);
    }

    @Override
    public void rollback(CloseComplaintRequest form, CloseComplaintPipelineContext pipelineContext)
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public PDFCloseComplaintDocumentGenerator getPdfCloseComplaintDocumentGenerator()
    {
        return pdfCloseComplaintDocumentGenerator;
    }

    public void setPdfCloseComplaintDocumentGenerator(PDFCloseComplaintDocumentGenerator pdfCloseComplaintDocumentGenerator)
    {
        this.pdfCloseComplaintDocumentGenerator = pdfCloseComplaintDocumentGenerator;
    }
}
