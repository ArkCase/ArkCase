package com.armedia.acm.services.functionalaccess.web.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/functionalaccess", "/api/latest/functionalaccess" })
public class GetApplicationRolesAPIController {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private FunctionalAccessService functionalAccessService;
	
	@RequestMapping(value="/roles/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> getApplicationRoles(Authentication auth)
    {
		LOG.info("Taking application roles ...");
		
		List<String> retval = getFunctionalAccessService().getApplicationRoles();
		LOG.info("Application roles: " + retval.toString());
		
		return retval;
    }

	public FunctionalAccessService getFunctionalAccessService() 
	{
		return functionalAccessService;
	}

	public void setFunctionalAccessService(FunctionalAccessService functionalAccessService) 
	{
		this.functionalAccessService = functionalAccessService;
	}
}
