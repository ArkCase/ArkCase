/**
 * 
 */
package com.armedia.acm.forms.roi.service;

import java.util.Date;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.forms.roi.model.ROIForm;
import com.armedia.acm.forms.roi.model.ReportInformation;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.users.dao.ldap.UserActionDao;
import com.armedia.acm.services.users.model.AcmUserActionName;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author riste.tutureski
 *
 */
public class ROIService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(ROIService.class);
	private ComplaintDao complaintDao;
	private CaseFileDao caseFileDao;
	private UserActionDao userActionDao;

	/* (non-Javadoc)
	 * @see com.armedia.acm.frevvo.config.FrevvoFormService#get(java.lang.String)
	 */
	@Override
	public Object get(String action) {
		Object result = null;
		
		if (action != null) {
			if ("init-form-data".equals(action)) {
				result = initFormData();
			}
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.armedia.acm.frevvo.config.FrevvoFormService#save(java.lang.String, org.springframework.util.MultiValueMap)
	 */
	@Override
	public boolean save(String xml,
			MultiValueMap<String, MultipartFile> attachments) throws Exception {
		
		String cmisFolderId = null;
		String parentObjectType = null;
		Long parentObjectId = null;
		
		ROIForm roiForm = (ROIForm) convertFromXMLToObject(cleanXML(xml), ROIForm.class);
		
		if (roiForm == null) {
			LOG.warn("Cannot umarshall ROI Form.");
			return false;
		}
		
		if (roiForm.getReportDetails() == null || roiForm.getReportDetails().getType() == null) {
			LOG.warn("Cannot read type of the ROI form. Should be 'complaint' or 'case'.");
			return false;
		}
		
		String type = roiForm.getReportDetails().getType();
		
		if ("complaint".equals(type)){
			Complaint complaint = complaintDao.find(roiForm.getReportDetails().getComplaintId());
			
			if (complaint == null) {
				LOG.warn("Cannot find complaint by given complaintId=" + roiForm.getReportDetails().getComplaintId());
				return false;
			}
			
			cmisFolderId = findFolderId(complaint.getContainer(), complaint.getObjectType(), complaint.getId());
			parentObjectType = FrevvoFormName.COMPLAINT.toUpperCase();
			parentObjectId = complaint.getComplaintId();

			// Record user action
			getUserActionExecutor().execute(complaint.getComplaintId(), AcmUserActionName.LAST_COMPLAINT_MODIFIED, getAuthentication().getName());

		}else if ("case".equals(type)){	
			CaseFile caseFile = caseFileDao.find(roiForm.getReportDetails().getCaseId());
			
			if (caseFile == null) {
				LOG.warn("Cannot find case by given caseId=" + roiForm.getReportDetails().getCaseId());
				return false;
			}
            cmisFolderId = findFolderId(caseFile.getContainer(), caseFile.getObjectType(), caseFile.getId());
			parentObjectType = FrevvoFormName.CASE_FILE.toUpperCase();
			parentObjectId = caseFile.getId();
			
			// Record user action
			getUserActionExecutor().execute(caseFile.getId(), AcmUserActionName.LAST_CASE_MODIFIED, getAuthentication().getName());
		}
			
		saveAttachments(attachments, cmisFolderId, parentObjectType, parentObjectId);
		
		return true;
	}

    /**
	 * Initialization of ROI Form fields
	 * 
	 * @return
	 */
	private JSONObject initFormData() {
		
		ROIForm roiForm = new ROIForm();
		ReportInformation reportInformation = new ReportInformation();
		
		reportInformation.setDate(new Date());
		
		roiForm.setReportInformation(reportInformation);
		
		Gson gson = new GsonBuilder().setDateFormat(DateFormats.FREVVO_DATE_FORMAT).create();
		String jsonString = gson.toJson(roiForm);
		
		JSONObject json = new JSONObject(jsonString);
		
		return json;
	}

    @Override
    public String getFormName()
    {
        return FrevvoFormName.ROI;
    }

	/**
	 * @return the complaintDao
	 */
	public ComplaintDao getComplaintDao() {
		return complaintDao;
	}

	/**
	 * @param complaintDao the complaintDao to set
	 */
	public void setComplaintDao(ComplaintDao complaintDao) {
		this.complaintDao = complaintDao;
	}

	/**
	 * @return the caseFileDao
	 */
	public CaseFileDao getCaseFileDao() {
		return caseFileDao;
	}

	/**
	 * @param caseFileDao the caseFileDao to set
	 */
	public void setCaseFileDao(CaseFileDao caseFileDao) {
		this.caseFileDao = caseFileDao;
	}

	/**
	 * @return the userActionDao
	 */
	public UserActionDao getUserActionDao() {
		return userActionDao;
	}

	/**
	 * @param userActionDao the userActionDao to set
	 */
	public void setUserActionDao(UserActionDao userActionDao) {
		this.userActionDao = userActionDao;
	}
}
