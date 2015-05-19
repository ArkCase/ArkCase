/**
 * 
 */
package com.armedia.acm.form.project.service;

import java.util.List;

import javax.persistence.PersistenceException;

import org.activiti.engine.RuntimeService;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.form.casefile.service.CaseFileWorkflowListener;
import com.armedia.acm.form.config.xml.OwningGroupItem;
import com.armedia.acm.form.project.model.ProjectForm;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.SaveCaseService;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;

/**
 * @author riste.tutureski
 *
 */
public class ProjectService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private ProjectFactory projectFactory;
	private SaveCaseService saveCaseService;
	private CaseFileDao caseFileDao;
	private FileWorkflowBusinessRule fileWorkflowBusinessRule;
	private RuntimeService activitiRuntimeService;
	private CaseFile caseFile;
	
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
			
			if ("init-participants-groups".equals(action)) 
			{
				result = initParticipantsAndGroupsInfo();
			}
		}
		
		return result;
	}

	@Override
	public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception 
	{
		// Convert XML to Object
		ProjectForm form = (ProjectForm) convertFromXMLToObject(cleanXML(xml), ProjectForm.class);
		
		if (form == null)
		{
			LOG.warn("Cannot unmarshall Project Form.");
			return false;
		}
		
		// Save ProjectForm to the database
		form = saveProjectForm(form);
		
		updateXMLAttachment(attachments, FrevvoFormName.PROJECT, form);
		
		// Save Attachments
		FrevvoUploadedFiles frevvoFiles = saveAttachments(
                attachments,
                form.getCmisFolderId(),
                FrevvoFormName.CASE_FILE.toUpperCase(),
                form.getId());
		
		String mode = getRequest().getParameter("mode");
		if ( !"edit".equals(mode) )
		{
			CaseFileWorkflowListener workflowListener = new CaseFileWorkflowListener();
			workflowListener.handleNewCaseFile(
                    getCaseFile(),
                    frevvoFiles,
                    getActivitiRuntimeService(),
                    getFileWorkflowBusinessRule(),
                    this);
		}
		
		return true;
	}
	
	private ProjectForm saveProjectForm(ProjectForm form) throws AcmCreateObjectFailedException
	{
		LOG.info("Saving Project form ...");
		
		CaseFile caseFile = null;
		
		// Edit mode
		String mode = getRequest().getParameter("mode");
		if (mode != null && "edit".equals(mode) && form.getId() != null)
		{
			caseFile = getCaseFileDao().find(form.getId());
		}
		
		caseFile = getProjectFactory().asAcmCaseFile(form, caseFile);
		
		// Save Case file
		try
        {
			caseFile = getSaveCaseService().saveCase(caseFile, getAuthentication(), getUserIpAddress());
        }
		catch (MuleException | PersistenceException e)
        {
            throw new AcmCreateObjectFailedException("Case File", e.getMessage(), e);
        }
		
		setCaseFile(caseFile);
		
		form = getProjectFactory().asFrevvoProjectForm(caseFile, form, this);
		
		return form;
	}

	@Override
	public String getFormName() 
	{
		return FrevvoFormName.PROJECT;
	}
	
	private Object initFormData()
	{
		ProjectForm form = new ProjectForm();
		
		// TODO: Init information when needed. For this demo is not needed
		
		JSONObject json = createResponse(form);

		return json;
	}
	
	private JSONObject initParticipantsAndGroupsInfo()
	{
		ProjectForm form = new ProjectForm();
		
		// Init Participant types
		List<String> participantTypes = convertToList((String) getProperties().get(FrevvoFormName.PROJECT + ".participantTypes"), ",");
		form.setParticipantsTypeOptions(participantTypes);
		
		form.setParticipantsPrivilegeTypes(getParticipantsPrivilegeTypes(participantTypes, FrevvoFormName.PROJECT));
		
		// Init Owning Group information
		String owningGroupType = (String) getProperties().get(FrevvoFormName.PROJECT + ".owningGroupType");
		OwningGroupItem owningGroupItem = new OwningGroupItem();
		owningGroupItem.setType(owningGroupType);
		
		form.setOwningGroup(owningGroupItem);
		form.setOwningGroupOptions(getOwningGroups(owningGroupType, FrevvoFormName.PROJECT));
		
		JSONObject json = createResponse(form);
		
		return json;
	}
	
	public void updateXML(CaseFile caseFile, Authentication auth)
    {
    	if (caseFile != null)
    	{
    		// First find the XML that is already in the system and create Frevvo form
    		Long containerId = caseFile.getContainer().getId();
    		Long folderId = caseFile.getContainer().getFolder().getId();
    		String fileType = FrevvoFormName.PROJECT.toLowerCase() + "_xml";
    		
    		EcmFile ecmFile = getEcmFileDao().findForContainerFolderAndFileType(containerId, folderId, fileType);
    		ProjectForm form = (ProjectForm) getExistingForm(ecmFile.getId(), ProjectForm.class);
    		
    		// Update eBrief form with the new data provided in the Case File
    		form = getProjectFactory().asFrevvoProjectForm(caseFile, form, this);
    		
    		if (form != null)
    		{
    			String xml = convertFromObjectToXML(form);
    			updateXML(xml, ecmFile, auth);		
    		}
    	}
    	
    }
	
	public ProjectFactory getProjectFactory() {
		return projectFactory;
	}

	public void setProjectFactory(ProjectFactory projectFactory) {
		this.projectFactory = projectFactory;
	}

	public SaveCaseService getSaveCaseService() {
		return saveCaseService;
	}

	public void setSaveCaseService(SaveCaseService saveCaseService) {
		this.saveCaseService = saveCaseService;
	}

	public CaseFileDao getCaseFileDao() {
		return caseFileDao;
	}

	public void setCaseFileDao(CaseFileDao caseFileDao) {
		this.caseFileDao = caseFileDao;
	}

	public FileWorkflowBusinessRule getFileWorkflowBusinessRule() {
		return fileWorkflowBusinessRule;
	}

	public void setFileWorkflowBusinessRule(
			FileWorkflowBusinessRule fileWorkflowBusinessRule) {
		this.fileWorkflowBusinessRule = fileWorkflowBusinessRule;
	}

	public RuntimeService getActivitiRuntimeService() {
		return activitiRuntimeService;
	}

	public void setActivitiRuntimeService(RuntimeService activitiRuntimeService) {
		this.activitiRuntimeService = activitiRuntimeService;
	}

	public CaseFile getCaseFile() {
		return caseFile;
	}

	public void setCaseFile(CaseFile caseFile) {
		this.caseFile = caseFile;
	}

}
