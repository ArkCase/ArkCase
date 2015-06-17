/**
 * 
 */
package com.armedia.acm.plugins.admin.web.api;


import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.form.plainconfiguration.service.PlainConfigurationFormFactory;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class GetPlainFormTargetsAPIController {
	
	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private PlainConfigurationFormFactory plainConfigurationFormFactory;
	
	@RequestMapping(value="/plainform/targets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String[] getAllPlainFormTargets(Authentication auth,
    						  HttpSession httpSession) throws Exception
    {
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Taking all plain form targets.");
		}
		
		String[] plainForms = getPlainConfigurationFormFactory().getKeyValueTargets();
	
		return plainForms;
    }
	
	public PlainConfigurationFormFactory getPlainConfigurationFormFactory() {
		return plainConfigurationFormFactory;
	}

	public void setPlainConfigurationFormFactory(
			PlainConfigurationFormFactory plainConfigurationFormFactory) {
		this.plainConfigurationFormFactory = plainConfigurationFormFactory;
	}

}
