/**
 * 
 */
package com.armedia.acm.form.closecomplaint.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.armedia.acm.form.closecomplaint.model.CloseComplaintFormEvent;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.form.closecomplaint.model.CloseComplaintForm;
import com.armedia.acm.form.closecomplaint.model.ExistingCase;
import com.armedia.acm.form.closecomplaint.model.ReferExternal;
import com.armedia.acm.form.config.ResolveInformation;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserActionName;
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
	private ApplicationEventPublisher applicationEventPublisher;
	private AcmPluginManager acmPluginManager;
	private FunctionalAccessService functionalAccessService;

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

		String mode = getRequest().getParameter("mode");
		
		// Convert XML data to Object
		CloseComplaintForm form = (CloseComplaintForm) convertFromXMLToObject(cleanXML(xml), CloseComplaintForm.class);
		
		if (form == null){
			LOG.warn("Cannot unmarshall Close Complaint Form.");
			return false;
		}
		
		// Get Complaint depends on the complaint ID
		Complaint complaint = getComplaintDao().find(form.getInformation().getId());
		
		if (complaint == null) {
			LOG.warn("Cannot find complaint by given complaintId=" + form.getInformation().getId());
			return false;
		}
		
		if (("IN APPROVAL".equals(complaint.getStatus()) || "CLOSED".equals(complaint.getStatus())) && !"edit".equals(mode)){
			LOG.info("The complaint is already in '" + complaint.getStatus() + "' mode. No further action will be taken.");
			return true;
		}

        CloseComplaintRequestFactory factory = new CloseComplaintRequestFactory();
        CloseComplaintRequest closeComplaintRequest = factory.fromFormXml(form, getAuthentication());
        
        if ("edit".equals(mode)){
        	String requestId = getRequest().getParameter("requestId");
        	CloseComplaintRequest closeComplaintRequestFromDatabase = null;
        	try{
	        	Long closeComplaintRequestId = Long.parseLong(requestId);
	        	closeComplaintRequestFromDatabase = getCloseComplaintRequestDao().find(closeComplaintRequestId);
        	}
        	catch(Exception e)
        	{
        		LOG.warn("Close Complaint Request with id=" + requestId + " is not found. The new request will be recorded in the database.");
        	}
        	
        	if (null != closeComplaintRequestFromDatabase){
        		closeComplaintRequest.setId(closeComplaintRequestFromDatabase.getId());
        		getCloseComplaintRequestDao().delete(closeComplaintRequestFromDatabase.getParticipants());
        	}
        }
        
        CloseComplaintRequest savedRequest = getCloseComplaintRequestDao().save(closeComplaintRequest);
        
        if (!"edit".equals(mode))
        {
        	// Record user action
        	getUserActionExecutor().execute(savedRequest.getId(), AcmUserActionName.LAST_CLOSE_COMPLAINT_CREATED, getAuthentication().getName());
        }
        else
        {
        	// Record user action
        	getUserActionExecutor().execute(savedRequest.getId(), AcmUserActionName.LAST_CLOSE_COMPLAINT_MODIFIED, getAuthentication().getName());
        }
		
		// Update Status to "IN APPROVAL"
		if (!complaint.getStatus().equals("IN APPROVAL") && !"edit".equals(mode)){
			complaint.setStatus("IN APPROVAL");
			getComplaintDao().save(complaint);
		}
		
		// Save attachments (or update XML form and PDF form if the mode is "edit")
        String cmisFolderId = findFolderId(complaint.getContainer(), complaint.getObjectType(), complaint.getId());
		FrevvoUploadedFiles uploadedFiles = saveAttachments(
                attachments,
                cmisFolderId,
                FrevvoFormName.COMPLAINT.toUpperCase(),
                complaint.getComplaintId());

		CloseComplaintFormEvent event = new CloseComplaintFormEvent(
				complaint.getComplaintNumber(), complaint.getComplaintId(), savedRequest, uploadedFiles, mode,
				getAuthentication().getName(), getUserIpAddress(), true);
				getApplicationEventPublisher().publishEvent(event);
		
		return true;
	}
	
	private Object initFormData(){

		String mode = getRequest().getParameter("mode");
		CloseComplaintForm closeComplaint = new CloseComplaintForm();
		
		ResolveInformation information = new ResolveInformation();
		if (!"edit".equals(mode))
		{
			information.setDate(new Date());
		}
		information.setResolveOptions(convertToList((String) getProperties().get(FrevvoFormName.CLOSE_COMPLAINT + ".dispositions"), ","));

		// Get Approvers
		String privilege = (String) getProperties().get(FrevvoFormName.CLOSE_COMPLAINT + ".approve.privilege");
		
		List<String> rolesForPrivilege = getAcmPluginManager().getRolesForPrivilege(privilege);
		Map<String, List<String>> rolesToGroups = getFunctionalAccessService().getApplicationRolesToGroups();
		
		// Get owning group
		String group = getOwningGroup();
		
		Set<AcmUser> usersSet = getFunctionalAccessService().getUsersByRolesAndGroups(rolesForPrivilege, rolesToGroups, group, null);
        
		List<AcmUser> acmUsers = new ArrayList<>(usersSet);
		
		List<String> approverOptions = new ArrayList<String>();
		if (acmUsers != null && acmUsers.size() > 0){
			for (AcmUser acmUser : acmUsers) {
				approverOptions.add(acmUser.getUserId() + "=" + acmUser.getFullName());
			}
		}
		
		ReferExternal referExternal = new ReferExternal();
		if (!"edit".equals(mode))
		{
			referExternal.setDate(new Date());
		}
		ContactMethod contact = new ContactMethod();
		contact.setTypes(convertToList((String) getProperties().get(FrevvoFormName.CLOSE_COMPLAINT + ".deviceTypes"), ","));
		referExternal.setContact(contact);
		
		closeComplaint.setInformation(information);
		closeComplaint.setApproverOptions(approverOptions);
		closeComplaint.setReferExternal(referExternal);
		
		Gson gson = new GsonBuilder().setDateFormat(DateFormats.FREVVO_DATE_FORMAT).create();
		String jsonString = gson.toJson(closeComplaint);
		
		JSONObject json = new JSONObject(jsonString);

		return json;
	}
	
	private String getOwningGroup()
	{
		String complaintIdString = getRequest().getParameter("complaintId");
		if (complaintIdString != null && !complaintIdString.isEmpty())
		{
			try
			{
				Long complaintId = Long.parseLong(complaintIdString);
				Complaint complaint = getComplaintDao().find(complaintId);
				
				if (complaint != null && complaint.getParticipants() != null)
				{
					for (AcmParticipant participant : complaint.getParticipants())
					{
						if ("owning group".equals(participant.getParticipantType()))
						{
							return participant.getParticipantLdapId();
						}
					}
				}
			}
			catch (Exception e)
			{
				LOG.warn("Cannot retrieve Complaint. The users for all groups will be returned.");
			}
		}
		
		return null;
	}
	
	private Object searchApprovers(String keyword){
		
		String mode = getRequest().getParameter("mode");
		CloseComplaintForm closeComplaint = new CloseComplaintForm();
		
		List<String> approverOptions = new ArrayList<String>();
		
		if (keyword != null){
			// Get Approvers
			List<AcmUser> acmUsers = getUserDao().findByFullNameKeyword(keyword);

			if (acmUsers != null && acmUsers.size() > 0){
				for (AcmUser acmUser : acmUsers) {
					// Add only users that are not the logged user
					if (!acmUser.getUserId().equals(getAuthentication().getName())  || "edit".equals(mode)){
						approverOptions.add(acmUser.getUserId() + "=" + acmUser.getFullName());
					}
				}
			}
		}
		
		closeComplaint.setApproverOptions(approverOptions);
		
		Gson gson = new GsonBuilder().setDateFormat(DateFormats.FREVVO_DATE_FORMAT).create();
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
		
		Gson gson = new GsonBuilder().setDateFormat(DateFormats.FREVVO_DATE_FORMAT).create();
		String jsonString = gson.toJson(closeComplaint);
		
		JSONObject json = new JSONObject(jsonString);
		
		return json;
	}

    @Override
    public String getFormName()
    {
        return FrevvoFormName.CLOSE_COMPLAINT;
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

	public ApplicationEventPublisher getApplicationEventPublisher()
	{
		return applicationEventPublisher;
	}

	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
	{
		this.applicationEventPublisher = applicationEventPublisher;
	}

	public AcmPluginManager getAcmPluginManager() {
		return acmPluginManager;
	}

	public void setAcmPluginManager(AcmPluginManager acmPluginManager) {
		this.acmPluginManager = acmPluginManager;
	}

	public FunctionalAccessService getFunctionalAccessService() {
		return functionalAccessService;
	}

	public void setFunctionalAccessService(
			FunctionalAccessService functionalAccessService) {
		this.functionalAccessService = functionalAccessService;
	}
}
