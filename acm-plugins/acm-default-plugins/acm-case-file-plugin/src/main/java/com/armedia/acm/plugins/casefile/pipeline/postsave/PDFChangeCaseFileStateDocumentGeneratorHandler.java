package com.armedia.acm.plugins.casefile.pipeline.postsave;

import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.service.PDFChangeCaseFileStateDocumentGenerator;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

public class PDFChangeCaseFileStateDocumentGeneratorHandler implements PipelineHandler<ChangeCaseStatus, CaseFilePipelineContext>
{
    private PDFChangeCaseFileStateDocumentGenerator pdfChangeCaseFileStateDocumentGenerator;

    @Override
    public void execute(ChangeCaseStatus form, CaseFilePipelineContext ctx) throws PipelineProcessException
    {
        pdfChangeCaseFileStateDocumentGenerator.generatePdf("CASE_FILE", form.getCaseId(), ctx);
    }

    @Override
    public void rollback(ChangeCaseStatus entity, CaseFilePipelineContext pipelineContext)
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public PDFChangeCaseFileStateDocumentGenerator getPdfChangeCaseFileStateDocumentGenerator()
    {
        return pdfChangeCaseFileStateDocumentGenerator;
    }

    public void setPdfChangeCaseFileStateDocumentGenerator(PDFChangeCaseFileStateDocumentGenerator pdfChangeCaseFileStateDocumentGenerator)
    {
        this.pdfChangeCaseFileStateDocumentGenerator = pdfChangeCaseFileStateDocumentGenerator;
    }
}
