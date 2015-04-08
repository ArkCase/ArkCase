/**
 * 
 */
package com.armedia.acm.form.casefile.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;

import org.activiti.engine.RuntimeService;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.form.casefile.model.CaseFileForm;
import com.armedia.acm.form.casefile.model.CaseFileFormConstants;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.SaveCaseService;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.dao.PersonIdentificationDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.xml.InitiatorPerson;
import com.armedia.acm.plugins.person.model.xml.PeoplePerson;
import com.armedia.acm.service.history.dao.AcmHistoryDao;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserActionName;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private CaseFileFactory caseFileFactory;
	private SaveCaseService saveCaseService;
	private AcmHistoryDao acmHistoryDao;
	private CaseFileDao caseFileDao;
	private PersonIdentificationDao personIdentificationDao;
	private FileWorkflowBusinessRule fileWorkflowBusinessRule;

	private RuntimeService activitiRuntimeService;

	private CaseFile caseFile;
	
	private FunctionalAccessService functionalAccessService;

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
			
			if ("init-participants".equals(action)) 
			{
				result = initParticipants();
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
			LOG.warn("Cannot unmarshall Case Form.");
			return false;
		}
		
		// Save Case File to the database
		form = saveCaseFile(form);
		
		// Save Reference (Reinvestigation)
		form = saveReference(form);
		
		// Create Frevvo form from CaseFile
		form = getCaseFileFactory().asFrevvoCaseFile(getCaseFile(), this);
		
		updateXMLAttachment(attachments, FrevvoFormName.CASE_FILE, form);
		
		// Save Attachments
		FrevvoUploadedFiles frevvoFiles = saveAttachments(
                attachments,
                form.getCmisFolderId(),
                FrevvoFormName.CASE_FILE.toUpperCase(),
                form.getId());
		
		// Log the last user action
		if (null != form && null != form.getId())
		{
			getUserActionExecutor().execute(form.getId(), AcmUserActionName.LAST_CASE_CREATED, getAuthentication().getName());
		}
		
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
	
	private CaseFileForm saveCaseFile(CaseFileForm form) throws AcmCreateObjectFailedException 
	{
		LOG.info("Saving case file ...");
		
		CaseFile caseFile = null;
		
		// Edit mode
		String mode = getRequest().getParameter("mode");
		if (mode != null && "edit".equals(mode) && form.getId() != null)
		{
			caseFile = getCaseFileDao().find(form.getId());
		}
		
		caseFile = getCaseFileFactory().asAcmCaseFile(form, caseFile);
		HttpSession session = getRequest().getSession();
		String ipAddress = (String) session.getAttribute("acm_ip_address");
		
		// Save Case file
		try
        {
			caseFile = getSaveCaseService().saveCase(caseFile, getAuthentication(), ipAddress);
        }
		catch (MuleException | PersistenceException e)
        {
            throw new AcmCreateObjectFailedException("Case File", e.getMessage(), e);
        }
		
		// Add id's and other information to the Frevvo form
		form.setId(caseFile.getId());
		form.setCaseNumber(caseFile.getCaseNumber());
		
		setCaseFile(caseFile);
		
		return form;
	}
	
	public void updateXML(CaseFile caseFile, Authentication auth)
    {
    	if (caseFile != null)
    	{    		
    		CaseFileForm form = getCaseFileFactory().asFrevvoCaseFile(caseFile, this);
    		
    		if (form != null)
    		{
    			String xml = convertFromObjectToXML(form);
    			updateXML(xml, FrevvoFormName.CASE_FILE.toUpperCase(), caseFile.getId(), auth);		
    		}
    	}
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
		
		// Init Case File types
		caseFileForm.setCaseTypes(convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".types"), ","));
		
		// Init Initiator information		
		caseFileForm.setInitiator(initInitiator());
		
		// Init People information
		caseFileForm.setPeople(initPeople());
		
		JSONObject json = createResponse(caseFileForm);

		return json;
	}
	
	private InitiatorPerson initInitiator()
	{
		InitiatorPerson initiator = new InitiatorPerson();
		
		List<String> titles = convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".titles"), ",");
		initiator.setTitles(titles);
		initiator.setContactMethods(initContactMethods());
		initiator.setOrganizations(initOrganizations());
		initiator.setAddresses(initAddresses());
		initiator.setType(CaseFileFormConstants.PERSON_TYPE_INITIATOR);
		initiator.setTypes(convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".personTypes"), ","));
		
		return initiator;
	}
	
	private List<Person> initPeople()
	{
		List<Person> people = new ArrayList<>();
		
		PeoplePerson peoplePerson = new PeoplePerson();
		
		List<String> titles = convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".titles"), ",");
		peoplePerson.setTitles(titles);
		peoplePerson.setContactMethods(initContactMethods());
		peoplePerson.setOrganizations(initOrganizations());
		peoplePerson.setAddresses(initAddresses());
		
		List<String> types = convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".personTypes"), ",");
		
		// Remove "Initiator". It's first in the list
		if (types != null && types.size() > 0){
			types.remove(0);
		}
		
		peoplePerson.setTypes(types);
		
		people.add(peoplePerson);
		
		return people;
	}
	
	private JSONObject initParticipants()
	{
		CaseFileForm form = new CaseFileForm();
		
		// Init Participant types
		List<String> participantTypes = convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".participantTypes"), ",");
		form.setParticipantsTypeOptions(participantTypes);
		
		// Init Participants
		form.setParticipantsOptions(getParticipants(participantTypes, FrevvoFormName.CASE_FILE));
		
		JSONObject json = createResponse(form);
		
		return json;
	}
	
	private List<ContactMethod> initContactMethods()
	{		
		List<ContactMethod> contactMethods = new ArrayList<>();
		List<String> contactMethodTypes = convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".deviceTypes"), ",");
		
		ContactMethod contactMethod = new ContactMethod();
		
		contactMethod.setTypes(contactMethodTypes);
		contactMethod.setCreated(new Date());
		contactMethod.setCreator(getUserFullName());
		
		contactMethods.add(contactMethod);
		
		return contactMethods;
	}
	
	private List<Organization> initOrganizations()
	{
		List<Organization> organizations = new ArrayList<>();
		List<String> organizationsTypes = convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".organizationTypes"), ",");
		
		Organization organization = new Organization();
		
		organization.setOrganizationTypes(organizationsTypes);
		organization.setCreated(new Date());
		organization.setCreator(getUserFullName());
		
		organizations.add(organization);
		
		return organizations;
	}
	
	private List<PostalAddress> initAddresses()
	{
		List<PostalAddress> locations = new ArrayList<>();
		List<String> locationTypes = convertToList((String) getProperties().get(FrevvoFormName.CASE_FILE + ".locationTypes"), ",");
		
		PostalAddress location = new PostalAddress();
		
		location.setTypes(locationTypes);
		location.setCreated(new Date());
		location.setCreator(getUserFullName());
		
		locations.add(location);
		
		return locations;
	}
	
	private String getUserFullName()
	{
		String fullName = null;
		
		String userId = getAuthentication().getName();
		
		if (userId != null)
		{
			AcmUser user = getUserDao().findByUserId(userId);
			
			if (user != null)
			{
				return user.getFullName();
			}
		}
		
		return fullName;
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
			
			if (oldCaseId != null && oldCaseNumber != null && form.getId() != null && form.getCaseNumber() != null)
			{		
				String status = CaseFileFormConstants.STATUS_ACTIVE;
				CaseFile caseFile = getCaseFileDao().find(oldCaseId);
				if (caseFile != null && caseFile.getStatus() != null)
				{
					status = caseFile.getStatus();
				}
				
				ObjectAssociation objectAssociation = new ObjectAssociation();
				
				objectAssociation.setStatus(status);
				objectAssociation.setParentType(FrevvoFormName.CASE_FILE.toUpperCase());
				objectAssociation.setParentId(form.getId());
				objectAssociation.setParentName(form.getCaseNumber());
				objectAssociation.setTargetType(FrevvoFormName.CASE_FILE.toUpperCase());
				objectAssociation.setTargetId(oldCaseId);
				objectAssociation.setTargetName(oldCaseNumber);
				objectAssociation.setAssociationType(CaseFileFormConstants.ASSOCIATION_TYPE_REFERENCE);
				
				getObjectAssociationDao().save(objectAssociation);
			}
		}
		
		return form;
	}

	public CaseFileFactory getCaseFileFactory() {
		return caseFileFactory;
	}

	public void setCaseFileFactory(CaseFileFactory caseFileFactory) {
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

	public FunctionalAccessService getFunctionalAccessService() {
		return functionalAccessService;
	}

	public void setFunctionalAccessService(
			FunctionalAccessService functionalAccessService) {
		this.functionalAccessService = functionalAccessService;
	}
}
