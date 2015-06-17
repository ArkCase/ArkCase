/**
 * 
 */
package com.armedia.acm.plugins.admin.web.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.form.plainconfiguration.model.PlainConfigurationForm;
import com.armedia.acm.form.plainconfiguration.service.PlainConfigurationFormFactory;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class GetPlainFormsAPIController {
	
	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private PlainConfigurationFormFactory plainConfigurationFormFactory;
	
	@RequestMapping(value="/plainforms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<PlainConfigurationForm> getAllPlainForms(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
				              @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,			
				              @RequestParam(value = "s", required = false, defaultValue = "") String sort,
    						  Authentication auth,
    						  HttpSession httpSession) throws Exception
    {
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Taking all plain forms.");
		}
		
		List<PlainConfigurationForm> plainForms = getPlainConfigurationFormFactory().convertFromProperties(null);
		
		if (plainForms != null)
		{
			try
			{
				// TODO: Finish paging and sorting ... add Comparator to the stream ...
				// For now avoid paging. UI should first support paging. After that, remove this line below
				maxRows = plainForms.size();
				return plainForms.stream()
				                 .skip(startRow)
				                 .limit(maxRows)
				                 .collect(Collectors.toList());
			}
			catch (Exception e)
			{
				LOG.error("Cannot return requested page: start=" + startRow + ", n=" + maxRows, e);
			}
		}
		
		return new ArrayList<PlainConfigurationForm>();
    }
	
	@RequestMapping(value="/plainforms/{target}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<PlainConfigurationForm> getPlainFormsForTarget(@PathVariable("target") String target,
    						  Authentication auth,
    						  HttpSession httpSession) throws Exception
    {
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Taking all plain forms for target=" + target);
		}
		
		List<PlainConfigurationForm> plainForms = getPlainConfigurationFormFactory().convertFromProperties(Arrays.asList(target));
		
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
