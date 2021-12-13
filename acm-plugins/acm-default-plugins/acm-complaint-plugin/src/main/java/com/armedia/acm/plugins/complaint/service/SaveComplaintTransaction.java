package com.armedia.acm.plugins.complaint.service;

/*-
 * #%L
 * ACM Default Plugin: Complaints
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.complaint.pipeline.ComplaintPipelineContext;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private final Logger log = LogManager.getLogger(getClass());
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

        if(complaint.getComplaintTitle() == null || complaint.getComplaintTitle().length() == 0) {
            complaint.setComplaintTitle(ComplaintConstants.OBJECT_TYPE);
        }

        return pipelineManager.executeOperation(complaint, pipelineContext, () -> {

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

    public Complaint getComplaint(Long complaintId)
    {
        return complaintDao.find(complaintId);
    }
}
