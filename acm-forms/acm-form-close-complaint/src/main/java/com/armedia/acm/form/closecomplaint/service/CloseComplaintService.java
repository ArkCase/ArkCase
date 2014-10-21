/**
 * 
 */
package com.armedia.acm.form.closecomplaint.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.form.closecomplaint.model.CloseComplaintForm;
import com.armedia.acm.form.closecomplaint.model.CloseComplaintInformation;
import com.armedia.acm.form.closecomplaint.model.ExistingCase;
import com.armedia.acm.form.closecomplaint.model.ReferExternal;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.users.model.AcmUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author riste.tutureski
 *
 */
public class CloseComplaintService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(CloseComplaintService.class);
	private ComplaintDao complaintDao;
	private CaseFileDao caseFileDao;
    private CloseComplaintRequestDao closeComplaintRequestDao;
			
	/* (non-Javadoc)
	 * @see com.armedia.acm.frevvo.config.FrevvoFormService#init()
	 */
	@Override
	public Object init() {

		Object result = "";
		
		String type = getRequest().getParameter("type");
		
		if ("approver".equals(type))
		{
			// TODO: Call service to get the XML form for editing
		}
		
		return result;
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
			
			if ("search-approvers".equals(action)) {
				String keyword = getRequest().getParameter("keyword");
				result = searchApprovers(keyword);
			}
			
			if ("case".equals(action)) {
				String caseNumber = getRequest().getParameter("caseNumber");
				result = getCase(caseNumber);

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

		// Convert XML data to Object
		CloseComplaintForm form = (CloseComplaintForm) convertFromXMLToObject(cleanXML(xml), CloseComplaintForm.class);
		
		if (form == null){
			LOG.warn("Cannot unmarshall Close Complaint Form.");
			return false;
		}
		
		// Get Complaint depends on the complaint ID
		Complaint complaint = getComplaintDao().find(form.getInformation().getComplaintId());
		
		if (complaint == null) {
			LOG.warn("Cannot find complaint by given complaintId=" + form.getInformation().getComplaintId());
			return false;
		}
		
		if ("IN APPROVAL".equals(complaint.getStatus()) || "CLOSED".equals(complaint.getStatus())){
			LOG.info("The complaint is already in '" + complaint.getStatus() + "' mode. No further action will be taken.");
			return true;
		}

        CloseComplaintRequestFactory factory = new CloseComplaintRequestFactory();
        CloseComplaintRequest closeComplaintRequest = factory.fromFormXml(form, getAuthentication());
        getCloseComplaintRequestDao().save(closeComplaintRequest);
		
		// Update Status to "IN APPROVAL"
		if (!complaint.getStatus().equals("IN APPROVAL")){
			getComplaintDao().updateComplaintStatus(complaint.getComplaintId(), "IN APPROVAL", getAuthentication().getName(), form.getInformation().getCloseDate());
		}
		
		// Save attachments
		saveAttachments(attachments, complaint.getEcmFolderId(), FrevvoFormName.COMPLAINT.toUpperCase(), complaint.getComplaintId(), complaint.getComplaintNumber());
		
		return true;
	}
	
	private Object initFormData(){
		
		String type = getRequest().getParameter("type");
		CloseComplaintForm closeComplaint = new CloseComplaintForm();
		
		CloseComplaintInformation information = new CloseComplaintInformation();
		if (!"approver".equals(type))
		{
			information.setCloseDate(new Date());
		}
		information.setDispositions(convertToList((String) getProperties().get(FrevvoFormName.CLOSE_COMPLAINT + ".dispositions"), ","));
		
		// Get Approvers
		List<AcmUser> acmUsers = getUserDao().findByFullNameKeyword("");
		
		List<String> approverOptions = new ArrayList<String>();
		if (acmUsers != null && acmUsers.size() > 0){
			for (AcmUser acmUser : acmUsers) {
				// Add only users that are not the logged user
				if (!acmUser.getUserId().equals(getAuthentication().getName()) || "approver".equals(type)){
					approverOptions.add(acmUser.getUserId() + "=" + acmUser.getFullName());
				}
			}
		}
		
		ReferExternal referExternal = new ReferExternal();
		if (!"approver".equals(type))
		{
			referExternal.setDate(new Date());
		}
		ContactMethod contact = new ContactMethod();
		contact.setTypes(convertToList((String) getProperties().get(FrevvoFormName.CLOSE_COMPLAINT + ".deviceTypes"), ","));
		referExternal.setContact(contact);
		
		closeComplaint.setTypes(convertToList((String) getProperties().get(FrevvoFormName.CLOSE_COMPLAINT + ".types"), ","));
		closeComplaint.setInformation(information);
		closeComplaint.setApproverOptions(approverOptions);
		closeComplaint.setReferExternal(referExternal);
		
		Gson gson = new GsonBuilder().setDateFormat("M/dd/yyyy").create();
		String jsonString = gson.toJson(closeComplaint);
		
		JSONObject json = new JSONObject(jsonString);
		
		return json;
	}
	
	private Object searchApprovers(String keyword){
		
		String type = getRequest().getParameter("type");
		CloseComplaintForm closeComplaint = new CloseComplaintForm();
		
		List<String> approverOptions = new ArrayList<String>();
		
		if (keyword != null){
			// Get Approvers
			List<AcmUser> acmUsers = getUserDao().findByFullNameKeyword(keyword);

			if (acmUsers != null && acmUsers.size() > 0){
				for (AcmUser acmUser : acmUsers) {
					// Add only users that are not the logged user
					if (!acmUser.getUserId().equals(getAuthentication().getName())  || "approver".equals(type)){
						approverOptions.add(acmUser.getUserId() + "=" + acmUser.getFullName());
					}
				}
			}
		}
		
		closeComplaint.setApproverOptions(approverOptions);
		
		Gson gson = new GsonBuilder().setDateFormat("M/dd/yyyy").create();
		String jsonString = gson.toJson(closeComplaint);
		
		JSONObject json = new JSONObject(jsonString);
		
		return json;
	}
	
	private Object getCase(String caseNumber){
		CloseComplaintForm closeComplaint = new CloseComplaintForm();
		ExistingCase existingCase = new ExistingCase();

		CaseFile caseFile = null;
		
		try{
			caseFile = getCaseFileDao().findByCaseNumber(caseNumber);
		}catch(Exception e){
			LOG.warn("The case with number '" + caseNumber + "' doesn't exist.");
		}
		
		if (caseFile != null){
			existingCase.setCaseNumber(caseNumber);
			existingCase.setCaseTitle(caseFile.getTitle());
			existingCase.setCaseCreationDate(caseFile.getCreated());
			existingCase.setCasePriority(caseFile.getPriority());
		}
		
		closeComplaint.setExistingCase(existingCase);
		
		Gson gson = new GsonBuilder().setDateFormat("M/dd/yyyy").create();
		String jsonString = gson.toJson(closeComplaint);
		
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

    public CloseComplaintRequestDao getCloseComplaintRequestDao()
    {
        return closeComplaintRequestDao;
    }

    public void setCloseComplaintRequestDao(CloseComplaintRequestDao closeComplaintRequestDao)
    {
        this.closeComplaintRequestDao = closeComplaintRequestDao;
    }
}
