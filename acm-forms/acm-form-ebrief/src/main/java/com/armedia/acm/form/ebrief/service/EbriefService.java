/**
 * 
 */
package com.armedia.acm.form.ebrief.service;

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
import com.armedia.acm.form.ebrief.model.EbriefConstants;
import com.armedia.acm.form.ebrief.model.EbriefForm;
import com.armedia.acm.form.ebrief.model.xml.EbriefDetails;
import com.armedia.acm.form.ebrief.model.xml.EbriefInformation;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.model.FrevvoFormConstants;
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
public class EbriefService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private EbriefFactory ebriefFactory;
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
			
			if ("init-location-data".equals(action)) 
			{
				result = initLocationData();
			}
		}
		
		return result;
	}

	@Override
	public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception 
	{
		// Convert XML to Object
		EbriefForm form = (EbriefForm) convertFromXMLToObject(cleanXML(xml), EbriefForm.class);
		
		if (form == null)
		{
			LOG.warn("Cannot unmarshall eBrief Form.");
			return false;
		}
		
		// Save eBrief to the database
		form = saveEBrief(form);
		
		updateXMLAttachment(attachments, FrevvoFormName.EBRIEF, form);
		
		// Change PDF file name
		attachments = updateFileName(getCaseFile().getTitle(), FrevvoFormConstants.PDF, attachments);
		
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
	
	private EbriefForm saveEBrief(EbriefForm form) throws AcmCreateObjectFailedException
	{
		LOG.info("Saving eBrief ...");
		
		CaseFile caseFile = null;
		
		// Edit mode
		String mode = getRequest().getParameter("mode");
		if (mode != null && "edit".equals(mode) && form.getId() != null)
		{
			caseFile = getCaseFileDao().find(form.getId());
		}
		
		caseFile = getEbriefFactory().asAcmCaseFile(form, caseFile);
		
		// Save Case file
		try
        {
			caseFile = getSaveCaseService().saveCase(caseFile, getAuthentication(), getUserIpAddress());
        }
		catch (MuleException | PersistenceException e)
        {
            throw new AcmCreateObjectFailedException("eBrief", e.getMessage(), e);
        }
		
		setCaseFile(caseFile);
		
		form = getEbriefFactory().asFrevvoEbriefForm(caseFile, form, this);
		
		return form;
	}
	
	private Object initFormData()
	{
		EbriefInformation information = new EbriefInformation();
		
		information.setTypes(convertToList((String) getProperties().get(FrevvoFormName.EBRIEF + ".types"), ","));
		
		JSONObject json = createResponse(information);

		return json;
	}
	
	private Object initLocationData()
	{
		EbriefDetails details = new EbriefDetails();
		
		details.setCourtLocations(convertToList((String) getProperties().get(FrevvoFormName.EBRIEF + ".court.locations"), ","));
		
		JSONObject json = createResponse(details);

		return json;
	}
	
	public void updateXML(CaseFile caseFile, Authentication auth)
    {
    	if (caseFile != null)
    	{
    		// First find the XML that is already in the system and create Frevvo form
    		Long containerId = caseFile.getContainer().getId();
    		Long folderId = caseFile.getContainer().getFolder().getId();
    		String fileType = FrevvoFormName.EBRIEF.toLowerCase() + "_xml";
    		
    		EcmFile ecmFile = getEcmFileDao().findForContainerFolderAndFileType(containerId, folderId, fileType);
    		EbriefForm form = (EbriefForm) getExistingForm(ecmFile.getId(), EbriefForm.class);
    		
    		// Update eBrief form with the new data provided in the Case File
    		form = getEbriefFactory().asFrevvoEbriefForm(caseFile, form, this);
    		
    		if (form != null)
    		{
    			String xml = convertFromObjectToXML(form);
    			updateXML(xml, ecmFile, auth);		
    		}
    	}
    	
    }

	@Override
	public String getFormName() 
	{
		return FrevvoFormName.EBRIEF;
	}

	public CaseFileDao getCaseFileDao() {
		return caseFileDao;
	}

	public void setCaseFileDao(CaseFileDao caseFileDao) {
		this.caseFileDao = caseFileDao;
	}
	

	public SaveCaseService getSaveCaseService() {
		return saveCaseService;
	}

	public void setSaveCaseService(SaveCaseService saveCaseService) {
		this.saveCaseService = saveCaseService;
	}

	public EbriefFactory getEbriefFactory() {
		return ebriefFactory;
	}

	public void setEbriefFactory(EbriefFactory ebriefFactory) {
		this.ebriefFactory = ebriefFactory;
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
