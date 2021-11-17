package com.armedia.acm.plugins.complaint.pipeline.presave;

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

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CheckCloseComplaintRequest implements PipelineHandler<CloseComplaintRequest, CloseComplaintPipelineContext>
{
    private final Logger log = LogManager.getLogger(getClass());
    private ComplaintDao complaintDao;

    @Override
    public void execute(CloseComplaintRequest form, CloseComplaintPipelineContext ctx) throws PipelineProcessException
    {
        String mode = (String) ctx.getPropertyValue("mode");
        String message = "";

        if (form == null)
        {
            throw new PipelineProcessException("Cannot unmarshall Close Complaint Form.");
        }

        Complaint complaint = getComplaintDao().find(form.getComplaintId());
        if (complaint == null)
        {
            throw new PipelineProcessException("Cannot find complaint by given complaintId=" + form.getComplaintId());
        }

        if (("IN APPROVAL".equals(complaint.getStatus()) || "CLOSED".equals(complaint.getStatus())) && !"edit".equals(mode))
        {
            message = "The complaint is already in '" + complaint.getStatus() + "' mode. No further action will be taken.";
        }

        if(form.isCloseComplaintStatusFlow() && !"IN APPROVAL".equals(form.getStatus()) && !"edit".equals(mode))
        {
            complaint.setStatus("IN APPROVAL");
            log.debug("If closeComplaintStatusFlow is true, the status should be IN APPROVAL");
        }
        else if (!form.isCloseComplaintStatusFlow() && !"CLOSED".equals(form.getStatus()) && !"edit".equals(mode))
        {
            complaint.setStatus("CLOSED");
            log.debug("If closeComplaintStatusFlow is false, the status should be CLOSED");
        }

        if (!message.isEmpty())
        {
            throw new PipelineProcessException(message);
        }
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
}
