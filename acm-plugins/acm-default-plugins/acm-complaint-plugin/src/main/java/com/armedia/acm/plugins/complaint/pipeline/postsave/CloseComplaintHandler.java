package com.armedia.acm.plugins.complaint.pipeline.postsave;

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
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintUpdatedEvent;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class CloseComplaintHandler
        implements ApplicationEventPublisherAware, PipelineHandler<CloseComplaintRequest, CloseComplaintPipelineContext>
{
    private ComplaintDao complaintDao;
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void execute(CloseComplaintRequest entity, CloseComplaintPipelineContext ctx)
    {
        Complaint complaint = complaintDao.find(entity.getComplaintId());

        complaint.setStatus(entity.getStatus());

        Complaint updatedComplaint = getComplaintDao().save(complaint);

        ctx.setComplaint(updatedComplaint);

        ComplaintUpdatedEvent complaintUpdatedEvent = new ComplaintUpdatedEvent(updatedComplaint,
               AuthenticationUtils.getUserIpAddress());
        complaintUpdatedEvent.setSucceeded(true);
        ctx.addProperty("complaintUpdated", complaintUpdatedEvent);
        getApplicationEventPublisher().publishEvent(complaintUpdatedEvent);

    }

    @Override
    public void rollback(CloseComplaintRequest entity, CloseComplaintPipelineContext ctx)
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }
}
