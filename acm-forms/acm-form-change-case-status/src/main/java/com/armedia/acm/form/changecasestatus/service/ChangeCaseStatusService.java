/**
 * 
 */
package com.armedia.acm.form.changecasestatus.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.armedia.acm.form.changecasestatus.model.ChangeCaseStatusForm;
import com.armedia.acm.form.changecasestatus.model.ChangeCaseStatusFormEvent;
import com.armedia.acm.form.config.ResolveInformation;
import com.armedia.acm.frevvo.config.FrevvoFormName;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.dao.ChangeCaseStatusDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserActionName;

/**
 * @author riste.tutureski
 *
 */
public class ChangeCaseStatusService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private CaseFileDao caseFileDao;
	private ChangeCaseStatusDao changeCaseStatusDao;
	private ApplicationEventPublisher applicationEventPublisher;

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

		String mode = getRequest().getParameter("mode");
		
		// Convert XML data to Object
		ChangeCaseStatusForm form = (ChangeCaseStatusForm) convertFromXMLToObject(cleanXML(xml), ChangeCaseStatusForm.class);
		
		if (form == null)
		{
			LOG.warn("Cannot unmarshall Close Case Form.");
			return false;
		}
		
		// Get CaseFile depends on the CaseFile ID
		CaseFile caseFile = getCaseFileDao().find(form.getInformation().getId());
		
		if (caseFile == null) 
		{
			LOG.warn("Cannot find case file by given caseId=" + form.getInformation().getId());
			return false;
		}
		
		// Skip if the case is already closed and if it's not edit mode 
		if ("CLOSED".equals(caseFile.getStatus()) && !"edit".equals(mode))
		{
			LOG.info("The case file is already in '" + caseFile.getStatus() + "' mode. No further action will be taken.");
			return true;
		}
		
		ChangeCaseStatusRequestFactory factory = new ChangeCaseStatusRequestFactory();
		ChangeCaseStatus changeCaseStatus = factory.formFromXml(form, getAuthentication());
		
		if ("edit".equals(mode))
		{
			String requestId = getRequest().getParameter("requestId");
			ChangeCaseStatus changeCaseStatusFromDatabase = null;
			
			try{
	        	Long changeCaseStatusId = Long.parseLong(requestId);
	        	changeCaseStatusFromDatabase = getChangeCaseStatusDao().find(changeCaseStatusId);
        	}
        	catch(Exception e)
        	{
        		LOG.warn("Close Case Request with id=" + requestId + " is not found. The new request will be recorded in the database.");
        	}
			
			if (null != changeCaseStatusFromDatabase){
				changeCaseStatus.setId(changeCaseStatusFromDatabase.getId());
        		getChangeCaseStatusDao().delete(changeCaseStatusFromDatabase.getParticipants());
        	}
		}
		
		ChangeCaseStatus savedRequest = getChangeCaseStatusDao().save(changeCaseStatus);
		
		if (!"edit".equals(mode))
        {
        	// Record user action
        	getUserActionExecutor().execute(savedRequest.getId(), AcmUserActionName.LAST_CHANGE_CASE_STATUS_CREATED, getAuthentication().getName());
        }
        else
        {
        	// Record user action
        	getUserActionExecutor().execute(savedRequest.getId(), AcmUserActionName.LAST_CHANGE_CASE_STATUS_MODIFIED, getAuthentication().getName());
        }
		
		// Save attachments (or update XML form and PDF form if the mode is "edit")
        String cmisFolderId = findFolderId(caseFile.getContainer(), caseFile.getObjectType(), caseFile.getId());
		FrevvoUploadedFiles uploadedFiles = saveAttachments(
                attachments,
                cmisFolderId,
                FrevvoFormName.CASE_FILE.toUpperCase(),
                caseFile.getId());
		
		ChangeCaseStatusFormEvent event = new ChangeCaseStatusFormEvent(caseFile.getCaseNumber(), caseFile.getId(), savedRequest, uploadedFiles, mode, getAuthentication().getName(), getUserIpAddress(), true);
		getApplicationEventPublisher().publishEvent(event);
		
		return true;
	}

    @Override
    public String getFormName()
    {
        return FrevvoFormName.CHANGE_CASE_STATUS;
    }
    
    private Object initFormData()
    {
    	String mode = getRequest().getParameter("mode");
    	ChangeCaseStatusForm changeCaseStatus = new ChangeCaseStatusForm();
    	
    	ResolveInformation information = new ResolveInformation();
		if (!"edit".equals(mode))
		{
			information.setDate(new Date());
		}
		information.setResolveOptions(convertToList((String) getProperties().get(FrevvoFormName.CHANGE_CASE_STATUS + ".statuses"), ","));
		
		// Get Approvers
		List<AcmUser> acmUsers = getUserDao().findByFullNameKeyword("");
		
		List<String> approverOptions = new ArrayList<String>();
		if (acmUsers != null && acmUsers.size() > 0){
			for (AcmUser acmUser : acmUsers) {
				// Add only users that are not the logged user
				if (!acmUser.getUserId().equals(getAuthentication().getName()) || "edit".equals(mode)){
					approverOptions.add(acmUser.getUserId() + "=" + acmUser.getFullName());
				}
			}
		}

		String caseResolutions = (String) getProperties().get(FrevvoFormName.CHANGE_CASE_STATUS + ".resolutions");
		List<String> resolutions = convertToList(caseResolutions, ",");
		changeCaseStatus.setResolutions(resolutions);
		
		changeCaseStatus.setInformation(information);
		changeCaseStatus.setApproverOptions(approverOptions);
    	
		JSONObject json = createResponse(changeCaseStatus);

		return json;
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

	public ChangeCaseStatusDao getChangeCaseStatusDao() {
		return changeCaseStatusDao;
	}

	public void setChangeCaseStatusDao(ChangeCaseStatusDao changeCaseStatusDao) {
		this.changeCaseStatusDao = changeCaseStatusDao;
	}

	public ApplicationEventPublisher getApplicationEventPublisher() {
		return applicationEventPublisher;
	}

	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

}
