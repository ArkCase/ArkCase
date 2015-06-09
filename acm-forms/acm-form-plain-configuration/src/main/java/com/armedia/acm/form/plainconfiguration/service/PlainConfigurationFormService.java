/**
 * 
 */
package com.armedia.acm.form.plainconfiguration.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.form.plainconfiguration.model.PlainConfigurationForm;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoService;
import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.frevvo.forms.client.ApplicationEntry;
import com.frevvo.forms.client.FormTypeEntry;
import com.frevvo.forms.client.SchemaEntry;

/**
 * @author riste.tutureski
 *
 */
public class PlainConfigurationFormService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private String plainFormPropertiesLocation;
	private FrevvoService frevvoService;
	private PlainConfigurationFormFactory plainConfigurationFormFactory;
	private PropertyFileManager propertyFileManager;
	
	@Override
	public Object get(String action) 
	{
		Object result = null;
		
		if (action != null) 
		{
			if ("init-form-data".equals(action)) 
			{
				result = initFormData();
			}
			
			if ("init-targets".equals(action)) 
			{
				result = initTargets();
			}
			
			if ("get-form-info".equals(action)) 
			{
				result = getFormInfo();
			}
		}
		
		return result;
	}

	@Override
	public boolean save(String xml,
			MultiValueMap<String, MultipartFile> attachments) throws Exception 
	{
		PlainConfigurationForm form = (PlainConfigurationForm) convertFromXMLToObject(cleanXML(xml), PlainConfigurationForm.class);
		
		if (form == null) {
			LOG.warn("Cannot umarshall PlainConfiguration Form.");
			return false;
		}
		
		String formType = getPropertyFileManager().load(getPlainFormPropertiesLocation(), form.getKey() + ".type", null);
		String formParameters = getPropertyFileManager().load(getPlainFormPropertiesLocation(), form.getKey() + ".parameters." + form.getTarget(), null);
		
		if (formType == null || formParameters == null)
		{
			Map<String, String> properties = getFormProperties(form);
			getPropertyFileManager().storeMultiple(properties, getPlainFormPropertiesLocation(), false);
		}
		else
		{
			LOG.warn("The form" + form.getName()  + " for object type" + form.getTarget() + " is already registered.");
			return false;
		}

		return true;
	}

	@Override
	public String getFormName() 
	{
		return FrevvoFormName.PLAIN_CONFIGURATION;
	}

	@Override
	public Object convertToFrevvoForm(Object obj, Object form) {
		// Implementation no need so far
		return null;
	}
	
	private Object initFormData()
	{
		LOG.debug("Plain Configuration Form initialization.");
		PlainConfigurationForm form = new PlainConfigurationForm();
		
		LOG.debug("Start taking the application and forms from Frevvo.");
		getFrevvoService().login();
		ApplicationEntry application = getFrevvoService().getApplication("_IgbIMDnTEeSjTrBIsoKK0g");
		List<FormTypeEntry> forms = getFrevvoService().getForms(application);
		getFrevvoService().logout();
		
		form.setFormOptions(getKeyValuePairsForForms(forms));
		
		JSONObject json = createResponse(form);
		
		LOG.debug("Response: " + json);

		return json;
	}
	
	private Object initTargets()
	{
		LOG.debug("Targets initialization.");
		PlainConfigurationForm form = new PlainConfigurationForm();
		
		List<String> targets = convertToList((String) getProperties().get(FrevvoFormName.PLAIN_CONFIGURATION + ".targets"), ",");
		form.setTargetOptions(targets);
		
		JSONObject json = createResponse(form);
		
		LOG.debug("Response: " + json);

		return json;
	}
	
	private Object getFormInfo()
	{
		LOG.debug("Taking form information.");
		PlainConfigurationForm form = new PlainConfigurationForm();
		
		String formId = getRequest().getParameter("formId");
		
		FormTypeEntry formEntry = null;
		SchemaEntry schemaEntry = null;
		if (formId != null && !formId.isEmpty())
		{
			LOG.debug("Start taking the form information from Frevvo.");
			getFrevvoService().login();
			formEntry = getFrevvoService().getForm(formId);
			schemaEntry = getFrevvoService().getSchema(formId);
			getFrevvoService().logout();
		}
		
		if (formEntry != null && schemaEntry != null)
		{
			form = getPlainConfigurationFormFactory().convertFromFormTypeEntry(formEntry, schemaEntry);
		}
		
		JSONObject json = createResponse(form);
		
		LOG.debug("Response: " + json);

		return json;
	}
	
	private List<String> getKeyValuePairsForForms(List<FormTypeEntry> forms)
	{
		List<String> keyValuePairs = new ArrayList<String>();
		
		if (forms != null)
		{
			for (FormTypeEntry form : forms)
			{
				String keyValuePair = form.getId() + "=" + form.getTitle().getPlainText();
				keyValuePairs.add(keyValuePair);
			}
		}
		
		return keyValuePairs;
	}
	
	private Map<String, String> getFormProperties(PlainConfigurationForm form)
	{
		Map<String, String> properties = new HashMap<String, String>();
		String key = form.getKey();
		
		properties.put(key + ".name", form.getName());
		properties.put(key + ".type", form.getType());
		properties.put(key + ".mode", form.getMode());
		
		AcmMarshaller marshaller = ObjectConverter.createJSONMarshaller();
		String jsonParameters = marshaller.marshal(form.getUrlParameters());
		
		if (jsonParameters == null)
		{
			jsonParameters = "";
		}
		
		properties.put(key + ".parameters." + form.getTarget(), jsonParameters);
		
		return properties;
	}

	public FrevvoService getFrevvoService() {
		return frevvoService;
	}

	public void setFrevvoService(FrevvoService frevvoService) {
		this.frevvoService = frevvoService;
	}

	public PlainConfigurationFormFactory getPlainConfigurationFormFactory() {
		return plainConfigurationFormFactory;
	}

	public void setPlainConfigurationFormFactory(
			PlainConfigurationFormFactory plainConfigurationFormFactory) {
		this.plainConfigurationFormFactory = plainConfigurationFormFactory;
	}

	public String getPlainFormPropertiesLocation() {
		return plainFormPropertiesLocation;
	}

	public void setPlainFormPropertiesLocation(String plainFormPropertiesLocation) {
		this.plainFormPropertiesLocation = plainFormPropertiesLocation;
	}

	public PropertyFileManager getPropertyFileManager() {
		return propertyFileManager;
	}

	public void setPropertyFileManager(PropertyFileManager propertyFileManager) {
		this.propertyFileManager = propertyFileManager;
	}
}
