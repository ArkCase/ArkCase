/**
 * 
 */
package com.armedia.acm.form.electroniccommunication.service;

import java.util.Date;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.form.electroniccommunication.model.ElectronicCommunicationForm;
import com.armedia.acm.form.electroniccommunication.model.ElectronicCommunicationInformation;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.users.model.AcmUserActionName;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author riste.tutureski
 *
 */
public class ElectronicCommunicationService extends FrevvoFormAbstractService{

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private ComplaintDao complaintDao;
	private CaseFileDao caseFileDao;
	
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

	@Override
	public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception {

		String cmisFolderId = null;
		String parentObjectType = null;
		Long parentObjectId = null;
		String parentObjectName = null;
		
		ElectronicCommunicationForm form = (ElectronicCommunicationForm) convertFromXMLToObject(cleanXML(xml), ElectronicCommunicationForm.class);
		
		if (form == null) {
			LOG.warn("Cannot umarshall Electronic Communication Form.");
			return false;
		}
		
		if (form.getDetails() == null || form.getDetails().getType() == null) {
			LOG.warn("Cannot read type of the Electronic Communictation form. Should be 'complaint' or 'case'.");
			return false;
		}
		
		String type = form.getDetails().getType();
		
		if ("complaint".equals(type)){
			Complaint complaint = getComplaintDao().find(form.getDetails().getComplaintId());
			
			if (complaint == null) {
				LOG.warn("Cannot find complaint by given complaintId=" + form.getDetails().getComplaintId());
				return false;
			}
			
			cmisFolderId = complaint.getContainerFolder().getCmisFolderId();
			parentObjectType = FrevvoFormName.COMPLAINT.toUpperCase();
			parentObjectId = complaint.getComplaintId();
			parentObjectName = complaint.getComplaintNumber();		

			// Record user action
			getUserActionExecutor().execute(complaint.getComplaintId(), AcmUserActionName.LAST_COMPLAINT_MODIFIED, getAuthentication().getName());

		}else if ("case".equals(type)){	
			CaseFile caseFile = getCaseFileDao().find(form.getDetails().getCaseId());
			
			if (caseFile == null) {
				LOG.warn("Cannot find case by given caseId=" + form.getDetails().getCaseId());
				return false;
			}
			cmisFolderId = caseFile.getContainerFolder().getCmisFolderId();
			parentObjectType = FrevvoFormName.CASE_FILE.toUpperCase();
			parentObjectId = caseFile.getId();
			parentObjectName = caseFile.getCaseNumber();
			
			// Record user action
			getUserActionExecutor().execute(caseFile.getId(), AcmUserActionName.LAST_CASE_MODIFIED, getAuthentication().getName());
		}
			
		saveAttachments(attachments, cmisFolderId, parentObjectType, parentObjectId, parentObjectName);
		
		return true;
	}
	
	private JSONObject initFormData() {
		ElectronicCommunicationForm form = new ElectronicCommunicationForm();
		ElectronicCommunicationInformation information = new ElectronicCommunicationInformation();
		
		information.setDate(new Date());
		
		form.setInformation(information);
		
		Gson gson = new GsonBuilder().setDateFormat(DateFormats.FREVVO_DATE_FORMAT).create();
		String jsonString = gson.toJson(form);
		
		JSONObject json = new JSONObject(jsonString);
		
		return json;
	}

	@Override
	public String getFormName() {
		return FrevvoFormName.ELECTRONIC_COMMUNICATION;
	}

	public ComplaintDao getComplaintDao() {
		return complaintDao;
	}

	public void setComplaintDao(ComplaintDao complaintDao) {
		this.complaintDao = complaintDao;
	}

	public CaseFileDao getCaseFileDao() {
		return caseFileDao;
	}

	public void setCaseFileDao(CaseFileDao caseFileDao) {
		this.caseFileDao = caseFileDao;
	}
	
	

}
