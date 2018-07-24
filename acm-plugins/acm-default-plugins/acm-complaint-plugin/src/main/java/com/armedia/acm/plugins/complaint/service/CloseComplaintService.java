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

import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintUpdatedEvent;
import com.armedia.acm.plugins.complaint.model.closeModal.CloseComplaintEvent;
import com.armedia.acm.plugins.complaint.model.closeModal.CloseComplaintForm;
import com.armedia.acm.plugins.complaint.model.closeModal.Item;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.users.model.AcmUserActionName;
import com.armedia.acm.services.users.service.ldap.AcmUserActionExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class CloseComplaintService
{

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private ComplaintDao complaintDao;
    private CloseComplaintRequestDao closeComplaintRequestDao;
    private AcmUserActionExecutor userActionExecutor;
    private ApplicationEventPublisher applicationEventPublisher;
    // private CloseComplaintRequestFactory closeComplaintRequestFactory;

    public CloseComplaintRequest fromFormXml(
            CloseComplaintForm form,
            Authentication auth)
    {
        CloseComplaintRequest req = new CloseComplaintRequest();

        List<AcmParticipant> participants = convertItemsToParticipants(form.getApprovers());
        req.setParticipants(participants);
        req.setComplaintId(form.getInformation().getId());

        populateDisposition(form, auth, req);

        return req;
    }

    private void populateDisposition(CloseComplaintForm form, Authentication auth, CloseComplaintRequest req)
    {
        Disposition disposition = new Disposition();
        req.setDisposition(disposition);

        if (form.getInformation() != null)
        {
            // convert java.util.Date to LocalDate
            if (form.getInformation().getDate() != null && form.getInformation().getDate().toInstant() != null)
            {
                disposition.setCloseDate(form.getInformation().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            disposition.setDispositionType(form.getInformation().getOption());
        }

        if (form.getExistingCase() != null)
        {
            disposition.setExistingCaseNumber(form.getExistingCase().getCaseNumber());
        }

        if (form.getReferExternal() != null)
        {
            disposition.setReferExternalContactPersonName(form.getReferExternal().getPerson());
            disposition.setReferExternalOrganizationName(form.getReferExternal().getAgency());
            disposition.setReferExternalContactMethod(form.getReferExternal().getContact().returnBase());
        }
    }

    private List<AcmParticipant> convertItemsToParticipants(List<Item> items)
    {
        List<AcmParticipant> participants = new ArrayList<>();
        Logger log = LoggerFactory.getLogger(getClass());
        log.debug("# of incoming approvers: " + items.size());
        if (items != null)
        {
            for (Item item : items)
            {

                AcmParticipant participant = new AcmParticipant();
                participant.setParticipantLdapId(item.getValue());
                participant.setParticipantType("approver");
                participants.add(participant);
            }
        }

        return participants;
    }

    public boolean save(CloseComplaintForm form, Authentication auth, String mode) throws Exception
    {
        // @@@@@@ Mode e tipot so se praka "Create", "save"... 99% tuka ne treba
        // String mode = getRequest().getParameter("mode");

        // @@@@@@ NE treba!!!
        // Convert XML data to Object
        // CloseComplaintForm form = (CloseComplaintForm) convertFromXMLToObject(cleanXML(xml), getFormClass());

        if (form == null)
        {
            LOG.warn("Cannot unmarshall Close Complaint Form.");
            return false;
        }

        // Get Complaint depends on the complaint ID
        Complaint complaint = getComplaintDao().find(form.getInformation().getId());
        if (complaint == null)
        {
            LOG.warn("Cannot find complaint by given complaintId=" + form.getInformation().getId());
            return false;
        }

        if (("IN APPROVAL".equals(complaint.getStatus()) || "CLOSED".equals(complaint.getStatus())) && !"edit".equals(mode))
        {
            LOG.info("The complaint is already in '" + complaint.getStatus() +
                    "' mode. No further action will be taken.");
            return true;
        }
        CloseComplaintRequest closeComplaintRequest = fromFormXml(form, auth);

        /*
         * if ("edit".equals(mode))
         * {
         * String requestId = getRequest().getParameter("requestId");
         * CloseComplaintRequest closeComplaintRequestFromDatabase = null;
         * try
         * {
         * Long closeComplaintRequestId = Long.parseLong(requestId);
         * closeComplaintRequestFromDatabase = getCloseComplaintRequestDao().find(closeComplaintRequestId);
         * }
         * catch (Exception e)
         * {
         * LOG.warn("Close Complaint Request with id=" + requestId
         * + " is not found. The new request will be recorded in the database.");
         * }
         * if (null != closeComplaintRequestFromDatabase)
         * {
         * closeComplaintRequest.setId(closeComplaintRequestFromDatabase.getId());
         * getCloseComplaintRequestDao().delete(closeComplaintRequestFromDatabase.getParticipants());
         * }
         * }
         */

        CloseComplaintRequest savedRequest = getCloseComplaintRequestDao().save(closeComplaintRequest);

        if (!"edit".equals(mode))
        {
            // Record user action
            getUserActionExecutor().execute(savedRequest.getId(), AcmUserActionName.LAST_CLOSE_COMPLAINT_CREATED,
                    auth.getName());
        }
        else
        {
            // Record user action
            getUserActionExecutor().execute(savedRequest.getId(), AcmUserActionName.LAST_CLOSE_COMPLAINT_MODIFIED,
                    auth.getName());
        }

        // Update Status to "IN APPROVAL"

        if (!complaint.getStatus().equals("IN APPROVAL") && !"edit".equals(mode))
        {
            complaint.setStatus("IN APPROVAL");
            Complaint updatedComplaint = getComplaintDao().save(complaint);
            ComplaintUpdatedEvent complaintUpdatedEvent = new ComplaintUpdatedEvent(updatedComplaint);
            complaintUpdatedEvent.setSucceeded(true);
            getApplicationEventPublisher().publishEvent(complaintUpdatedEvent);
        }

        // Save attachments (or update XML form and PDF form if the mode is "edit")
        // String cmisFolderId = findFolderIdForAttachments(complaint.getContainer(), complaint.getObjectType(),
        // complaint.getId());
        // FrevvoUploadedFiles uploadedFiles = saveAttachments(attachments, cmisFolderId,
        // FrevvoFormName.COMPLAINT.toUpperCase(),
        // complaint.getComplaintId());

        // EVENTOT - IPaddress ????
        CloseComplaintEvent event = new CloseComplaintEvent(complaint.getComplaintNumber(),
                complaint.getComplaintId(),
                savedRequest, mode, auth.getName(), "192.168.56.1", true);
        getApplicationEventPublisher().publishEvent(event);

        return true;
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    public CloseComplaintRequestDao getCloseComplaintRequestDao()
    {
        return closeComplaintRequestDao;
    }

    public void setCloseComplaintRequestDao(CloseComplaintRequestDao closeComplaintRequestDao)
    {
        this.closeComplaintRequestDao = closeComplaintRequestDao;
    }

    public AcmUserActionExecutor getUserActionExecutor()
    {
        return userActionExecutor;
    }

    public void setUserActionExecutor(AcmUserActionExecutor userActionExecutor)
    {
        this.userActionExecutor = userActionExecutor;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}