
/**
 * 
 */
package com.armedia.acm.service.frevvo.forms.web.api;


import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.dao.ChangeCaseStatusDao;
import com.armedia.acm.plugins.casefile.service.SaveCaseService;
import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.service.SaveComplaintTransaction;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.dao.PersonIdentificationDao;

import org.activiti.engine.RuntimeService;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.armedia.acm.frevvo.config.FrevvoFormService;
import com.armedia.acm.service.frevvo.forms.factory.FrevvoFormServiceFactory;
import com.armedia.acm.service.history.dao.AcmHistoryDao;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.users.dao.ldap.UserActionDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.service.ldap.AcmUserActionExecutor;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping("/api/v1/forms/crud/acm")
public class FrevvoFormController implements ApplicationEventPublisherAware {
	private ApplicationEventPublisher applicationEventPublisher;
	
	private Logger LOG = LoggerFactory.getLogger(FrevvoFormController.class);
	
	private Map<String, Object> properties;
	private AuthenticationTokenService authenticationTokenService;
	private AcmPluginManager acmPluginManager;
	private UserDao userDao;
	private UserActionDao userActionDao;
	private ComplaintDao complaintDao;
	private CaseFileDao caseFileDao;
    private CloseComplaintRequestDao closeComplaintRequestDao;
    private ChangeCaseStatusDao changeCaseStatusDao;
    private PersonDao personDao;
    private EcmFileDao ecmFileDao;
    private AcmUserActionExecutor userActionExecutor;

    private SaveComplaintTransaction saveComplaintTransaction;
    private EcmFileService ecmFileService;
    private MuleClient muleClient;
    
    private SaveCaseService saveCaseService;
    
    private AcmHistoryDao acmHistoryDao;
    
    private ObjectAssociationDao objectAssociationDao;
    private PersonIdentificationDao personIdentificationDao;

	private FileWorkflowBusinessRule fileWorkflowBusinessRule;

	private RuntimeService activitiRuntimeService;
	
	@RequestMapping(value = "/{formName}/init")
    public void doInit(Authentication authentication, 
    		    		@PathVariable("formName") String formName,
    		    		HttpServletRequest request, HttpServletResponse response){
		
		LOG.info("Initialization form \"" + formName + "\"");
		
		// Create and initialize appropriate service for given form name
		FrevvoFormService frevvoFormService = FrevvoFormServiceFactory.getService(formName, this, request, authentication);
		
		// Initialize some data that should be shown on the form (if there should be any) - this is happen while form is loading for the first time
		String result = (String) frevvoFormService.init();	
		try{
			if (result != null){
				response.setContentType("text/xml");
				response.getOutputStream().write(result.getBytes(Charset.forName("UTF-8")));
				response.getOutputStream().flush();
			}else{
				LOG.warn("Empty response.");
			}
		}catch(Exception e){
			LOG.error("The output cannot be returned.", e);
		}
		
	}
	
	@RequestMapping(value = "/{formName}/get/{action}")
    public void doGet(Authentication authentication, 
    		    		@PathVariable("formName") String formName,
    		    		@PathVariable("action") String action,
    		    		HttpServletRequest request, HttpServletResponse response){
		
		LOG.info("Execute action \"" + action + "\" for form \"" + formName + "\"");

		// Create and initialize appropriate service for given form name
		FrevvoFormService frevvoFormService = FrevvoFormServiceFactory.getService(formName, this, request, authentication);

		// Initialize some data that should be shown on the form (if there should be any) - this is happening after form is loaded
		Object result = frevvoFormService.get(action);
		try{
			if (result != null){
				if (result instanceof String) {
					response.setContentType("text/xml");
					response.getOutputStream().write(((String) result).getBytes(Charset.forName("UTF-8")));
					response.getOutputStream().flush();			
				}else if (result instanceof JSONObject){
					response.addHeader("X-JSON", ((JSONObject) result).toString());
				}
			}else{
				LOG.warn("Empty response.");
			}
			
		}catch(Exception e){
			LOG.error("The output cannot be returned.", e);
		}
		
	}
	
