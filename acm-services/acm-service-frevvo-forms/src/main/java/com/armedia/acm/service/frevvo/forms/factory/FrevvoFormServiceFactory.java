/**
 * 
 */
package com.armedia.acm.service.frevvo.forms.factory;

import com.armedia.acm.frevvo.config.FrevvoFormService;
import com.armedia.acm.plugins.complaint.service.ComplaintService;
import com.armedia.acm.service.frevvo.forms.web.api.FrevvoFormController;


/**
 * @author riste.tutureski
 *
 */
public class FrevvoFormServiceFactory {

	public static FrevvoFormService getService(String name, FrevvoFormController frevvoFormController)
    {
		
		if ("complaint".equals(name))
        {
            ComplaintService service = new ComplaintService();
            service.setSaveComplaintTransaction(frevvoFormController.getSaveComplaintTransaction());
            return service;
		}
		
		return null;
	}
	
}
