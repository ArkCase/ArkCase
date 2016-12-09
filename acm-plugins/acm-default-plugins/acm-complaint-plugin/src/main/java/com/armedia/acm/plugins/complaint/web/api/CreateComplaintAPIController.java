package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.frevvo.config.FrevvoFormService;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.complaint.ComplaintForm;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
import com.armedia.acm.plugins.complaint.service.ComplaintService;
import com.armedia.acm.plugins.complaint.service.SaveComplaintTransaction;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;


@Controller
@RequestMapping({"/api/v1/plugin/complaint", "/api/latest/plugin/complaint"})
public class CreateComplaintAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private SaveComplaintTransaction complaintTransaction;
    private ComplaintEventPublisher eventPublisher;
    private FrevvoFormService complaintService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Complaint createComplaint(
            @RequestBody Complaint in,
            Authentication auth
    ) throws AcmCreateObjectFailedException
    {
        log.trace("Got a complaint: {}; complaint ID: '{}'", in, in.getComplaintId());
        log.trace("complaint type: {}", in.getComplaintType());

        boolean isInsert = in.getComplaintId() == null;

        // explicitly set modifier and modified to trigger transformer to reindex data
        // fixes problem when some child objects are changed (e.g participants) and solr document is not updated
        in.setModifier(AuthenticationUtils.getUsername());
        in.setModified(new Date());

        try
        {
            Complaint saved = getComplaintTransaction().saveComplaint(in, auth);

            // Update Frevvo XML file
            getComplaintService().updateXML(saved, auth, ComplaintForm.class);

            getEventPublisher().publishComplaintEvent(saved, auth, isInsert, true);

            // since the approver list is not persisted to the database, we want to send them back to the caller...
            // the approver list is only here to send to the Activiti engine.  After the workflow is started the
            // approvers are stored in Activiti.
            saved.setApprovers(in.getApprovers());

            return saved;

        } catch (PipelineProcessException | TransactionException e)
        {
            log.error("Could not save complaint: {}", e.getMessage(), e);
            getEventPublisher().publishComplaintEvent(in, auth, isInsert, false);

            throw new AcmCreateObjectFailedException("complaint", e.getMessage(), e);
        }

    }

    public SaveComplaintTransaction getComplaintTransaction()
    {
        return complaintTransaction;
    }

    public void setComplaintTransaction(SaveComplaintTransaction complaintTransaction)
    {
        this.complaintTransaction = complaintTransaction;
    }

    public ComplaintEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(ComplaintEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public FrevvoFormService getComplaintService()
    {
        return complaintService;
    }

    public void setComplaintService(ComplaintService complaintService)
    {
        this.complaintService = complaintService;
    }
}
