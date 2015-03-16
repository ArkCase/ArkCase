/**
 * 
 */
package com.armedia.acm.form.cost.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.form.cost.model.CostForm;
import com.armedia.acm.form.cost.model.CostItem;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.services.costsheet.dao.AcmCostsheetDao;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.service.CostsheetService;
import com.armedia.acm.services.users.model.AcmUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author riste.tutureski
 *
 */
public class CostService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private CostsheetService costsheetService;
	private AcmCostsheetDao acmCostsheetDao;
	private CostFactory costFactory;
	
	@Override
	public Object init() 
	{
		Object result = "";
		
		String objectId = getRequest().getParameter("objectId");	
		String objectType = getRequest().getParameter("objectType");
		String userId = getAuthentication().getName();
		
		CostForm form = new CostForm();
		AcmCostsheet costsheet = null;
			
		if (objectId != null && !"".equals(objectId))
		{			
			try
			{
				Long objectIdLong = Long.parseLong(objectId);
				costsheet = getAcmCostsheetDao().findByUserIdAndObjectId(userId, objectIdLong);
				form.setObjectId(objectIdLong);
			}
			catch(Exception e)
			{
				LOG.error("Cannot parse " + objectId + " to Long type. Empty form will be created.", e);
			}			
		}
		
		if (costsheet != null)
		{
			form = getCostFactory().asFrevvoCostForm(costsheet);
		}
		else
		{
			form.setItems(Arrays.asList(new CostItem()));
		}
		
		form.setObjectType(objectType);
		form.setUser(userId);
		form.setBalanceTable(Arrays.asList(new String()));
		
		// Back initDate in the form. It will need Frevvo engine to recalculate init values after calling this method
		JSONObject initData = (JSONObject) initFormData();
		form.setInitData(initData.toString());
		
		result = convertFromObjectToXML(form);
		
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
		}

		return result;
	}

	@Override
	public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception 
	{
		// Get submission name - Save or Submit
		String submissionName = getRequest().getParameter("submission_name");
		
		// Unmarshall XML to object
		CostForm form = (CostForm) convertFromXMLToObject(cleanXML(xml), CostForm.class);
		
		if (form == null)
		{
			LOG.warn("Cannot unmarshall Time Form.");
			return false;
		}
		
		AcmCostsheet costsheet = getCostFactory().asAcmCostsheet(form);		
		AcmCostsheet saved = getCostsheetService().save(costsheet, submissionName);
		
		form = getCostFactory().asFrevvoCostForm(saved);
		
		// TODO: Add logic for approval workflow and save attachments
		
		return true;
	}
	
	public Object initFormData()
	{
		String userId = getAuthentication().getName();
        AcmUser user = getUserDao().findByUserId(userId);
		
		CostForm form = new CostForm();
		
		// Set user
		form.setUser(userId);
		form.setUserOptions(Arrays.asList(userId + "=" + user.getFullName()));
		
		// Init Types
		List<String> types = convertToList((String) getProperties().get(FrevvoFormName.COST + ".types"), ",");
		form.setObjectTypeOptions(types);
		
		// Init Statuses
		form.setStatusOptions(convertToList((String) getProperties().get(FrevvoFormName.COST + ".statuses"), ","));
		
		// Init Titles
		CostItem item = new CostItem();
		item.setTitleOptions(convertToList((String) getProperties().get(FrevvoFormName.COST + ".titles"), ","));
		form.setItems(Arrays.asList(item));
		
		// Set charge codes for each type
		Map<String, List<String>> codeOptions = getCodeOptions(types);
		form.setCodeOptions(codeOptions);
		
		// Create JSON and back to the Frevvo form
		Gson gson = new GsonBuilder().setDateFormat(DateFormats.FREVVO_DATE_FORMAT).create();
		String jsonString = gson.toJson(form);
		
		JSONObject json = new JSONObject(jsonString);

		return json;
	}
	
	private Map<String, List<String>> getCodeOptions(List<String> types)
	{
		Map<String, List<String>> codeOptions = new HashMap<String, List<String>>();
		
		if (types != null)
		{
			for (String type : types)
			{
				String[] typeArray = type.split("=");
				if (typeArray != null && typeArray.length == 2)
				{
					List<String> options = getCodeOptionsByObjectType(typeArray[0]);
					codeOptions.put(typeArray[0], options);
				}
			}
		}
		
		return codeOptions;
	}
	
	private List<String> getCodeOptionsByObjectType(String objectType)
	{
		List<String> codeOptions = new ArrayList<>();
		
		JSONObject jsonObject = getCostsheetService().getObjectsFromSolr(objectType, getAuthentication(), 0, 50, "name ASC");
		if (jsonObject != null && jsonObject.has("response") && jsonObject.getJSONObject("response").has("docs"))
		{
			JSONArray objects = jsonObject.getJSONObject("response").getJSONArray("docs");
			
			for (int i = 0; i < objects.length(); i++)
			{
				JSONObject object = objects.getJSONObject(i);
				
				if (object.has("name"))
				{
					codeOptions.add(object.getString("object_id_s") + "=" + object.getString("name"));
				}
			}
		}
		
		return codeOptions;
	}

	@Override
	public String getFormName() 
	{
		return FrevvoFormName.COST;
	}

	public CostsheetService getCostsheetService() {
		return costsheetService;
	}

	public void setCostsheetService(CostsheetService costsheetService) {
		this.costsheetService = costsheetService;
	}

	public AcmCostsheetDao getAcmCostsheetDao() {
		return acmCostsheetDao;
	}

	public void setAcmCostsheetDao(AcmCostsheetDao acmCostsheetDao) {
		this.acmCostsheetDao = acmCostsheetDao;
	}

	public CostFactory getCostFactory() {
		return costFactory;
	}

	public void setCostFactory(CostFactory costFactory) {
		this.costFactory = costFactory;
	}

}
