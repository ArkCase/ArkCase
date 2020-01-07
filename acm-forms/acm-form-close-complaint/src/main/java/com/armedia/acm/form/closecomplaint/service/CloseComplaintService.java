/**
 *
 */
package com.armedia.acm.form.closecomplaint.service;

/*-
 * #%L
 * ACM Forms: Close Complaint
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
import com.armedia.acm.form.closecomplaint.model.CloseComplaintForm;
import com.armedia.acm.form.closecomplaint.model.CloseComplaintFormEvent;
import com.armedia.acm.form.closecomplaint.model.ExistingCase;
import com.armedia.acm.form.closecomplaint.model.ReferExternal;
import com.armedia.acm.form.config.ResolveInformation;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.model.UploadedFiles;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintUpdatedEvent;
import com.armedia.acm.services.users.model.AcmUserActionName;

import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * @author riste.tutureski
 */
public class CloseComplaintService extends FrevvoFormAbstractService implements ApplicationEventPublisherAware
{

    private Logger LOG = LogManager.getLogger(CloseComplaintService.class);
    private ComplaintDao complaintDao;
    private CaseFileDao caseFileDao;
    private CloseComplaintRequestDao closeComplaintRequestDao;
    private ApplicationEventPublisher applicationEventPublisher;
    private CloseComplaintRequestFactory closeComplaintRequestFactory;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.frevvo.config.FrevvoFormService#get(java.lang.String)
     */
    @Override
    public Object get(String action)
    {
        Object result = null;

        if (action != null)
        {
            if ("init-form-data".equals(action))
            {
                result = initFormData();
            }

            if ("case".equals(action))
            {
                String caseNumber = getRequest().getParameter("caseNumber");
                result = getCase(caseNumber);

            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.frevvo.config.FrevvoFormService#save(java.lang.String,
     * org.springframework.util.MultiValueMap)
     */
    @Override
    public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception
    {

        String mode = getRequest().getParameter("mode");

        // Convert XML data to Object
        CloseComplaintForm form = (CloseComplaintForm) convertFromXMLToObject(cleanXML(xml), getFormClass());

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
            LOG.info("The complaint is already in '" + complaint.getStatus() + "' mode. No further action will be taken.");
            return true;
        }

        CloseComplaintRequest closeComplaintRequest = getCloseComplaintRequestFactory().fromFormXml(form, getAuthentication());

        if ("edit".equals(mode))
        {
            String requestId = getRequest().getParameter("requestId");
            CloseComplaintRequest closeComplaintRequestFromDatabase = null;
            try
            {
                Long closeComplaintRequestId = Long.parseLong(requestId);
                closeComplaintRequestFromDatabase = getCloseComplaintRequestDao().find(closeComplaintRequestId);
            }
            catch (Exception e)
            {
                LOG.warn("Close Complaint Request with id=" + requestId
                        + " is not found. The new request will be recorded in the database.");
            }

            if (null != closeComplaintRequestFromDatabase)
            {
                closeComplaintRequest.setId(closeComplaintRequestFromDatabase.getId());
                getCloseComplaintRequestDao().delete(closeComplaintRequestFromDatabase.getParticipants());
            }
        }

        CloseComplaintRequest savedRequest = getCloseComplaintRequestDao().save(closeComplaintRequest);

        if (!"edit".equals(mode))
        {
            // Record user action
            getUserActionExecutor().execute(savedRequest.getId(), AcmUserActionName.LAST_CLOSE_COMPLAINT_CREATED,
                    getAuthentication().getName());
        }
        else
        {
            // Record user action
            getUserActionExecutor().execute(savedRequest.getId(), AcmUserActionName.LAST_CLOSE_COMPLAINT_MODIFIED,
                    getAuthentication().getName());
        }

        // Update Status to "IN APPROVAL"
        if (!complaint.getStatus().equals("IN APPROVAL") && !"edit".equals(mode))
        {
            complaint.setStatus("IN APPROVAL");
            Complaint updatedComplaint = getComplaintDao().save(complaint);

            ComplaintUpdatedEvent complaintUpdatedEvent = new ComplaintUpdatedEvent(updatedComplaint,
                    AuthenticationUtils.getUserIpAddress());
            complaintUpdatedEvent.setSucceeded(true);
            getApplicationEventPublisher().publishEvent(complaintUpdatedEvent);
        }

        // Save attachments (or update XML form and PDF form if the mode is "edit")
        String cmisFolderId = findFolderIdForAttachments(complaint.getContainer(), complaint.getObjectType(),
                complaint.getId());
        UploadedFiles uploadedFiles = saveAttachments(attachments, cmisFolderId,
                FrevvoFormName.COMPLAINT.toUpperCase(),
                complaint.getComplaintId());

        CloseComplaintFormEvent event = new CloseComplaintFormEvent(complaint.getComplaintNumber(),
                complaint.getComplaintId(),
                savedRequest, uploadedFiles, mode, getAuthentication().getName(), getUserIpAddress(), true);
        getApplicationEventPublisher().publishEvent(event);

        return true;
    }

    private Object initFormData()
    {

        String mode = getRequest().getParameter("mode");
        CloseComplaintForm closeComplaint = new CloseComplaintForm();

        ResolveInformation information = new ResolveInformation();
        if (!"edit".equals(mode))
        {
            information.setDate(new Date());
        }
        information.setResolveOptions(getStandardLookupEntries("dispositions"));

        ReferExternal referExternal = new ReferExternal();
        if (!"edit".equals(mode))
        {
            referExternal.setDate(new Date());
        }
        ContactMethod contact = new ContactMethod();
        contact.setTypes(getStandardLookupEntries("deviceTypes"));
        referExternal.setContact(contact);

        closeComplaint.setInformation(information);
        closeComplaint.setReferExternal(referExternal);

        JSONObject json = createResponse(closeComplaint);

        return json;
    }

    private Object getCase(String caseNumber)
    {
        CloseComplaintForm closeComplaint = new CloseComplaintForm();
        ExistingCase existingCase = new ExistingCase();

        CaseFile caseFile = null;

        try
        {
            caseFile = getCaseFileDao().findByCaseNumber(caseNumber);
        }
        catch (Exception e)
        {
            LOG.warn("The case with number '" + caseNumber + "' doesn't exist.");
        }

        if (caseFile != null)
        {
            existingCase.setCaseNumber(caseNumber);
            existingCase.setCaseTitle(caseFile.getTitle());
            existingCase.setCaseCreationDate(caseFile.getCreated());
            existingCase.setCasePriority(caseFile.getPriority());
        }

        closeComplaint.setExistingCase(existingCase);

        JSONObject json = createResponse(closeComplaint);

        return json;
    }

    @Override
    public String getFormName()
    {
        return FrevvoFormName.CLOSE_COMPLAINT;
    }

    @Override
    public Class<?> getFormClass()
    {
        return CloseComplaintForm.class;
    }

    /**
     * @return the complaintDao
     */
    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    /**
     * @param complaintDao
     *            the complaintDao to set
     */
    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    /**
     * @return the caseFileDao
     */
    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    /**
     * @param caseFileDao
     *            the caseFileDao to set
     */
    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public CloseComplaintRequestDao getCloseComplaintRequestDao()
    {
        return closeComplaintRequestDao;
    }

    public void setCloseComplaintRequestDao(CloseComplaintRequestDao closeComplaintRequestDao)
    {
        this.closeComplaintRequestDao = closeComplaintRequestDao;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Object convertToFrevvoForm(Object obj, Object form)
    {
        // Implementation no needed so far
        return null;
    }

    public CloseComplaintRequestFactory getCloseComplaintRequestFactory()
    {
        return closeComplaintRequestFactory;
    }

    public void setCloseComplaintRequestFactory(CloseComplaintRequestFactory closeComplaintRequestFactory)
    {
        this.closeComplaintRequestFactory = closeComplaintRequestFactory;
    }
}