	@RequestMapping(value = "/{formName}/save")
    public void doSave(Authentication authentication, 
    		    		@PathVariable("formName") String formName,
    		    		HttpServletRequest request,
						HttpServletResponse response,
						HttpSession session)
	{
		
		LOG.info("Save form \"" + formName + "\"");

		// Create and initialize appropriate service for given form name
		FrevvoFormService frevvoFormService = FrevvoFormServiceFactory.getService(formName, this, request, authentication);
		frevvoFormService.setUserIpAddress((String) session.getAttribute("acm_ip_address"));
		
		try{
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

			if (multipartRequest != null && multipartRequest.getFileMap() != null) {
				MultipartFile formDataFile = multipartRequest.getFileMap().get("form_" + formName);	
				if (formDataFile != null) {
					StringWriter writer = new StringWriter();
					IOUtils.copy(formDataFile.getInputStream(), writer, Charset.forName("UTF-8"));
					String xml = writer.toString();
					
					frevvoFormService.save(xml, multipartRequest.getMultiFileMap());
				}
			}	        
		}catch(Exception e){
			LOG.error("Cannot read request information.", e);
		}
	}
	
	public ApplicationEventPublisher getApplicationEventPublisher() {
		return applicationEventPublisher;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;		
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	/**
	 * @return the authenticationTokenService
	 */
	public AuthenticationTokenService getAuthenticationTokenService() {
		return authenticationTokenService;
	}

	/**
	 * @param authenticationTokenService the authenticationTokenService to set
	 */
	public void setAuthenticationTokenService(
			AuthenticationTokenService authenticationTokenService) {
		this.authenticationTokenService = authenticationTokenService;
	}

	/**
	 * @return the acmPluginManager
	 */
	public AcmPluginManager getAcmPluginManager() {
		return acmPluginManager;
	}

	/**
	 * @param acmPluginManager the acmPluginManager to set
	 */
	public void setAcmPluginManager(AcmPluginManager acmPluginManager) {
		this.acmPluginManager = acmPluginManager;
	}

	/**
	 * @return the userDao
	 */
	public UserDao getUserDao() {
		return userDao;
	}

	/**
	 * @param userDao the userDao to set
	 */
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

    /**
	 * @return the userActionDao
	 */
	public UserActionDao getUserActionDao() {
		return userActionDao;
	}

	/**
	 * @param userActionDao the userActionDao to set
	 */
	public void setUserActionDao(UserActionDao userActionDao) {
		this.userActionDao = userActionDao;
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

	public SaveComplaintTransaction getSaveComplaintTransaction()
    {
        return saveComplaintTransaction;
    }

    public void setSaveComplaintTransaction(SaveComplaintTransaction saveComplaintTransaction)
    {
        this.saveComplaintTransaction = saveComplaintTransaction;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public CloseComplaintRequestDao getCloseComplaintRequestDao()
    {
        return closeComplaintRequestDao;
    }

    public void setCloseComplaintRequestDao(CloseComplaintRequestDao closeComplaintRequestDao)
    {
        this.closeComplaintRequestDao = closeComplaintRequestDao;
    }

	public ChangeCaseStatusDao getChangeCaseStatusDao() {
		return changeCaseStatusDao;
	}

	public void setChangeCaseStatusDao(ChangeCaseStatusDao changeCaseStatusDao) {
		this.changeCaseStatusDao = changeCaseStatusDao;
	}

	public PersonDao getPersonDao() {
		return personDao;
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}

	public EcmFileDao getEcmFileDao() {
		return ecmFileDao;
	}
	
	public void setEcmFileDao(EcmFileDao ecmFileDao) {
		this.ecmFileDao = ecmFileDao;
	}

	public MuleClient getMuleClient() {
		return muleClient;
	}

	public void setMuleClient(MuleClient muleClient) {
		this.muleClient = muleClient;
	}

	public AcmUserActionExecutor getUserActionExecutor() {
		return userActionExecutor;
	}

	public void setUserActionExecutor(AcmUserActionExecutor userActionExecutor) {
		this.userActionExecutor = userActionExecutor;
	}

	public SaveCaseService getSaveCaseService() {
		return saveCaseService;
	}

	public void setSaveCaseService(SaveCaseService saveCaseService) {
		this.saveCaseService = saveCaseService;
	}

	public AcmHistoryDao getAcmHistoryDao() {
		return acmHistoryDao;
	}

	public void setAcmHistoryDao(AcmHistoryDao acmHistoryDao) {
		this.acmHistoryDao = acmHistoryDao;
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
}
