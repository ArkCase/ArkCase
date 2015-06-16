/**
 * 
 */
package com.armedia.acm.form.plainconfiguration.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.form.plainconfiguration.model.PlainConfigurationForm;
import com.armedia.acm.form.plainconfiguration.model.xml.UrlParameterItem;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.frevvo.forms.client.FormTypeEntry;
import com.frevvo.forms.client.SchemaEntry;

/**
 * @author riste.tutureski
 *
 */
public class PlainConfigurationFormFactory {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private FrevvoService frevvoService;
	private Properties formProperties;
	private Properties plainFormProperties;
	
	public PlainConfigurationForm convertFromFormTypeEntry(FormTypeEntry formEntry, SchemaEntry schemaEntry)
	{
		PlainConfigurationForm form = new PlainConfigurationForm();
		
		try
		{
			if (formEntry != null && schemaEntry != null)
			{
				form.setKey(getFrevvoService().getFormKey(schemaEntry));
				form.setFormId(formEntry.getId());
				form.setName(formEntry.getTitle().getPlainText());
				form.setType(getFrevvoService().getFormType(formEntry));
				form.setUrl(getFrevvoService().getFormUrl().getNewFormUrl(form.getKey(), true));
			}
		}
		catch(Exception e)
		{
			LOG.error("Cannot convert Frevvo Form Entry to Plain Configuration form.", e);
		}
		
		return form;
	}
	
	public List<PlainConfigurationForm> convertFromProperties(List<String> targets)
	{
		List<PlainConfigurationForm> plainForms = new ArrayList<PlainConfigurationForm>();
		
		if (getFormProperties() != null && getPlainFormProperties() != null)
		{		
			if (targets == null)
			{
				targets = getTargets();
			}
			
			if (targets != null && targets.size() > 0)
			{
				for (Entry<Object, Object> entry : getPlainFormProperties().entrySet())
				{
					String key = (String) entry.getKey();
					
					if (key.endsWith(".id"))
					{
						String formKey = getFormKey(key);
						
						plainForms.addAll(getFormsForKeyAndTargets(formKey, targets));
					}
				}
			}
		}
		
		return plainForms;
	}
	
	public List<String> getTargets()
	{
		List<String> targets = new ArrayList<String>();
		
		String keyValuePairsTargets = getFormProperties().getProperty(FrevvoFormName.PLAIN_CONFIGURATION + ".targets", null);
		
		if (keyValuePairsTargets != null && !keyValuePairsTargets.isEmpty())
		{			
			try
			{
				String[] keyValuePairsTargetsArray = keyValuePairsTargets.split(",");
				if (keyValuePairsTargetsArray != null && keyValuePairsTargetsArray.length > 0)
				{
					for (int i = 0; i < keyValuePairsTargetsArray.length; i++) {
						String target = getFormTarget(keyValuePairsTargetsArray[i]);
						
						if (target != null)
						{
							targets.add(target);
						}
					}
				}
			}
			catch(Exception e)
			{
				LOG.error("Cannot create list of targets.", e);
			}
		}
		
		return targets;
	}
	
	private String getFormKey(String propertyKey)
	{
		if (propertyKey != null && !propertyKey.isEmpty())
		{
			String[] propertyKeyParts = propertyKey.split("\\.");
			
			if (propertyKeyParts != null && propertyKeyParts.length > 0)
			{
				return propertyKeyParts[0];
			}
		}
		
		return null;
	}
	
	private String getFormTarget(String target)
	{
		if (target != null && !target.isEmpty())
		{
			String[] targetParts = target.split("=");
			
			if (targetParts != null && targetParts.length > 0)
			{
				return targetParts[0];
			}
		}
		
		return null;
	}
	
	public List<PlainConfigurationForm> getFormsForKeyAndTargets(String formKey, List<String> targets)
	{
		List<PlainConfigurationForm> plainForms = new ArrayList<PlainConfigurationForm>();
		
		if (formKey != null && targets != null && targets.size() > 0)
		{		
			for (String target : targets)
			{				
				if (getPlainFormProperties().getProperty(formKey + ".parameters." + target) != null)
				{
					PlainConfigurationForm plainForm = getFormInfoFromProperties(formKey, target);
					plainForms.add(plainForm);
				}
			}
		}
		
		return plainForms;
	}
	
	public PlainConfigurationForm getFormInfoFromProperties(String formKey, String target)
	{
		PlainConfigurationForm form = new PlainConfigurationForm();
		
		form.setKey(formKey);
		form.setFormId(getPlainFormProperties().getProperty(formKey + ".id", null));
		form.setName(getPlainFormProperties().getProperty(formKey + ".name", null));
		form.setType(getPlainFormProperties().getProperty(formKey + ".type", null));
		form.setMode(getPlainFormProperties().getProperty(formKey + ".mode", null));
		form.setTarget(target);
		form.setDescription(getPlainFormProperties().getProperty(formKey + ".description." + target, null));
		form.setUrl(getFrevvoService().getFormUrl().getNewFormUrl(formKey, true));
		
		List<UrlParameterItem> urlParameters = null;
		
		String jsonParameters = getPlainFormProperties().getProperty(formKey + ".parameters." + target);
		if (jsonParameters != null && !jsonParameters.isEmpty())
		{
			try
			{
				ObjectMapper objectMapper = new ObjectMapper();
				urlParameters = objectMapper.readValue(jsonParameters, TypeFactory.defaultInstance().constructParametricType(List.class, UrlParameterItem.class));
			}
			catch (Exception e)
			{
				LOG.error("Cannot parse JSON=" + jsonParameters, e);
			}
		}
		
		form.setUrlParameters(urlParameters);
		
		return form;
	}

	public FrevvoService getFrevvoService() {
		return frevvoService;
	}

	public void setFrevvoService(FrevvoService frevvoService) {
		this.frevvoService = frevvoService;
	}

	public Properties getFormProperties() {
		return formProperties;
	}

	public void setFormProperties(Properties formProperties) {
		this.formProperties = formProperties;
	}

	public Properties getPlainFormProperties() {
		return plainFormProperties;
	}

	public void setPlainFormProperties(Properties plainFormProperties) {
		this.plainFormProperties = plainFormProperties;
	}
	
}
