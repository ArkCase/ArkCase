package com.armedia.acm.plugins.complaint.pipeline.postsave;

import com.armedia.acm.plugins.admin.service.JsonPropertiesManagementService;
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
    private JsonPropertiesManagementService jsonPropertiesManagementService;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(CloseComplaintRequest form, CloseComplaintPipelineContext ctx) throws PipelineProcessException
    {
        String formsType = "";
        try
        {
            formsType = jsonPropertiesManagementService.getProperty("formsType").get("formsType").toString();
        }
        catch (Exception e)
        {
            log.error("Can't retrieve application property", e);
        }

        if (!formsType.equals("frevvo"))
        {
            generatePdf("COMPLAINT", form.getComplaintId(), ctx);
        }
    }

    @Override
    public void rollback(CloseComplaintRequest form, CloseComplaintPipelineContext pipelineContext)
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public JsonPropertiesManagementService getJsonPropertiesManagementService()
    {
        return jsonPropertiesManagementService;
    }

    public void setJsonPropertiesManagementService(JsonPropertiesManagementService jsonPropertiesManagementService)
    {
        this.jsonPropertiesManagementService = jsonPropertiesManagementService;
    }
}
