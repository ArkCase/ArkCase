/**
 * 
 */
package com.armedia.acm.form.casefile.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;

import org.activiti.engine.RuntimeService;
import org.drools.core.RuntimeDroolsException;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.form.casefile.model.AddressHistory;
import com.armedia.acm.form.casefile.model.CaseFileForm;
import com.armedia.acm.form.casefile.model.EmploymentHistory;
import com.armedia.acm.form.casefile.model.Subject;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.SaveCaseService;
import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.dao.PersonIdentificationDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.service.history.dao.AcmHistoryDao;
import com.armedia.acm.service.history.model.AcmHistory;
import com.armedia.acm.services.users.model.AcmUserActionName;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private CaseFileFactory caseFileFactory = new CaseFileFactory();
	private SaveCaseService saveCaseService;
	private AcmHistoryDao acmHistoryDao;
	private CaseFileDao caseFileDao;
	private ObjectAssociationDao objectAssociationDao;
	private PersonIdentificationDao personIdentificationDao;
	private FileWorkflowBusinessRule fileWorkflowBusinessRule;

	private RuntimeService activitiRuntimeService;

	private CaseFile caseFile;

	/* (non-Javadoc)
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

	/* (non-Javadoc)
	 * @see com.armedia.acm.frevvo.config.FrevvoFormService#save(java.lang.String, org.springframework.util.MultiValueMap)
	 */
	@Override
	public boolean save(String xml,
			MultiValueMap<String, MultipartFile> attachments) throws Exception 
	{
		
		// Convert XML to Object
		CaseFileForm form = (CaseFileForm) convertFromXMLToObject(cleanXML(xml), CaseFileForm.class);
		
		if (form == null)
		{
			LOG.warn("Cannot unmarshall Close Case Form.");
			return false;
		}
		
		// Save Case File to the database
		form = saveCaseFile(form);
		
		// Save Address History
		form = saveAddressHistory(form);
		
		// Save Employment History
		form = saveEmploymentHistory(form);
		
		// Save Reference (Reinvestigation)
		form = saveReference(form);
		
		// Cave Attachments
		FrevvoUploadedFiles frevvoFiles = saveAttachments(attachments, form.getCmisFolderId(),
				FrevvoFormName.CASE_FILE.toUpperCase(), form.getId(), form.getNumber());
		
		// Log the last user action
		if (null != form && null != form.getId())
		{
			getUserActionExecutor().execute(form.getId(), AcmUserActionName.LAST_CASE_CREATED, getAuthentication().getName());
		}


		String mode = getRequest().getParameter("mode");
		if ( !"edit".equals(mode) )
		{
			CaseFileWorkflowListener workflowListener = new CaseFileWorkflowListener();
			workflowListener.handleNewCaseFile(getCaseFile(), frevvoFiles, getActivitiRuntimeService(), getFileWorkflowBusinessRule());
		}

		return true;
	}
	
	private CaseFileForm saveCaseFile(CaseFileForm form) throws AcmCreateObjectFailedException 
	{
		LOG.info("Saving case file ...");
		
		CaseFile caseFile = null;
		
		String mode = getRequest().getParameter("mode");
		if (mode != null && "edit".equals(mode))
		{
			String caseIdAsString = getRequest().getParameter("caseId");
			Long caseId = null;
			try
			{
				caseId = Long.parseLong(caseIdAsString);
			}
			catch(Exception e)
			{
				LOG.error("Cannot parse String caseId=" + caseIdAsString + " to Long.", e);
			}
			
			if (caseId != null)
			{
				caseFile = getCaseFileDao().find(caseId);
			}
		}
		
		caseFile = getCaseFileFactory().asAcmCaseFile(form, caseFile);
		HttpSession session = getRequest().getSession();
		String ipAddress = (String) session.getAttribute("acm_ip_address");
		
		// Save Case file
		try
        {
			caseFile = getSaveCaseService().saveCase(caseFile, getAuthentication(), ipAddress);
        }
		catch (MuleException | PersistenceException | RuntimeDroolsException e)
        {
            throw new AcmCreateObjectFailedException("Case File", e.getMessage(), e);
        }
		
		
		
		// Add id's and other information to the Frevvo form
		form.setId(caseFile.getId());
		form.setCmisFolderId(caseFile.getEcmFolderId());
		form.setNumber(caseFile.getCaseNumber());
		
		// Add Address History id's to the Frevvo form
		List<AddressHistory> addressHistoryArray = form.getAddressHistory();
		if (addressHistoryArray != null && addressHistoryArray.size() > 0 && 
				caseFile.getOriginator() != null && caseFile.getOriginator().getPerson() != null  &&
				caseFile.getOriginator().getPerson().getAddresses() != null && 
				caseFile.getOriginator().getPerson().getAddresses().size() > 0)
		{
			for (int i = 0; i < addressHistoryArray.size(); i++)
			{
				if (i < caseFile.getOriginator().getPerson().getAddresses().size()) 
				{
					addressHistoryArray.get(i).setLocation(caseFile.getOriginator().getPerson().getAddresses().get(i));
				}
			}
		}
		
		// Add Employment History id's to the Frevvo form
		List<EmploymentHistory> employmentHistoryArray = form.getEmploymentHistory();
		if (employmentHistoryArray != null && employmentHistoryArray.size() > 0 && 
				caseFile.getOriginator() != null && caseFile.getOriginator().getPerson() != null  &&
				caseFile.getOriginator().getPerson().getOrganizations() != null && 
				caseFile.getOriginator().getPerson().getOrganizations().size() > 0)
		{
			int organizationIndex = 0;
			for (int i = 0; i < employmentHistoryArray.size(); i++)
			{
				if (employmentHistoryArray.get(i).getOrganization() != null && organizationIndex < caseFile.getOriginator().getPerson().getOrganizations().size())
				{
					employmentHistoryArray.get(i).setOrganization(caseFile.getOriginator().getPerson().getOrganizations().get(organizationIndex));
					organizationIndex++;
				}
			}
		}
		
		if (caseFile.getOriginator() != null && caseFile.getOriginator().getPerson() != null)
		{			
			form.getSubject().setId(caseFile.getOriginator().getPerson().getId());	
		}

		setCaseFile(caseFile);
		
		return form;
	}
	
	private CaseFileForm saveAddressHistory(CaseFileForm form)
	{
		LOG.info("Saving address history ...");
		
		String objectType = "POSTAL_ADDRESS";
		Long personId = form.getSubject().getId();
		List<AddressHistory> addressHistoryArray = form.getAddressHistory();
		
		if (personId != null && addressHistoryArray != null && addressHistoryArray.size() > 0)
		{
			String mode = getRequest().getParameter("mode");
			if (mode != null && "edit".equals(mode))
			{
				getAcmHistoryDao().deleteByPersonIdAndObjectType(personId, objectType);
			}
			
			for (AddressHistory addressHistory : addressHistoryArray)
			{
				AcmHistory acmHistory = new AcmHistory();
				acmHistory.setPersonId(personId);
				acmHistory.setObjectId(addressHistory.getLocation().getId());
				acmHistory.setObjectType(objectType);
				acmHistory.setStartDate(addressHistory.getStartDate());
				acmHistory.setEndDate(addressHistory.getEndDate());
				
				getAcmHistoryDao().save(acmHistory);
			}
		}
		
		return form;
	}
	
	private CaseFileForm saveEmploymentHistory(CaseFileForm form)
	{
		LOG.info("Saving employment history ...");
		
		String objectType = "ORGANIZATION";
		Long personId = form.getSubject().getId();
		List<EmploymentHistory> employmentHistoryArray = form.getEmploymentHistory();
		
		if (personId != null && employmentHistoryArray != null && employmentHistoryArray.size() > 0)
		{
			String mode = getRequest().getParameter("mode");
			if (mode != null && "edit".equals(mode))
			{
				getAcmHistoryDao().deleteByPersonIdAndObjectType(personId, objectType);
			}
			
			for (EmploymentHistory employmentHistory : employmentHistoryArray)
			{
				AcmHistory acmHistory = new AcmHistory();
				acmHistory.setPersonId(personId);
				
				if (employmentHistory.getOrganization() != null)
				{
					acmHistory.setObjectId(employmentHistory.getOrganization().getOrganizationId());
				}
				else if (employmentHistory.getType() != null)
				{
					acmHistory.setPersonType(employmentHistory.getType());
				}
				
				acmHistory.setObjectType(objectType);
				acmHistory.setStartDate(employmentHistory.getStartDate());
				acmHistory.setEndDate(employmentHistory.getEndDate());
				
				getAcmHistoryDao().save(acmHistory);
			}
		}
		
		return form;
	}
	
	private CaseFileForm saveReference(CaseFileForm form)
	{		
		String mode = getRequest().getParameter("mode");
		if (mode != null && "reinvestigate".equals(mode))
		{
			LOG.info("Saving reference ...");
			
			String oldCaseIdAsString = getRequest().getParameter("caseId");
			String oldCaseNumber = getRequest().getParameter("caseNumber");
			Long oldCaseId = null;
			try
			{
				oldCaseId = Long.parseLong(oldCaseIdAsString);
			}
			catch(Exception e)
			{
				LOG.error("Cannot parse String oldCaseId=" + oldCaseIdAsString + " to Long.", e);
			}
			
			if (oldCaseId != null && oldCaseNumber != null && form.getId() != null && form.getNumber() != null)
			{		
				String status = "ACTIVE";
				CaseFile caseFile = getCaseFileDao().find(oldCaseId);
				if (caseFile != null && caseFile.getStatus() != null)
				{
					status = caseFile.getStatus();
				}
				
				ObjectAssociation objectAssociation = new ObjectAssociation();
				
				objectAssociation.setStatus(status);
				objectAssociation.setParentType("CASE_FILE");
				objectAssociation.setParentId(form.getId());
				objectAssociation.setParentName(form.getNumber());
				objectAssociation.setTargetType("CASE_FILE");
				objectAssociation.setTargetId(oldCaseId);
				objectAssociation.setTargetName(oldCaseNumber);
				
				getObjectAssociationDao().save(objectAssociation);
			}
		}
		
		return form;
	}
	
	/* (non-Javadoc)
	 * @see com.armedia.acm.frevvo.config.FrevvoFormService#getFormName()
	 */
	@Override
	public String getFormName() 
	{
		return FrevvoFormName.CASE_FILE;
	}
	
	private Object initFormData()
	{
		CaseFileForm caseFileForm = new CaseFileForm();
		Subject subject = new Subject();
		
		AddressHistory addressHistory = new AddressHistory();
		PostalAddress postalAddress = new PostalAddress();
		List<AddressHistory> addressHistoryList = new ArrayList<AddressHistory>();
		
		EmploymentHistory employmentHistory = new EmploymentHistory();
		Organization organization = new Organization();
		List<EmploymentHistory> employmentHistoryList = new ArrayList<EmploymentHistory>();
		
		caseFileForm.setType("Background Investigation");
		caseFileForm.setTypes(convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".types"), ","));
		
		subject.setTitles(convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".titles"), ","));
		
		postalAddress.setTypes(convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".locationTypes"), ","));
		postalAddress.setType("Home");
		addressHistory.setLocation(postalAddress);
		addressHistoryList.add(addressHistory);
		
		organization.setOrganizationTypes(convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".organizationTypes"), ","));
		employmentHistory.setOrganization(organization);
		employmentHistoryList.add(employmentHistory);
		
		caseFileForm.setSubject(subject);
		caseFileForm.setAddressHistory(addressHistoryList);
		caseFileForm.setEmploymentHistory(employmentHistoryList);
		
		Gson gson = new GsonBuilder().setDateFormat("M/dd/yyyy").create();
		String jsonString = gson.toJson(caseFileForm);
		
		JSONObject json = new JSONObject(jsonString);

		return json;
	}

	public CaseFileFactory getCaseFileFactory() 
	{
		return caseFileFactory;
	}

	public void setCaseFileFactory(CaseFileFactory caseFileFactory) 
	{
		this.caseFileFactory = caseFileFactory;
	}

	public SaveCaseService getSaveCaseService() 
	{
		return saveCaseService;
	}

	public void setSaveCaseService(SaveCaseService saveCaseService) 
	{
		this.saveCaseService = saveCaseService;
	}

	public AcmHistoryDao getAcmHistoryDao() 
	{
		return acmHistoryDao;
	}

	public void setAcmHistoryDao(AcmHistoryDao acmHistoryDao) 
	{
		this.acmHistoryDao = acmHistoryDao;
	}

	public CaseFileDao getCaseFileDao() {
		return caseFileDao;
	}

	public void setCaseFileDao(CaseFileDao caseFileDao) {
		this.caseFileDao = caseFileDao;
	}

	public ObjectAssociationDao getObjectAssociationDao() {
		return objectAssociationDao;
	}

	public void setObjectAssociationDao(ObjectAssociationDao objectAssociationDao) {
		this.objectAssociationDao = objectAssociationDao;
	}

	public PersonIdentificationDao getPersonIdentificationDao() {
		return personIdentificationDao;
	}

	public void setPersonIdentificationDao(
			PersonIdentificationDao personIdentificationDao) {
		this.personIdentificationDao = personIdentificationDao;
	}

	public FileWorkflowBusinessRule getFileWorkflowBusinessRule()
	{
		return fileWorkflowBusinessRule;
	}

	public void setFileWorkflowBusinessRule(FileWorkflowBusinessRule fileWorkflowBusinessRule)
	{
		this.fileWorkflowBusinessRule = fileWorkflowBusinessRule;
	}

	public RuntimeService getActivitiRuntimeService()
	{
		return activitiRuntimeService;
	}

	public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
	{
		this.activitiRuntimeService = activitiRuntimeService;
	}

	public CaseFile getCaseFile()
	{
		return caseFile;
	}

	public void setCaseFile(CaseFile caseFile)
	{
		this.caseFile = caseFile;
	}
}
