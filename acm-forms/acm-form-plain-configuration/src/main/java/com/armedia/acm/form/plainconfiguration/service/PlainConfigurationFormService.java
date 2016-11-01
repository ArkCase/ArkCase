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
	public Object init() 
	{
		String result = "";
		
		String mode = getRequest().getParameter("mode");
		String key = getRequest().getParameter("formKey");
		String target = getRequest().getParameter("formTarget");
		
		if (mode != null && "edit".equals(mode) && 
			key != null && !key.isEmpty() && 
			target != null && !target.isEmpty())
		{
			PlainConfigurationForm form = getPlainConfigurationFormFactory().getFormInfoFromProperties(key, target);
			if (form != null)
			{
				result = convertFromObjectToXML(form);
			}
		}
		
		return result;
	}
	
	
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
			
			if ("init-object-properties".equals(action)) 
			{
				result = initObjectProperties();
			}
			
			if ("init-required-url-parameters".equals(action)) 
			{
				result = initRequiredUrlParameters();
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
		String mode = getRequest().getParameter("mode");
		
		PlainConfigurationForm form = (PlainConfigurationForm) convertFromXMLToObject(cleanXML(xml), PlainConfigurationForm.class);
		
		if (form == null) {
			LOG.warn("Cannot umarshall PlainConfiguration Form.");
			return false;
		}
		
		String formId = getPropertyFileManager().load(getPlainFormPropertiesLocation(), form.getFormId() + ".id", null);
		String formParameters = getPropertyFileManager().load(getPlainFormPropertiesLocation(), form.getKey() + ".parameters." + form.getTarget(), null);
		
		if (formId == null || formParameters == null || "edit".equals(mode))
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
	public Class<?> getFormClass()
	{
		return PlainConfigurationForm.class;
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
		List<String> applicationIds = getFrevvoService().getFormUrl().getPlainFormApplicationIds();
		final List<FormTypeEntry> forms = new ArrayList<>();

		if (applicationIds != null && !applicationIds.isEmpty())
		{
			getFrevvoService().login();
			applicationIds.stream().forEach(applicationId -> {
				ApplicationEntry application = getFrevvoService().getApplication(applicationId);
				List<FormTypeEntry> _forms = getFrevvoService().getPlainForms(application);
				if (_forms != null)
				{
					forms.addAll(_forms);
				}
			});
			getFrevvoService().logout();
		}
		
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
	
	private Object initObjectProperties()
	{
		LOG.debug("Object Properties initialization.");
		String target = getRequest().getParameter("target");
		
		PlainConfigurationForm form = new PlainConfigurationForm();
		if (target != null && !target.isEmpty())
		{
			LOG.debug("Object Properties initialization for target=" + target);
			List<String> objectProperties = convertToList((String) getProperties().get(FrevvoFormName.PLAIN_CONFIGURATION + "." + target + ".properties"), ",");
			form.setObjectPropertiesOptions(objectProperties);
		}
		else 
		{
			LOG.debug("Cannot initialize object properties. No target specified.");
		}
		
		JSONObject json = createResponse(form);
		
		LOG.debug("Response: " + json);

		return json;
	}
	
	private Object initRequiredUrlParameters()
	{
		LOG.debug("Require URL Parameters initialization.");
		PlainConfigurationForm form = new PlainConfigurationForm();
		
		List<String> requiredUrlParemeters = convertToList((String) getProperties().get(FrevvoFormName.PLAIN_CONFIGURATION + ".required.url.parameters"), ",");
		form.setRequiredUrlParemeters(requiredUrlParemeters);
		
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
		ApplicationEntry applicationEntry = null;
		if (formId != null && !formId.isEmpty())
		{
			LOG.debug("Start taking the form information from Frevvo.");
			getFrevvoService().login();
			formEntry = getFrevvoService().getForm(formId);
			schemaEntry = getFrevvoService().getSchema(formId);
			applicationEntry = getFrevvoService().getApplication(getFrevvoService().getFormApplicationId(formEntry));
			getFrevvoService().logout();
		}
		
		if (formEntry != null && schemaEntry != null && applicationEntry != null)
		{
			form = getPlainConfigurationFormFactory().convertFromFormTypeEntry(formEntry, schemaEntry, applicationEntry);
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
			getFrevvoService().login();
			for (FormTypeEntry form : forms)
			{
				String mode = getRequest().getParameter("mode");
				String target = getRequest().getParameter("target");
				PlainConfigurationForm registeredForm = null;
				
				if (!"edit".equals(mode))
				{
					registeredForm = getRegisteredForm(form.getId(), target);
				}
				
				if (registeredForm == null)
				{
					String applicationName = "";
					ApplicationEntry applicationEntry = getFrevvoService().getApplication(getFrevvoService().getFormApplicationId(form));

					if (applicationEntry != null)
					{
						applicationName = " (" + applicationEntry.getTitle().getPlainText() + ")";
					}

					String keyValuePair = form.getId() + "=" + form.getTitle().getPlainText() + applicationName;
					keyValuePairs.add(keyValuePair);
				}
			}
			getFrevvoService().logout();
		}
		
		return keyValuePairs;
	}
	
	private PlainConfigurationForm getRegisteredForm(String id, String target)
	{
		List<PlainConfigurationForm> registeredForms = getPlainConfigurationFormFactory().convertFromProperties(null);
		PlainConfigurationForm registeredForm = null;
		
		if (registeredForms != null && id != null && target != null)
		{
			try
			{
				registeredForm = registeredForms.stream()
											    .filter(element -> id.equals(element.getFormId()) && target.equals(element.getTarget()))
											    .findFirst()
											    .get();
			}
			catch(Exception e)
			{
				LOG.debug("The form with id=" + id + " is not registered in the system.");
			}
		}
		
		return registeredForm;
	}
	
	private Map<String, String> getFormProperties(PlainConfigurationForm form)
	{
		Map<String, String> properties = new HashMap<String, String>();
		String key = form.getKey();
		
		properties.put(key + ".id", form.getFormId() == null ? "" : form.getFormId());
		properties.put(key + ".name", form.getName() == null ? "" : form.getName());
		properties.put(key + ".type", form.getType() == null ? "" : form.getType());
		properties.put(key + ".application.id", form.getApplicationId() == null ? "" : form.getApplicationId());
		properties.put(key + ".application.name", form.getApplicationName() == null ? "" : form.getApplicationName());
		properties.put(key + ".mode", form.getMode() == null ? "" : form.getMode());
		properties.put(key + ".description." + form.getTarget(), form.getDescription() == null ? "" : form.getDescription());
		
		AcmMarshaller marshaller = ObjectConverter.createJSONMarshaller();
		String jsonParameters = marshaller.marshal(form.getUrlParameters());
		
		properties.put(key + ".parameters." + form.getTarget(), jsonParameters == null ? "" : jsonParameters);
		
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
