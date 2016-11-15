/**
 * 
 */
package com.armedia.acm.service.frevvo.forms.factory;

import com.armedia.acm.form.casefile.service.CaseFilePSService;
import com.armedia.acm.form.changecasestatus.service.ChangeCaseStatusService;
import com.armedia.acm.form.ebrief.service.EbriefService;
import com.armedia.acm.form.electroniccommunication.service.ElectronicCommunicationService;
import com.armedia.acm.form.plain.service.PlainFormService;
import com.armedia.acm.form.plainconfiguration.service.PlainConfigurationFormService;
import com.armedia.acm.form.project.service.ProjectService;
import com.armedia.acm.form.time.service.TimeService;
import com.armedia.acm.forms.roi.service.ROIService;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormService;
import com.armedia.acm.service.frevvo.forms.web.api.FrevvoFormController;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * @author riste.tutureski
 *
 */
public class FrevvoFormServiceFactory
{
	private Map<String, FrevvoFormService> services;

	public FrevvoFormService getService(String name, FrevvoFormController frevvoFormController, HttpServletRequest request, Authentication authentication)
    {
		String plainForm = (String) request.getParameter("plainForm");

		if (FrevvoFormName.ROI.equals(name))
		{
			String contextPath = request.getServletContext().getContextPath();

			ROIService service = new ROIService();

			service.setEcmFileService(frevvoFormController.getEcmFileService());
			service.setServletContextPath(contextPath);
			service.setProperties(frevvoFormController.getProperties());
			service.setRequest(request);
			service.setAuthentication(authentication);
			service.setAuthenticationTokenService(frevvoFormController.getAuthenticationTokenService());
			service.setUserDao(frevvoFormController.getUserDao());
			service.setUserActionDao(frevvoFormController.getUserActionDao());
			service.setUserActionExecutor(frevvoFormController.getUserActionExecutor());
			service.setComplaintDao(frevvoFormController.getComplaintDao());
			service.setCaseFileDao(frevvoFormController.getCaseFileDao());
			service.setApplicationEventPublisher(frevvoFormController.getApplicationEventPublisher());
			service.setFolderAndFilesUtils(frevvoFormController.getFolderAndFilesUtils());
			service.setAcmFolderService(frevvoFormController.getAcmFolderService());

			return service;
		}

		if (FrevvoFormName.CHANGE_CASE_STATUS.equals(name))
		{
			String contextPath = request.getServletContext().getContextPath();

			ChangeCaseStatusService service = new ChangeCaseStatusService();

			service.setEcmFileService(frevvoFormController.getEcmFileService());
			service.setServletContextPath(contextPath);
			service.setProperties(frevvoFormController.getProperties());
			service.setRequest(request);
			service.setAuthentication(authentication);
			service.setAuthenticationTokenService(frevvoFormController.getAuthenticationTokenService());
			service.setUserDao(frevvoFormController.getUserDao());
			service.setUserActionDao(frevvoFormController.getUserActionDao());
			service.setUserActionExecutor(frevvoFormController.getUserActionExecutor());
			service.setCaseFileDao(frevvoFormController.getCaseFileDao());
			service.setChangeCaseStatusDao(frevvoFormController.getChangeCaseStatusDao());
			service.setApplicationEventPublisher(frevvoFormController.getApplicationEventPublisher());
			service.setEcmFileDao(frevvoFormController.getEcmFileDao());
			service.setMuleContextManager(frevvoFormController.getMuleContextManager());

			return service;
		}

		if (FrevvoFormName.CASE_FILE_PS.equals(name))
		{
			String contextPath = request.getServletContext().getContextPath();

			CaseFilePSService service = new CaseFilePSService();

			service.setEcmFileService(frevvoFormController.getEcmFileService());
			service.setServletContextPath(contextPath);
			service.setProperties(frevvoFormController.getProperties());
			service.setRequest(request);
			service.setAuthentication(authentication);
			service.setAuthenticationTokenService(frevvoFormController.getAuthenticationTokenService());
			service.setUserDao(frevvoFormController.getUserDao());
			service.setUserActionDao(frevvoFormController.getUserActionDao());
			service.setUserActionExecutor(frevvoFormController.getUserActionExecutor());
			service.setSaveCaseService(frevvoFormController.getSaveCaseService());
			service.setAcmHistoryDao(frevvoFormController.getAcmHistoryDao());
			service.setEcmFileDao(frevvoFormController.getEcmFileDao());
			service.setMuleContextManager(frevvoFormController.getMuleContextManager());
			service.setCaseFileDao(frevvoFormController.getCaseFileDao());
			service.setObjectAssociationDao(frevvoFormController.getObjectAssociationDao());
			service.setIdentificationDao(frevvoFormController.getIdentificationDao());
			service.setActivitiRuntimeService(frevvoFormController.getActivitiRuntimeService());
			service.setFileWorkflowBusinessRule(frevvoFormController.getFileWorkflowBusinessRule());
			service.setCaseFilePSFactory(frevvoFormController.getCaseFilePSFactory());

			return service;
		}

		if (FrevvoFormName.ELECTRONIC_COMMUNICATION.equals(name))
		{
			String contextPath = request.getServletContext().getContextPath();

			ElectronicCommunicationService service = new ElectronicCommunicationService();

			service.setEcmFileService(frevvoFormController.getEcmFileService());
			service.setServletContextPath(contextPath);
			service.setProperties(frevvoFormController.getProperties());
			service.setRequest(request);
			service.setAuthentication(authentication);
			service.setAuthenticationTokenService(frevvoFormController.getAuthenticationTokenService());
			service.setUserDao(frevvoFormController.getUserDao());
			service.setUserActionDao(frevvoFormController.getUserActionDao());
			service.setUserActionExecutor(frevvoFormController.getUserActionExecutor());
			service.setComplaintDao(frevvoFormController.getComplaintDao());
			service.setCaseFileDao(frevvoFormController.getCaseFileDao());
			service.setFolderAndFilesUtils(frevvoFormController.getFolderAndFilesUtils());
			service.setAcmFolderService(frevvoFormController.getAcmFolderService());

			return service;
		}

		if (FrevvoFormName.TIMESHEET.equals(name))
		{
			String contextPath = request.getServletContext().getContextPath();

			TimeService service = new TimeService();

			service.setEcmFileService(frevvoFormController.getEcmFileService());
			service.setServletContextPath(contextPath);
			service.setProperties(frevvoFormController.getProperties());
			service.setRequest(request);
			service.setAuthentication(authentication);
			service.setAuthenticationTokenService(frevvoFormController.getAuthenticationTokenService());
			service.setUserDao(frevvoFormController.getUserDao());
			service.setUserActionDao(frevvoFormController.getUserActionDao());
			service.setUserActionExecutor(frevvoFormController.getUserActionExecutor());
			service.setTimesheetService(frevvoFormController.getTimesheetService());
			service.setAcmTimesheetDao(frevvoFormController.getAcmTimesheetDao());
			service.setTimeFactory(frevvoFormController.getTimeFactory());
			service.setSearchResults(frevvoFormController.getSearchResults());
			service.setAcmPluginManager(frevvoFormController.getAcmPluginManager());
			service.setAcmContainerDao(frevvoFormController.getAcmContainerDao());
			service.setFolderAndFilesUtils(frevvoFormController.getFolderAndFilesUtils());
			service.setEcmFileDao(frevvoFormController.getEcmFileDao());
			service.setTimesheetEventPublisher(frevvoFormController.getTimesheetEventPublisher());

			return service;
		}

		if (FrevvoFormName.EBRIEF.equals(name))
		{
			String contextPath = request.getServletContext().getContextPath();

			EbriefService service = new EbriefService();

			service.setEcmFileService(frevvoFormController.getEcmFileService());
			service.setServletContextPath(contextPath);
			service.setProperties(frevvoFormController.getProperties());
			service.setRequest(request);
			service.setAuthentication(authentication);
			service.setAuthenticationTokenService(frevvoFormController.getAuthenticationTokenService());
			service.setUserDao(frevvoFormController.getUserDao());
			service.setUserActionDao(frevvoFormController.getUserActionDao());
			service.setUserActionExecutor(frevvoFormController.getUserActionExecutor());
			service.setSearchResults(frevvoFormController.getSearchResults());
			service.setAcmPluginManager(frevvoFormController.getAcmPluginManager());
			service.setEcmFileDao(frevvoFormController.getEcmFileDao());
			service.setSaveCaseService(frevvoFormController.getSaveCaseService());
			service.setCaseFileDao(frevvoFormController.getCaseFileDao());
			service.setEbriefFactory(frevvoFormController.getEbriefFactory());
			service.setFunctionalAccessService(frevvoFormController.getFunctionalAccessService());
			service.setActivitiRuntimeService(frevvoFormController.getActivitiRuntimeService());
			service.setFileWorkflowBusinessRule(frevvoFormController.getFileWorkflowBusinessRule());
			service.setNotificationDao(frevvoFormController.getNotificationDao());
			service.setNotificationEventPublisher(frevvoFormController.getNotificationEventPublisher());

			return service;
		}

		if (FrevvoFormName.PROJECT.equals(name))
		{
			String contextPath = request.getServletContext().getContextPath();

			ProjectService service = new ProjectService();

			service.setEcmFileService(frevvoFormController.getEcmFileService());
			service.setServletContextPath(contextPath);
			service.setProperties(frevvoFormController.getProperties());
			service.setRequest(request);
			service.setAuthentication(authentication);
			service.setAuthenticationTokenService(frevvoFormController.getAuthenticationTokenService());
			service.setUserDao(frevvoFormController.getUserDao());
			service.setUserActionDao(frevvoFormController.getUserActionDao());
			service.setUserActionExecutor(frevvoFormController.getUserActionExecutor());
			service.setSearchResults(frevvoFormController.getSearchResults());
			service.setAcmPluginManager(frevvoFormController.getAcmPluginManager());
			service.setEcmFileDao(frevvoFormController.getEcmFileDao());
			service.setSaveCaseService(frevvoFormController.getSaveCaseService());
			service.setCaseFileDao(frevvoFormController.getCaseFileDao());
			service.setProjectFactory(frevvoFormController.getProjectFactory());
			service.setFunctionalAccessService(frevvoFormController.getFunctionalAccessService());
			service.setActivitiRuntimeService(frevvoFormController.getActivitiRuntimeService());
			service.setFileWorkflowBusinessRule(frevvoFormController.getFileWorkflowBusinessRule());

			return service;
		}

		if (FrevvoFormName.PLAIN_CONFIGURATION.equals(name))
		{
			String contextPath = request.getServletContext().getContextPath();

			PlainConfigurationFormService service = new PlainConfigurationFormService();

			service.setEcmFileService(frevvoFormController.getEcmFileService());
			service.setServletContextPath(contextPath);
			service.setProperties(frevvoFormController.getProperties());
			service.setPlainFormPropertiesLocation(frevvoFormController.getPlainFormPropertiesLocation());
			service.setRequest(request);
			service.setAuthentication(authentication);
			service.setAuthenticationTokenService(frevvoFormController.getAuthenticationTokenService());
			service.setUserDao(frevvoFormController.getUserDao());
			service.setUserActionDao(frevvoFormController.getUserActionDao());
			service.setUserActionExecutor(frevvoFormController.getUserActionExecutor());
			service.setFrevvoService(frevvoFormController.getFrevvoService());
			service.setPlainConfigurationFormFactory(frevvoFormController.getPlainConfigurationFormFactory());
			service.setPropertyFileManager(frevvoFormController.getPropertyFileManager());

			return service;
		}


		return getService(name, plainForm, request, authentication);
	}

	private FrevvoFormService getService(String name, String plainForm, HttpServletRequest request, Authentication authentication)
	{
		String formName = name;
		if (plainForm != null && "true".equals(plainForm))
		{
			formName = "plain";
		}

		FrevvoFormService service = null;

		// TODO: So far, only CaseFileService, ComplaintService, CloseComplaintService, CostService, PlainFormService are re-written on this way.
		// TODO: On time, we should do for all other services
		if (getServices() != null && getServices().containsKey(formName))
		{

			service = getServices().get(formName);

			if (service != null)
			{
				String contextPath = request.getServletContext().getContextPath();

				if (service.getFormName() == null && "true".equals(plainForm))
				{
					service.setFormName(name);
				}

				service.setServletContextPath(contextPath);
				service.setRequest(request);
				service.setAuthentication(authentication);
			}
		}

		return service;
	}

	public Map<String, FrevvoFormService> getServices()
	{
		return services;
	}

	public void setServices(Map<String, FrevvoFormService> services)
	{
		this.services = services;
	}
	
}
