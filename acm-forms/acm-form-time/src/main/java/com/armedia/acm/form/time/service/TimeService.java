/**
 *
 */
package com.armedia.acm.form.time.service;

/*-
 * #%L
 * ACM Forms: Time
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.form.config.xml.ApproverItem;
import com.armedia.acm.form.time.model.TimeForm;
import com.armedia.acm.form.time.model.TimeItem;
import com.armedia.acm.frevvo.config.FrevvoFormChargeAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.model.UploadedFiles;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;
import com.armedia.acm.services.timesheet.service.TimesheetConfigurationService;
import com.armedia.acm.services.timesheet.service.TimesheetEventPublisher;
import com.armedia.acm.services.timesheet.service.TimesheetService;
import com.armedia.acm.services.users.model.AcmUser;
import com.google.common.base.Objects;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author riste.tutureski
 */
public class TimeService extends FrevvoFormChargeAbstractService
{

    private Logger LOG = LogManager.getLogger(getClass());

    private TimesheetService timesheetService;
    private AcmTimesheetDao acmTimesheetDao;
    private TimesheetEventPublisher timesheetEventPublisher;
    private TimeFactory timeFactory;
    private TimesheetConfigurationService timesheetConfigurationService;

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
                form = (TimeForm) populateEditInformation(form, timesheet.getContainer(), getFormName().toLowerCase());
                if (form.getItems() != null)
                {
                    String objectIdString = getRequest().getParameter("_id");
                    String objectType = getRequest().getParameter("_type");
                    String objectNumber = getRequest().getParameter("_number");

                    TimeItem found = findTimeItem(form.getItems());
                    if (found == null && StringUtils.isNotEmpty(objectIdString) && StringUtils.isNotEmpty(objectType)
                            && StringUtils.isNotEmpty(objectNumber))
                    {
                        form.getItems().add(getTimeItem());
                    }
                }
            }
            else
            {
                form.setItems(Arrays.asList(getTimeItem()));
            }

        }

        form.setPeriod(periodDate);
        form.setPeriodUI(periodDate);
        form.setUser(userId);
        form.setTotals(Arrays.asList(new String()));

        if (form.getApprovers() == null || form.getApprovers().size() == 0)
        {
            form.setApprovers(Arrays.asList(new ApproverItem()));
        }

        result = convertFromObjectToXML(form);

        return result;
    }

    private TimeItem getTimeItem()
    {
        TimeItem timeItem = new TimeItem();

        String objectIdString = getRequest().getParameter("_id");
        String objectType = getRequest().getParameter("_type");
        String objectNumber = getRequest().getParameter("_number");

        if (StringUtils.isNotEmpty(objectIdString) && StringUtils.isNotEmpty(objectType) && StringUtils.isNotEmpty(objectNumber))
        {
            Long objectId = null;

            try
            {
                objectId = Long.parseLong(objectIdString);
            }
            catch (Exception e)
            {
                LOG.warn("Cannot convert string [{}] to long type. null value will be used instead", objectIdString);

            }

            timeItem.setObjectId(objectId);
            timeItem.setType(objectType);
            timeItem.setCode(objectNumber);
        }

        return timeItem;
    }

    private TimeItem findTimeItem(List<TimeItem> timeItems)
    {
        TimeItem timeItem = null;

        String objectIdString = getRequest().getParameter("_id");
        String objectType = getRequest().getParameter("_type");

        if (timeItems != null && StringUtils.isNotEmpty(objectIdString) && StringUtils.isNotEmpty(objectType))
        {
            try
            {
                final Long objectId = Long.parseLong(objectIdString);
                Optional<TimeItem> found = timeItems.stream()
                        .filter(item -> Objects.equal(item.getObjectId(), objectId) && Objects.equal(item.getType(), objectType))
                        .findFirst();

                if (found.isPresent())
                {
                    timeItem = found.get();
                }
            }
            catch (Exception e)
            {
                LOG.warn("Cannot convert string [{}] to long type. null item will be returned", objectIdString);

            }
        }

        return timeItem;
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
        TimeForm form = (TimeForm) convertFromXMLToObject(cleanXML(xml), getFormClass());

        if (form == null)
        {
            LOG.warn("Cannot unmarshall Time Form.");
            return false;
        }

        // Convert Frevvo form to Acm timesheet
        AcmTimesheet timesheet = getTimeFactory().asAcmTimesheet(form);

        // Create timesheet folder (if not exist)
        String rootFolder = getTimesheetService().getConfiguration().getRootFolder();
        String timesheetTitle = getTimesheetService().createName(timesheet);
        AcmContainer container = createContainer(rootFolder, timesheet.getUser().getUserId(), timesheet.getId(),
                TimesheetConstants.OBJECT_TYPE, timesheetTitle);
        timesheet.setContainer(container);
        timesheet.setTitle(timesheetTitle);

        AcmTimesheet saved = getTimesheetService().save(timesheet, submissionName);

        // Take user id and ip address
        String userId = getAuthentication().getName();
        String ipAddress = (String) getRequest().getSession().getAttribute("acm_ip_address");

        boolean startWorkflow = getTimesheetService()
                .checkWorkflowStartup(TimesheetConstants.EVENT_TYPE + "." + submissionName.toLowerCase());

        UploadedFiles uploadedFiles = saveAttachments(attachments, saved.getContainer().getFolder().getCmisFolderId(),
                getFormName().toUpperCase(), saved.getId());

        getTimesheetEventPublisher().publishEvent(saved, userId, ipAddress, true, submissionName.toLowerCase(), uploadedFiles,
                startWorkflow);

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

        // Set timesheet config
        form.setTimesheetConfig(getTimesheetConfigurationService().loadTimesheetChargeRolesConfig());

        LOG.debug("setting form types");
        List<String> types = getStandardLookupEntries("timesheetTypes");

        LOG.debug("setting charge codes");

        LOG.debug("creating time item");
        TimeItem item = new TimeItem();
        item.setTypeOptions(types);
        item.setChargeRoles(getStandardLookupEntries("timesheetChargeRoles"));
        form.setItems(Arrays.asList(item));

        // Init Statuses
        LOG.debug("setting statuses");
        form.setStatusOptions(getStandardLookupEntries("timesheetStatuses"));

        LOG.debug("Creating json");
        // Create JSON and back to the Frevvo form
        JSONObject json = createResponse(form);

        LOG.debug("JSON to return  - {}", json.toString());

        return json;
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

    public void setTimesheetEventPublisher(TimesheetEventPublisher timesheetEventPublisher)
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

    public TimesheetConfigurationService getTimesheetConfigurationService()
    {
        return timesheetConfigurationService;
    }

    public void setTimesheetConfigurationService(TimesheetConfigurationService timesheetConfigurationService)
    {
        this.timesheetConfigurationService = timesheetConfigurationService;
    }

    @Override
    public Object convertToFrevvoForm(Object obj, Object form)
    {
        // Implementation no needed so far
        return null;
    }
}
