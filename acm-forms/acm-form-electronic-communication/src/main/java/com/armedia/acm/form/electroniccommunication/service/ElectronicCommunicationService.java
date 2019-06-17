/**
 * 
 */
package com.armedia.acm.form.electroniccommunication.service;

/*-
 * #%L
 * ACM Forms: Electronic Communication
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

import com.armedia.acm.form.electroniccommunication.model.ElectronicCommunicationForm;
import com.armedia.acm.form.electroniccommunication.model.ElectronicCommunicationInformation;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.users.model.AcmUserActionName;

import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class ElectronicCommunicationService extends FrevvoFormAbstractService
{

    private Logger LOG = LogManager.getLogger(getClass());
    private ComplaintDao complaintDao;
    private CaseFileDao caseFileDao;

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
        }

        return result;
    }

    @Override
    public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception
    {

        Long folderId = getFolderAndFilesUtils().convertToLong((String) getRequest().getParameter("folderId"));
        String cmisFolderId = null;
        String parentObjectType = null;
        Long parentObjectId = null;

        ElectronicCommunicationForm form = (ElectronicCommunicationForm) convertFromXMLToObject(cleanXML(xml),
                ElectronicCommunicationForm.class);

        if (form == null)
        {
            LOG.warn("Cannot umarshall Electronic Communication Form.");
            return false;
        }

        if (form.getDetails() == null || form.getDetails().getType() == null)
        {
            LOG.warn("Cannot read type of the Electronic Communictation form. Should be 'complaint' or 'case'.");
            return false;
        }

        String type = form.getDetails().getType();

        if ("complaint".equals(type))
        {
            Complaint complaint = getComplaintDao().find(form.getDetails().getComplaintId());

            if (complaint == null)
            {
                LOG.warn("Cannot find complaint by given complaintId=" + form.getDetails().getComplaintId());
                return false;
            }

            cmisFolderId = findCmisFolderId(folderId, complaint.getContainer(), complaint.getObjectType(), complaint.getId());
            parentObjectType = FrevvoFormName.COMPLAINT.toUpperCase();
            parentObjectId = complaint.getComplaintId();

            // Record user action
            getUserActionExecutor().execute(complaint.getComplaintId(), AcmUserActionName.LAST_COMPLAINT_MODIFIED,
                    getAuthentication().getName());

        }
        else if ("case".equals(type))
        {
            CaseFile caseFile = getCaseFileDao().find(form.getDetails().getCaseId());

            if (caseFile == null)
            {
                LOG.warn("Cannot find case by given caseId=" + form.getDetails().getCaseId());
                return false;
            }
            cmisFolderId = findCmisFolderId(folderId, caseFile.getContainer(), caseFile.getObjectType(), caseFile.getId());
            parentObjectType = FrevvoFormName.CASE_FILE.toUpperCase();
            parentObjectId = caseFile.getId();

            // Record user action
            getUserActionExecutor().execute(caseFile.getId(), AcmUserActionName.LAST_CASE_MODIFIED, getAuthentication().getName());
        }

        saveAttachments(attachments, cmisFolderId, parentObjectType, parentObjectId);

        return true;
    }

    private JSONObject initFormData()
    {
        ElectronicCommunicationForm form = new ElectronicCommunicationForm();
        ElectronicCommunicationInformation information = new ElectronicCommunicationInformation();

        information.setDate(new Date());

        form.setInformation(information);

        JSONObject json = createResponse(form);

        return json;
    }

    @Override
    public String getFormName()
    {
        return FrevvoFormName.ELECTRONIC_COMMUNICATION;
    }

    @Override
    public Class<?> getFormClass()
    {
        return ElectronicCommunicationForm.class;
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    @Override
    public Object convertToFrevvoForm(Object obj, Object form)
    {
        // Implementation no needed so far
        return null;
    }

}
