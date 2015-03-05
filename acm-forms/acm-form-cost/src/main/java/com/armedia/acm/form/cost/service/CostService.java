/**
 * 
 */
package com.armedia.acm.form.cost.service;

import java.util.Arrays;

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
		
		// TODO: Add logic for approval workflow and edit form
		
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
		
		// Init Statuses
		form.setStatusOptions(convertToList((String) getProperties().get(FrevvoFormName.COST + ".statuses"), ","));
		
		// Init Titles
		CostItem item = new CostItem();
		item.setTitleOptions(convertToList((String) getProperties().get(FrevvoFormName.COST + ".titles"), ","));
		form.setItems(Arrays.asList(item));
		
		// Create JSON and back to the Frevvo form
		Gson gson = new GsonBuilder().setDateFormat(DateFormats.FREVVO_DATE_FORMAT).create();
		String jsonString = gson.toJson(form);
		
		JSONObject json = new JSONObject(jsonString);

		return json;
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
