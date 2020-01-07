/**
 *
 */
package com.armedia.acm.forms.roi.service;

/*-
 * #%L
 * ACM Forms: Report of Investigation
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

import com.armedia.acm.forms.roi.model.ROIForm;
import com.armedia.acm.forms.roi.model.ReportInformation;
import com.armedia.acm.forms.roi.model.ReportOfInvestigationFormEvent;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.model.UploadedFiles;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.users.model.AcmUserActionName;

import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * @author riste.tutureski
 */
public class ROIService extends FrevvoFormAbstractService
{

    private Logger LOG = LogManager.getLogger(ROIService.class);
    private ComplaintDao complaintDao;
    private CaseFileDao caseFileDao;
    private ApplicationEventPublisher applicationEventPublisher;

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

        Long folderId = getFolderAndFilesUtils().convertToLong(getRequest().getParameter("folderId"));
        String cmisFolderId = null;
        String parentObjectType = null;
        Long parentObjectId = null;

        ROIForm roiForm = (ROIForm) convertFromXMLToObject(cleanXML(xml), ROIForm.class);

        if (roiForm == null)
        {
            LOG.warn("Cannot umarshall ROI Form.");
            return false;
        }

        if (roiForm.getReportDetails() == null || roiForm.getReportDetails().getType() == null)
        {
            LOG.warn("Cannot read type of the ROI form. Should be 'complaint' or 'case'.");
            return false;
        }

        String type = roiForm.getReportDetails().getType();
        String forObjectType = null;
        String forObjectNumber = null;

        if ("complaint".equals(type))
        {
            Complaint complaint = complaintDao.find(roiForm.getReportDetails().getComplaintId());

            if (complaint == null)
            {
                LOG.warn("Cannot find complaint by given complaintId=" + roiForm.getReportDetails().getComplaintId());
                return false;
            }

            forObjectType = "Complaint";
            forObjectNumber = complaint.getComplaintNumber();

            cmisFolderId = findCmisFolderId(folderId, complaint.getContainer(), complaint.getObjectType(), complaint.getId());
            parentObjectType = FrevvoFormName.COMPLAINT.toUpperCase();
            parentObjectId = complaint.getComplaintId();

            // Record user action
            getUserActionExecutor().execute(complaint.getComplaintId(), AcmUserActionName.LAST_COMPLAINT_MODIFIED,
                    getAuthentication().getName());

        }
        else if ("case".equals(type))
        {
            CaseFile caseFile = caseFileDao.find(roiForm.getReportDetails().getCaseId());

            if (caseFile == null)
            {
                LOG.warn("Cannot find case by given caseId=" + roiForm.getReportDetails().getCaseId());
                return false;
            }
            forObjectType = "Case File";
            forObjectNumber = caseFile.getCaseNumber();

            cmisFolderId = findCmisFolderId(folderId, caseFile.getContainer(), caseFile.getObjectType(), caseFile.getId());
            parentObjectType = FrevvoFormName.CASE_FILE.toUpperCase();
            parentObjectId = caseFile.getId();

            // Record user action
            getUserActionExecutor().execute(caseFile.getId(), AcmUserActionName.LAST_CASE_MODIFIED, getAuthentication().getName());
        }

        UploadedFiles uploadedFiles = saveAttachments(attachments, cmisFolderId, parentObjectType, parentObjectId);

        ReportOfInvestigationFormEvent event = new ReportOfInvestigationFormEvent(forObjectType, forObjectNumber, parentObjectType,
                parentObjectId, roiForm, uploadedFiles, getAuthentication().getName(), getUserIpAddress(), true);

        getApplicationEventPublisher().publishEvent(event);

        return true;
    }

    /**
     * Initialization of ROI Form fields
     *
     * @return
     */
    private JSONObject initFormData()
    {

        ROIForm roiForm = new ROIForm();
        ReportInformation reportInformation = new ReportInformation();

        reportInformation.setDate(new Date());

        roiForm.setReportInformation(reportInformation);

        JSONObject json = createResponse(roiForm);

        return json;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public String getFormName()
    {
        return FrevvoFormName.ROI;
    }

    @Override
    public Class<?> getFormClass()
    {
        return ROIForm.class;
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

    @Override
    public Object convertToFrevvoForm(Object obj, Object form)
    {
        // Implementation no needed so far
        return null;
    }
}
