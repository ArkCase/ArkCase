/**
 * 
 */
package com.armedia.acm.form.time.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.form.time.model.TimeForm;
import com.armedia.acm.form.time.model.TimeFormConstants;
import com.armedia.acm.form.time.model.TimeItem;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.service.TimesheetService;
import com.armedia.acm.services.users.model.AcmUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author riste.tutureski
 *
 */
public class TimeService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private TimesheetService timesheetService;
	private AcmTimesheetDao acmTimesheetDao;
	private TimeFactory timeFactory;
	
	@Override
	public Object init() 
	{
		Object result = "";
		
		String period = getRequest().getParameter("period");		
		String userId = getAuthentication().getName();
		
		TimeForm form = new TimeForm();
		
		Date periodDate = null;
		try 
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormats.FREVVO_DATE_FORMAT);
			
			if (period == null || "".equals(period))
			{
				period = dateFormat.format(new Date());
			}
			
			periodDate = dateFormat.parse(period);		
		}
		catch (ParseException e) 
		{
			LOG.error("Could not parse date sent from Frevvo.", e);
		}
			
		if (periodDate != null)
		{
			Date startDate = getTimeFactory().getStartDate(periodDate);
			Date endDate = getTimeFactory().getEndDate(periodDate);
			
			AcmTimesheet timesheet = getAcmTimesheetDao().findByUserIdStartAndEndDate(userId, startDate, endDate);
			
			if (timesheet != null)
			{
				form = getTimeFactory().asFrevvoTimeForm(timesheet);
			}
			else
			{
				form.setItems(Arrays.asList(new TimeItem()));
			}
			
		}
		
		form.setPeriod(periodDate);
		form.setUser(userId);
		form.setTotals(Arrays.asList(new String()));
		
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
		TimeForm form = (TimeForm) convertFromXMLToObject(cleanXML(xml), TimeForm.class);
		
		if (form == null)
		{
			LOG.warn("Cannot unmarshall Time Form.");
			return false;
		}
		
		// Convert Frevvo form to Acm timesheet
		AcmTimesheet timesheet = getTimeFactory().asAcmTimesheet(form);
		AcmTimesheet saved = getTimesheetService().save(timesheet, submissionName);
		
		form = getTimeFactory().asFrevvoTimeForm(saved);
		
		// TODO: Add logic for approval workflow and save attachemtns
		
		return true;
	}
	
	private Object initFormData()
	{
		String userId = getAuthentication().getName();
        AcmUser user = getUserDao().findByUserId(userId);
		
		TimeForm form = new TimeForm();
		
		// Set user
		form.setUser(userId);
		form.setUserOptions(Arrays.asList(userId + "=" + user.getFullName()));
		
		// Set period (now)
		form.setPeriod(new Date());
		
		List<String> types = convertToList((String) getProperties().get(FrevvoFormName.TIME + ".types"), ",");
		
		// Set charge codes for each type
		Map<String, List<String>> codeOptions = getCodeOptions(types);
		TimeItem item = new TimeItem();
		item.setTypeOptions(types);
		item.setCodeOptions(codeOptions);
		form.setItems(Arrays.asList(item));
		
		// Init Statuses
		form.setStatusOptions(convertToList((String) getProperties().get(FrevvoFormName.TIME + ".statuses"), ","));
		
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
					List<String> options = new ArrayList<>();
					
					if (TimeFormConstants.OTHER.toUpperCase().equals(typeArray[0]))
					{
						options = convertToList((String) getProperties().get(FrevvoFormName.TIME + ".type.other"), ",");
					}
					else
					{
						options = getCodeOptionsByObjectType(typeArray[0]);
					}
					
					codeOptions.put(typeArray[0], options);
				}
			}
		}
		
		return codeOptions;
	}
	
	private List<String> getCodeOptionsByObjectType(String objectType)
	{
		List<String> codeOptions = new ArrayList<>();
		
		JSONObject jsonObject = getTimesheetService().getObjectsFromSolr(objectType, getAuthentication(), 0, 50, "name ASC");
		if (jsonObject != null && jsonObject.has("response") && jsonObject.getJSONObject("response").has("docs"))
		{
			JSONArray objects = jsonObject.getJSONObject("response").getJSONArray("docs");
			
			for (int i = 0; i < objects.length(); i++)
			{
				JSONObject object = objects.getJSONObject(i);
				
				if (object.has("name"))
				{
					codeOptions.add(object.getString("name") + "=" + object.getString("name"));
				}
			}
		}
		
		return codeOptions;
	}

	@Override
	public String getFormName() 
	{
		return FrevvoFormName.TIME;
	}
	
	public TimesheetService getTimesheetService() {
		return timesheetService;
	}

	public void setTimesheetService(TimesheetService timesheetService) {
		this.timesheetService = timesheetService;
	}

	public AcmTimesheetDao getAcmTimesheetDao() {
		return acmTimesheetDao;
	}

	public void setAcmTimesheetDao(AcmTimesheetDao acmTimesheetDao) {
		this.acmTimesheetDao = acmTimesheetDao;
	}

	public TimeFactory getTimeFactory() {
		return timeFactory;
	}

	public void setTimeFactory(TimeFactory timeFactory) {
		this.timeFactory = timeFactory;
	}

}
