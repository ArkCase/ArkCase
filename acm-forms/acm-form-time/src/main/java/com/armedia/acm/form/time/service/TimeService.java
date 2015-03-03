/**
 * 
 */
package com.armedia.acm.form.time.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.form.time.model.TimeForm;
import com.armedia.acm.form.time.model.TimeFormConstants;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;
import com.armedia.acm.services.users.model.AcmUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author riste.tutureski
 *
 */
public class TimeService extends FrevvoFormAbstractService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private AcmTimesheetDao acmTimesheetDao;
	private TimeFactory timeFactory;
	private ComplaintDao complaintDao;
	private CaseFileDao caseFileDao;
	
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
		TimeForm form = (TimeForm) convertFromXMLToObject(cleanXML(xml), TimeForm.class);
		
		if (form == null)
		{
			LOG.warn("Cannot unmarshall Time Form.");
			return false;
		}
		
		// Convert Frevvo form to Acm timesheet
		AcmTimesheet timesheet = getTimeFactory().asAcmTimesheet(form);
		
		// If submission_name is "Save", save as draft (editable version), otherwise save for approval
		String submission_name = getRequest().getParameter("submission_name");
		if (submission_name != null && submission_name.equals(TimeFormConstants.SAVE) && timesheet != null)
		{
			timesheet.setStatus(TimesheetConstants.DRAFT);
		}
		else if (submission_name != null && submission_name.equals(TimeFormConstants.SUBMIT) && timesheet != null)
		{
			timesheet.setStatus(TimesheetConstants.IN_APPROVAL);
		}
		else
		{
			LOG.warn("Cannot save the timehseet. Maybe submission name is not provided or timesheet itself not contain requred information.");
			return false;
		}
		
		AcmTimesheet saved = getAcmTimesheetDao().save(timesheet);
		
		form = getTimeFactory().asFrevvoTimeForm(saved);
		
		// TODO: Add logic for approval workflow and edit form
		
		return true;
	}
	
	private Object initFormData()
	{
		String userId = getAuthentication().getName();
        AcmUser user = getUserDao().findByUserId(userId);
		String type = getRequest().getParameter("type");
		
		TimeForm form = new TimeForm();
		
		// Set user
		form.setUser(userId);
		form.setUserOptions(Arrays.asList(userId + "=" + user.getFullName()));
		
		// Set period (now)
		form.setPeriod(new Date());
		
		// Set charge codes depends on the type
		List<String> codeOptions = new ArrayList<>();
		if (type != null)
		{
			if (FrevvoFormName.COMPLAINT.toUpperCase().equals(type))
			{
				codeOptions = getComplaintCodeOptions();
			}
			else if (FrevvoFormName.CASE_FILE.toUpperCase().equals(type))
			{
				codeOptions = getCaseFileCodeOptions();
			}
		}
		
		form.setCodeOptions(codeOptions);
		
		// Init Statuses
		form.setStatusOptions(convertToList((String) getProperties().get(FrevvoFormName.TIME + ".statuses"), ","));
		
		// Create JSON and back to the Frevvo form
		Gson gson = new GsonBuilder().setDateFormat(DateFormats.FREVVO_DATE_FORMAT).create();
		String jsonString = gson.toJson(form);
		
		JSONObject json = new JSONObject(jsonString);

		return json;
	}
	
	private List<String> getComplaintCodeOptions()
	{
		List<String> codeOptions = new ArrayList<>();
		List<Complaint> complaints = getComplaintDao().findAll();
		if (complaints != null)
		{
			for (Complaint complaint : complaints)
			{
				codeOptions.add(complaint.getComplaintId() + "=" + complaint.getComplaintNumber());
			}
		}
		
		return codeOptions;
	}
	
	private List<String> getCaseFileCodeOptions()
	{
		List<String> codeOptions = new ArrayList<>();
		List<CaseFile> caseFiles = getCaseFileDao().findAll();
		if (caseFiles != null)
		{
			for (CaseFile caseFile : caseFiles)
			{
				codeOptions.add(caseFile.getId() + "=" + caseFile.getCaseNumber());
			}
		}
		
		return codeOptions;
	}

	@Override
	public String getFormName() 
	{
		return FrevvoFormName.TIME;
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

	public ComplaintDao getComplaintDao() {
		return complaintDao;
	}

	public void setComplaintDao(ComplaintDao complaintDao) {
		this.complaintDao = complaintDao;
	}

	public CaseFileDao getCaseFileDao() {
		return caseFileDao;
	}

	public void setCaseFileDao(CaseFileDao caseFileDao) {
		this.caseFileDao = caseFileDao;
	}

}
