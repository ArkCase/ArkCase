/**
 * 
 */
package com.armedia.acm.plugins.admin.web.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.form.plainconfiguration.model.PlainConfigurationForm;
import com.armedia.acm.form.plainconfiguration.service.PlainConfigurationFormFactory;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class DeletePlainFormAPIController {
	
	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private PlainConfigurationFormFactory plainConfigurationFormFactory;
	private PropertyFileManager propertyFileManager;
	private String plainFormPropertiesLocation;
	
	@RequestMapping(value="/plainforms/{key}/{target}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PlainConfigurationForm deletePlainForm(@PathVariable("key") String key, @PathVariable("target") String target,
    						  Authentication auth,
    						  HttpSession httpSession) throws Exception
    {
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Remove plain form for key=" + key + " and target=" + target);
		}
		
		PlainConfigurationForm form = new PlainConfigurationForm();
		
		if (key != null && target != null)
		{
			String parameterKey = key + ".parameters." + target;
			if (getPlainConfigurationFormFactory().getPlainFormProperties().getProperty(parameterKey) != null)
			{
				form = getPlainConfigurationFormFactory().getFormInfoFromProperties(key, target);
				
				if (form.getType() != null && !form.getType().isEmpty())
				{
					LOG.debug("Removing form type = " + form.getType());
					getPropertyFileManager().removeMultiple(Arrays.asList(parameterKey), getPlainFormPropertiesLocation());
				}
			}
			
			// Check if there are are left plain forms for other targets. If no, remove general form information for all targets
			List<String> targets = getPlainConfigurationFormFactory().getTargets();
			List<PlainConfigurationForm> forms = getPlainConfigurationFormFactory().getFormsForKeyAndTargets(key, targets);
			
			if (forms == null || forms.isEmpty())
			{
				LOG.debug("Removing generic plain form information for all targets and key=" + key);
				getPropertyFileManager().removeMultiple(getKeys(key), getPlainFormPropertiesLocation());
			}
		}
		
		return form;
    }
	
	private List<String> getKeys(String formKey)
	{
		List<String> keys = new ArrayList<String>();
		
		keys.add(formKey + ".id");
		keys.add(formKey + ".name");
		keys.add(formKey + ".type");
		keys.add(formKey + ".mode");
		
		return keys;
	}

	public PlainConfigurationFormFactory getPlainConfigurationFormFactory() {
		return plainConfigurationFormFactory;
	}

	public void setPlainConfigurationFormFactory(
			PlainConfigurationFormFactory plainConfigurationFormFactory) {
		this.plainConfigurationFormFactory = plainConfigurationFormFactory;
	}

	public PropertyFileManager getPropertyFileManager() {
		return propertyFileManager;
	}

	public void setPropertyFileManager(PropertyFileManager propertyFileManager) {
		this.propertyFileManager = propertyFileManager;
	}

	public String getPlainFormPropertiesLocation() {
		return plainFormPropertiesLocation;
	}

	public void setPlainFormPropertiesLocation(String plainFormPropertiesLocation) {
		this.plainFormPropertiesLocation = plainFormPropertiesLocation;
	}

}
