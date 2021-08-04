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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.form.config.FormsTypeCheckService;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.complaint.ComplaintForm;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
import com.armedia.acm.plugins.complaint.service.ComplaintService;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
@RequestMapping({ "/api/v1/plugin/complaint", "/api/latest/plugin/complaint" })

public class CreateComplaintAPIController
{
    private Logger log = LogManager.getLogger(getClass());

    private ComplaintEventPublisher eventPublisher;

    private ComplaintService complaintService;

    private ObjectConverter objectConverter;
    private FormsTypeCheckService formsTypeCheckService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @DecoratedAssignedObjectParticipants
    @ResponseBody
    public Complaint createComplaint(@RequestBody Complaint in, Authentication auth) throws AcmCreateObjectFailedException
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
            Complaint oldComplaint = null;
            if (!isInsert)
            {
                String old = getObjectConverter().getJsonMarshaller()
                        .marshal(getComplaintService().getSaveComplaintTransaction().getComplaint(in.getComplaintId()));
                oldComplaint = getObjectConverter().getJsonUnmarshaller().unmarshall(old, Complaint.class);
            }
            Complaint saved = getComplaintService().saveComplaint(in, auth);

            if (formsTypeCheckService.getTypeOfForm().equals("frevvo"))
            {
                // Update Frevvo XML file
                getComplaintService().updateXML(saved, auth, ComplaintForm.class);
            }

            getEventPublisher().publishComplaintEvent(saved, oldComplaint, auth, isInsert, true);

            // since the approver list is not persisted to the database, we want to send them back to the caller...
            // the approver list is only here to send to the Activiti engine. After the workflow is started the
            // approvers are stored in Activiti.
            saved.setApprovers(in.getApprovers());

            return saved;

        }
        catch (PipelineProcessException | TransactionException e)
        {
            log.error("Could not save complaint: {}", e.getMessage(), e);
            getEventPublisher().publishComplaintEvent(in, null, auth, isInsert, false);

            throw new AcmCreateObjectFailedException("complaint", e.getMessage(), e);
        }

    }

    public ComplaintEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(ComplaintEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public ComplaintService getComplaintService()
    {
        return complaintService;
    }

    public void setComplaintService(ComplaintService complaintService)
    {
        this.complaintService = complaintService;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public FormsTypeCheckService getFormsTypeCheckService()
    {
        return formsTypeCheckService;
    }

    public void setFormsTypeCheckService(FormsTypeCheckService formsTypeCheckService)
    {
        this.formsTypeCheckService = formsTypeCheckService;
    }
}
