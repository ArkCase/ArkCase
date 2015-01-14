package com.armedia.acm.services.functionalaccess.web.api;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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
public class SaveApplicationRolesToGroupsAPIController {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private FunctionalAccessService functionalAccessService;
	
	@RequestMapping(value="/rolestogroups/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean saveApplicationRolesToGroups(@RequestBody Map<String, List<String>> applicationRolesToGroups, Authentication auth)
    {
		LOG.info("Saving application roles to groups ...");
		
		boolean retval = getFunctionalAccessService().saveApplicationRolesToGroups(applicationRolesToGroups);
		LOG.info("Successfuly save ? " + retval);
		
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
