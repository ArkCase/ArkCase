package com.armedia.acm.plugins.complaint.pipeline.postsave;

import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.plugins.complaint.service.PDFCloseComplaintDocumentGenerator;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import javax.xml.parsers.ParserConfigurationException;

public class PDFCloseComplaintDocumentGeneratorHandler implements PipelineHandler<CloseComplaintRequest, CloseComplaintPipelineContext>
{
    private CloseComplaintRequestDao closeComplaintRequestDao;
    private ComplaintDao complaintDao;

    private PDFCloseComplaintDocumentGenerator pdfCloseComplaintDocumentGenerator;

    @Override
    public void execute(CloseComplaintRequest form, CloseComplaintPipelineContext ctx) throws ParserConfigurationException
    {
        pdfCloseComplaintDocumentGenerator.generatePdf("COMPLAINT", form.getComplaintId(), ctx);
    }

    @Override
    public void rollback(CloseComplaintRequest form, CloseComplaintPipelineContext pipelineContext)
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public CloseComplaintRequestDao getCloseComplaintRequestDao()
    {
        return closeComplaintRequestDao;
    }

    public void setCloseComplaintRequestDao(CloseComplaintRequestDao closeComplaintRequestDao)
    {
        this.closeComplaintRequestDao = closeComplaintRequestDao;
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
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
