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
import com.armedia.acm.form.cost.model.CostFormConstants;
import com.armedia.acm.form.cost.model.CostItem;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.services.costsheet.dao.AcmCostsheetDao;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.users.model.AcmUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author riste.tutureski
 *
 */
public class CostService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
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
		// Unmarshall XML to object
		CostForm form = (CostForm) convertFromXMLToObject(cleanXML(xml), CostForm.class);
		
		if (form == null)
		{
			LOG.warn("Cannot unmarshall Time Form.");
			return false;
		}
		
		AcmCostsheet costsheet = getCostFactory().asAcmCostsheet(form);
		
		// If submission_name is "Save", save as draft (editable version), otherwise save for approval
		String submission_name = getRequest().getParameter("submission_name");
		if (submission_name != null && submission_name.equals(CostFormConstants.SAVE) && costsheet != null)
		{
			costsheet.setStatus(CostsheetConstants.DRAFT);
		}
		else if (submission_name != null && submission_name.equals(CostFormConstants.SUBMIT) && costsheet != null)
		{
			costsheet.setStatus(CostsheetConstants.IN_APPROVAL);
		}
		else
		{
			LOG.warn("Cannot save the costhseet. Maybe submission name is not provided or costsheet itself not contain required information.");
			return false;
		}
		
		AcmCostsheet saved = getAcmCostsheetDao().save(costsheet);
		
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
