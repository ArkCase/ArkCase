/**
 * 
 */
package com.armedia.acm.service.frevvo.forms.factory;

import com.armedia.acm.frevvo.config.FrevvoFormService;
import com.armedia.acm.plugins.complaint.service.ComplaintService;
import com.armedia.acm.service.frevvo.forms.web.api.FrevvoFormController;

import javax.servlet.http.HttpServletRequest;


/**
 * @author riste.tutureski
 *
 */
public class FrevvoFormServiceFactory {

	public static FrevvoFormService getService(String name, FrevvoFormController frevvoFormController, HttpServletRequest request)
    {
		
		if ("complaint".equals(name))
        {
            String contextPath = request.getServletContext().getContextPath();

            ComplaintService service = new ComplaintService();

            service.setSaveComplaintTransaction(frevvoFormController.getSaveComplaintTransaction());
            service.setEcmFileService(frevvoFormController.getEcmFileService());
            service.setServletContextPath(contextPath);
            return service;
		}
		
		return null;
	}
	
}
