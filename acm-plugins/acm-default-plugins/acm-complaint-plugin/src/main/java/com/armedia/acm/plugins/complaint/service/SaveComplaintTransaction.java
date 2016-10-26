package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.ComplaintPipelineContext;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implement transactional responsibilities for the SaveComplaintController.
 * <p>
 * JPA does all database writes at commit time. Therefore, if the transaction demarcation was in the controller,
 * exceptions would not be raised until after the controller method returns; i.e. the exception message goes write to
 * the browser. Also, separating transaction management (in this class) and exception handling (in the controller) is a
 * good idea in general.
 */
public class SaveComplaintTransaction
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private ComplaintDao complaintDao;
    private PipelineManager<Complaint, ComplaintPipelineContext> pipelineManager;

    @Transactional
    public Complaint saveComplaint(Complaint complaint, Authentication authentication) throws PipelineProcessException
    {
        ComplaintPipelineContext pipelineContext = new ComplaintPipelineContext();
        // populate the context
        pipelineContext.setAuthentication(authentication);
        pipelineContext.setNewComplaint(complaint.getId() == null);
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        pipelineContext.setIpAddress(ipAddress);

        return pipelineManager.executeOperation(complaint, pipelineContext, () ->
        {
            Complaint saved = complaintDao.save(complaint);
            log.info("Complaint saved '{}'", saved);
            return saved;

        });
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    public PipelineManager getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager pipelineManager)
    {
        this.pipelineManager = pipelineManager;
    }
}
