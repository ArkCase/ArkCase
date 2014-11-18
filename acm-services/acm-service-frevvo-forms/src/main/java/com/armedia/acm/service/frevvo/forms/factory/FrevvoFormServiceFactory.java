/**
 * 
 */
package com.armedia.acm.service.frevvo.forms.factory;

import com.armedia.acm.form.closecase.service.CloseCaseService;
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
            service.setComplaintDao(frevvoFormController.getComplaintDao());
            service.setCaseFileDao(frevvoFormController.getCaseFileDao());
            
            return service;
		}
		
		if (FrevvoFormName.CLOSE_CASE.equals(name))
		{
			String contextPath = request.getServletContext().getContextPath();
			
			CloseCaseService service = new CloseCaseService();
			
			service.setEcmFileService(frevvoFormController.getEcmFileService());
            service.setServletContextPath(contextPath);
            service.setProperties(frevvoFormController.getProperties());
            service.setRequest(request);
            service.setAuthentication(authentication);
            service.setAuthenticationTokenService(frevvoFormController.getAuthenticationTokenService());
            service.setUserDao(frevvoFormController.getUserDao());
            service.setCaseFileDao(frevvoFormController.getCaseFileDao());
            service.setCloseCaseRequestDao(frevvoFormController.getCloseCaseRequestDao());
            
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
            service.setComplaintDao(frevvoFormController.getComplaintDao());
            service.setCaseFileDao(frevvoFormController.getCaseFileDao());
            service.setCloseComplaintRequestDao(frevvoFormController.getCloseComplaintRequestDao());
            service.setApplicationEventPublisher(frevvoFormController.getApplicationEventPublisher());
            service.setEcmFileDao(frevvoFormController.getEcmFileDao());
            service.setMuleClient(frevvoFormController.getMuleClient());
            
            return service;
		}
		
		return null;
	}
	
}
