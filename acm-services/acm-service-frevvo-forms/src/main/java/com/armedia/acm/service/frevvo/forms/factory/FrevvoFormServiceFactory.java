/**
 * 
 */
package com.armedia.acm.service.frevvo.forms.factory;

import com.armedia.acm.form.casefile.service.CaseFilePSService;
import com.armedia.acm.form.casefile.service.CaseFileService;
import com.armedia.acm.form.changecasestatus.service.ChangeCaseStatusService;
import com.armedia.acm.form.closecomplaint.service.CloseComplaintService;
import com.armedia.acm.form.cost.service.CostService;
import com.armedia.acm.form.electroniccommunication.service.ElectronicCommunicationService;
import com.armedia.acm.form.time.service.TimeService;
import com.armedia.acm.forms.roi.service.ROIService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormService;
import com.armedia.acm.plugins.complaint.service.ComplaintService;
import com.armedia.acm.service.frevvo.forms.web.api.FrevvoFormController;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;


/**
 * @author riste.tutureski
 *
 */
public class FrevvoFormServiceFactory {

	public static FrevvoFormService getService(String name, FrevvoFormController frevvoFormController, HttpServletRequest request, Authentication authentication)
    {
		
		if (FrevvoFormName.COMPLAINT.equals(name))
        {
            String contextPath = request.getServletContext().getContextPath();

            ComplaintService service = new ComplaintService();

            service.setSaveComplaintTransaction(frevvoFormController.getSaveComplaintTransaction());
            service.setEcmFileService(frevvoFormController.getEcmFileService());
            service.setServletContextPath(contextPath);
            service.setProperties(frevvoFormController.getProperties());
            service.setRequest(request);
            service.setAuthentication(authentication);
            service.setAuthenticationTokenService(frevvoFormController.getAuthenticationTokenService());
            service.setUserDao(frevvoFormController.getUserDao());
            service.setUserActionDao(frevvoFormController.getUserActionDao());
            service.setUserActionExecutor(frevvoFormController.getUserActionExecutor());
            service.setAcmPluginManager(frevvoFormController.getAcmPluginManager());
            service.setPersonDao(frevvoFormController.getPersonDao());
            service.setComplaintEventPublisher(frevvoFormController.getComplaintEventPublisher());
            
            return service;
		}
		
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
            service.setMuleClient(frevvoFormController.getMuleClient());
            
            return service;
		}
		
		if (FrevvoFormName.CLOSE_COMPLAINT.equals(name))
		{
			String contextPath = request.getServletContext().getContextPath();
			
			CloseComplaintService service = new CloseComplaintService();
			
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
            service.setCloseComplaintRequestDao(frevvoFormController.getCloseComplaintRequestDao());
            service.setApplicationEventPublisher(frevvoFormController.getApplicationEventPublisher());
            service.setEcmFileDao(frevvoFormController.getEcmFileDao());
            service.setMuleClient(frevvoFormController.getMuleClient());
            
            return service;
		}
		
		if (FrevvoFormName.CASE_FILE.equals(name))
        {
            String contextPath = request.getServletContext().getContextPath();

            CaseFileService service = new CaseFileService();

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
            service.setMuleClient(frevvoFormController.getMuleClient());
            service.setCaseFileDao(frevvoFormController.getCaseFileDao());
            service.setObjectAssociationDao(frevvoFormController.getObjectAssociationDao());
            service.setPersonIdentificationDao(frevvoFormController.getPersonIdentificationDao());
            service.setActivitiRuntimeService(frevvoFormController.getActivitiRuntimeService());
            service.setFileWorkflowBusinessRule(frevvoFormController.getFileWorkflowBusinessRule());
            service.setCaseFileFactory(frevvoFormController.getCaseFileFactory());
            service.setAcmPluginManager(frevvoFormController.getAcmPluginManager());
            
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
            service.setMuleClient(frevvoFormController.getMuleClient());
            service.setCaseFileDao(frevvoFormController.getCaseFileDao());
            service.setObjectAssociationDao(frevvoFormController.getObjectAssociationDao());
            service.setPersonIdentificationDao(frevvoFormController.getPersonIdentificationDao());
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
            service.setEcmFileDao(frevvoFormController.getEcmFileDao());
            service.setTimesheetEventPublisher(frevvoFormController.getTimesheetEventPublisher());
            
            return service;
		}
		
		if (FrevvoFormName.COSTSHEET.equals(name))
        {
            String contextPath = request.getServletContext().getContextPath();

            CostService service = new CostService();

            service.setEcmFileService(frevvoFormController.getEcmFileService());
            service.setServletContextPath(contextPath);
            service.setProperties(frevvoFormController.getProperties());
            service.setRequest(request);
            service.setAuthentication(authentication);
            service.setAuthenticationTokenService(frevvoFormController.getAuthenticationTokenService());
            service.setUserDao(frevvoFormController.getUserDao());
            service.setUserActionDao(frevvoFormController.getUserActionDao());
            service.setUserActionExecutor(frevvoFormController.getUserActionExecutor());
            service.setCostsheetService(frevvoFormController.getCostsheetService());
            service.setAcmCostsheetDao(frevvoFormController.getAcmCostsheetDao());
            service.setCostFactory(frevvoFormController.getCostFactory());
            service.setSearchResults(frevvoFormController.getSearchResults());
            service.setAcmPluginManager(frevvoFormController.getAcmPluginManager());
            service.setAcmContainerDao(frevvoFormController.getAcmContainerDao());
            service.setEcmFileDao(frevvoFormController.getEcmFileDao());
            service.setCostsheetEventPublisher(frevvoFormController.getCostsheetEventPublisher());
            
            return service;
		}
		
		return null;
	}
	
}
