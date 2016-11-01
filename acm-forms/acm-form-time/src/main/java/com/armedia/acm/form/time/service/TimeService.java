/**
 *
 */
package com.armedia.acm.form.time.service;

import com.armedia.acm.form.config.xml.ApproverItem;
import com.armedia.acm.form.time.model.TimeForm;
import com.armedia.acm.form.time.model.TimeFormConstants;
import com.armedia.acm.form.time.model.TimeItem;
import com.armedia.acm.frevvo.config.FrevvoFormChargeAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.model.Details;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.frevvo.model.Options;
import com.armedia.acm.frevvo.model.OptionsAndDetailsByType;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;
import com.armedia.acm.services.timesheet.service.TimesheetEventPublisher;
import com.armedia.acm.services.timesheet.service.TimesheetService;
import com.armedia.acm.services.users.model.AcmUser;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 */
public class TimeService extends FrevvoFormChargeAbstractService
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private TimesheetService timesheetService;
    private AcmTimesheetDao acmTimesheetDao;
    private TimesheetEventPublisher timesheetEventPublisher;
    private TimeFactory timeFactory;
    private SearchResults searchResults;
    private AcmPluginManager acmPluginManager;

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
            SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormats.TIMESHEET_DATE_FORMAT);

            if (period == null || "".equals(period))
            {
                period = dateFormat.format(new Date());
            }

            periodDate = dateFormat.parse(period);
        } catch (ParseException e)
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
                form = (TimeForm) populateEditInformation(form, timesheet.getContainer(), FrevvoFormName.TIMESHEET.toLowerCase());
            } else
            {
                form.setItems(Arrays.asList(new TimeItem()));
            }

        }

        form.setPeriod(periodDate);
        form.setUser(userId);
        form.setTotals(Arrays.asList(new String()));

        if (form.getApprovers() == null || form.getApprovers().size() == 0)
        {
            form.setApprovers(Arrays.asList(new ApproverItem()));
        }

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

        // Create timesheet folder (if not exist)
        String rootFolder = (String) getTimesheetService().getProperties().get(TimesheetConstants.ROOT_FOLDER_KEY);
        String timesheetTitle = getTimesheetService().createName(timesheet);
        AcmContainer container = createContainer(rootFolder, timesheet.getUser().getUserId(), timesheet.getId(), TimesheetConstants.OBJECT_TYPE, timesheetTitle);
        timesheet.setContainer(container);
        timesheet.setTitle(timesheetTitle);

        AcmTimesheet saved = getTimesheetService().save(timesheet, submissionName);

        form = getTimeFactory().asFrevvoTimeForm(saved);

        // Take user id and ip address
        String userId = getAuthentication().getName();
        String ipAddress = (String) getRequest().getSession().getAttribute("acm_ip_address");

        boolean startWorkflow = getTimesheetService().checkWorkflowStartup(TimesheetConstants.EVENT_TYPE + "." + submissionName.toLowerCase());

        FrevvoUploadedFiles uploadedFiles = null;
        uploadedFiles = saveAttachments(attachments, saved.getContainer().getFolder().getCmisFolderId(), FrevvoFormName.TIMESHEET.toUpperCase(), saved.getId());

        getTimesheetEventPublisher().publishEvent(saved, userId, ipAddress, true, submissionName.toLowerCase(), uploadedFiles, startWorkflow);

        return true;
    }

    private Object initFormData()
    {
        LOG.debug("In initFormData");
        String userId = getAuthentication().getName();

        LOG.debug("Looking for user");
        AcmUser user = getUserDao().findByUserId(userId);

        LOG.debug("Creating time form");
        TimeForm form = new TimeForm();

        LOG.debug("Setting user actions");
        // Set user
        form.setUser(userId);
        form.setUserOptions(Arrays.asList(userId + "=" + user.getFullName()));

        // Set period (now)
        form.setPeriod(new Date());

        LOG.debug("setting form types");
        List<String> types = convertToList((String) getProperties().get(FrevvoFormName.TIMESHEET + ".types"), ",");

        LOG.debug("setting charge codes");
        // Set charge codes for each type and details for them
        OptionsAndDetailsByType optionsAndDetailsByType = getCodeOptionsAndDetails(FrevvoFormName.TIMESHEET, types);

        LOG.debug("getting options");
        Map<String, Options> codeOptions = optionsAndDetailsByType.getOptionsByType();
        LOG.debug("getting detail maps");
        Map<String, Map<String, Details>> codeOptionsDetails = optionsAndDetailsByType.getOptionsDetailsByType();

        LOG.debug("creating time item");
        TimeItem item = new TimeItem();
        item.setTypeOptions(types);
        item.setCodeOptions(codeOptions);
        item.setCodeDetails(codeOptionsDetails);
        form.setItems(Arrays.asList(item));

        // Init Statuses
        LOG.debug("setting statuses");
        form.setStatusOptions(convertToList((String) getProperties().get(FrevvoFormName.TIMESHEET + ".statuses"), ","));

        LOG.debug("Creating json");
        // Create JSON and back to the Frevvo form
        JSONObject json = createResponse(form);

        LOG.debug("JSON to return  - " + json.toString());

        return json;
    }

    @Override
    public Options getOptions(String type, String source)
    {
        Options options = new Options();

        if (TimeFormConstants.OTHER.toUpperCase().equals(type))
        {
            List<String> optionsOther = convertToList((String) getProperties().get(FrevvoFormName.TIMESHEET + ".type.other"), ",");
            options.addAll(optionsOther);
        } else
        {
            options = getCodeOptionsByObjectType(type, source);
        }

        return options;
    }

    @Override
    public String getSolrResponse(String objectType)
    {
        String jsonResults = getTimesheetService().getObjectsFromSolr(objectType, getAuthentication(), 0, 25,
                SearchConstants.PROPERTY_NAME + " " + SearchConstants.SORT_DESC, "*", null);

        return jsonResults;
    }

    @Override
    public String getFormName()
    {
        return FrevvoFormName.TIMESHEET;
    }

    @Override
    public void setFormName(String formName) {
        // No implementation needed so far
    }

    @Override
    public Class<?> getFormClass()
    {
        return TimeForm.class;
    }

    public TimesheetService getTimesheetService()
    {
        return timesheetService;
    }

    public void setTimesheetService(TimesheetService timesheetService)
    {
        this.timesheetService = timesheetService;
    }

    public AcmTimesheetDao getAcmTimesheetDao()
    {
        return acmTimesheetDao;
    }

    public void setAcmTimesheetDao(AcmTimesheetDao acmTimesheetDao)
    {
        this.acmTimesheetDao = acmTimesheetDao;
    }

    public TimesheetEventPublisher getTimesheetEventPublisher()
    {
        return timesheetEventPublisher;
    }

    public void setTimesheetEventPublisher(
            TimesheetEventPublisher timesheetEventPublisher)
    {
        this.timesheetEventPublisher = timesheetEventPublisher;
    }

    public TimeFactory getTimeFactory()
    {
        return timeFactory;
    }

    public void setTimeFactory(TimeFactory timeFactory)
    {
        this.timeFactory = timeFactory;
    }

    public SearchResults getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults)
    {
        this.searchResults = searchResults;
    }

    public AcmPluginManager getAcmPluginManager()
    {
        return acmPluginManager;
    }

    public void setAcmPluginManager(AcmPluginManager acmPluginManager)
    {
        this.acmPluginManager = acmPluginManager;
    }

    @Override
    public Object convertToFrevvoForm(Object obj, Object form)
    {
        // Implementation no needed so far
        return null;
    }
}
