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
			if ("init-roi-fields".equals(action)) {
				result = initRoiFields();
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
		
		ROIForm roiForm = (ROIForm) convertFromXMLToObject(xml, ROIForm.class);
		
		if (roiForm == null) {
			LOG.warn("Cannot umarshall ROI Form.");
			return false;
		}
		
		Complaint complaint = complaintDao.find(roiForm.getReportDetails().getComplaintId());
		
		if (complaint == null) {
			LOG.warn("Cannot find complaint by given complaintId=" + roiForm.getReportDetails().getComplaintId());
			return false;
		}
		
		saveAttachments(attachments, complaint.getEcmFolderId(), FrevvoFormName.COMPLAINT.toUpperCase(), complaint.getComplaintId(), complaint.getComplaintNumber());
		
		return true;
	}
	
	/**
	 * Initialization of ROI Form fields
	 * 
	 * @return
	 */
	private JSONObject initRoiFields() {
		
		ROIForm roiForm = new ROIForm();
		ReportInformation reportInformation = new ReportInformation();
		
		reportInformation.setDate(new Date());
		
		roiForm.setReportInformation(reportInformation);
		
		Gson gson = new GsonBuilder().setDateFormat("M/dd/yyyy").create();
		String jsonString = gson.toJson(roiForm);
		
		JSONObject json = new JSONObject(jsonString);
		
		return json;
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
