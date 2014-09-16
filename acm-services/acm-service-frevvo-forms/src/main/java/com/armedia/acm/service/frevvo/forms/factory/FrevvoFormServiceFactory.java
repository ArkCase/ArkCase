/**
 * 
 */
package com.armedia.acm.service.frevvo.forms.factory;

import com.armedia.acm.forms.roi.service.ROIService;
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
		
		if ("complaint".equals(name))
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
            
            return service;
		}
		
		if ("roi".equals(name))
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
		
		return null;
	}
	
}
