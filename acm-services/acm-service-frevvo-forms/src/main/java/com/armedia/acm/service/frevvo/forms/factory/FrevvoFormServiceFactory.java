/**
 * 
 */
package com.armedia.acm.service.frevvo.forms.factory;

import com.armedia.acm.form.casefile.service.CaseFileService;
import com.armedia.acm.form.changecasestatus.service.ChangeCaseStatusService;
import com.armedia.acm.form.closecomplaint.service.CloseComplaintService;
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
            
            return service;
		}
		
		return null;
	}
	
}
