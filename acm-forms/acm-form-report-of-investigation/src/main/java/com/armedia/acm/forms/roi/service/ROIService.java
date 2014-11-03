/**
 * 
 */
package com.armedia.acm.forms.roi.service;

import java.util.Date;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.forms.roi.model.ROIForm;
import com.armedia.acm.forms.roi.model.ReportInformation;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author riste.tutureski
 *
 */
public class ROIService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(ROIService.class);
	private ComplaintDao complaintDao;
	
	/* (non-Javadoc)
	 * @see com.armedia.acm.frevvo.config.FrevvoFormService#init()
	 */
	@Override
	public Object init() {
		// TODO Auto-generated method stub
		return null;
	}

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
		
		String ecmFolderId = null;
		String parentObjectType = null;
		Long parentObjectId = null;
		String parentObjectName = null;
		
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
			
			ecmFolderId = complaint.getEcmFolderId();
			parentObjectType = FrevvoFormName.COMPLAINT.toUpperCase();
			parentObjectId = complaint.getComplaintId();
			parentObjectName = complaint.getComplaintNumber();			
		}else if ("case".equals(type)){	
			ecmFolderId = roiForm.getReportDetails().getCaseFolderId();
			parentObjectType = "CASE";
			parentObjectId = roiForm.getReportDetails().getCaseId();
			parentObjectName = roiForm.getReportDetails().getCaseNumber();
		}
			
		saveAttachments(attachments, ecmFolderId, parentObjectType, parentObjectId, parentObjectName);
		
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
		
		Gson gson = new GsonBuilder().setDateFormat("M/dd/yyyy").create();
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

}
