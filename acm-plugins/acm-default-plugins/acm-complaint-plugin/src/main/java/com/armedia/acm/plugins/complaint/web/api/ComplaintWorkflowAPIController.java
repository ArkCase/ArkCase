package com.armedia.acm.plugins.complaint.web.api;

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

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping({ "/api/v1/plugin/complaint", "/api/latest/plugin/complaint" })
public class ComplaintWorkflowAPIController
{
    private Logger log = LogManager.getLogger(getClass());
    private ComplaintEventPublisher eventPublisher;

    @RequestMapping(method = RequestMethod.POST, value = "/workflow", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Complaint startApprovalWorkflow(
            @RequestBody Complaint in,
            Authentication auth,
            HttpServletResponse response,
            HttpSession session) throws AcmUserActionFailedException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Got a complaint: " + in + "; complaint ID: '" + in.getComplaintId() + "'");
            log.debug("complaint type: " + in.getComplaintType());
        }

        Long complaintId = in.getComplaintId();
        boolean isNew = (null == complaintId) || (0 == complaintId);

        String ipAddress = (String) session.getAttribute("acm_ip_address");

        if (isNew)
        {
            getEventPublisher().publishComplaintWorkflowEvent(in, auth, ipAddress, false);
            throw new AcmUserActionFailedException(
                    "start approval process",
                    "task",
                    complaintId,
                    "You must save the complaint first",
                    null);
        }

        getEventPublisher().publishComplaintWorkflowEvent(in, auth, ipAddress, true);

        return in;

    }

    public ComplaintEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(ComplaintEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

}
