/**
 *
 */
package com.armedia.acm.services.timesheet.service;

/*-
 * #%L
 * ACM Service: Timesheet
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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;
import com.armedia.acm.services.timesheet.pipeline.TimesheetPipelineContext;

import org.codehaus.plexus.util.StringUtils;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author riste.tutureski
 */
public class TimesheetServiceImpl implements TimesheetService
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private Properties properties;
    private AcmTimesheetDao acmTimesheetDao;
    private Map<String, String> submissionStatusesMap;
    private ExecuteSolrQuery executeSolrQuery;
    private List<String> startWorkflowEvents;

    private PipelineManager<AcmTimesheet, TimesheetPipelineContext> pipelineManager;

    @Override
    public Properties getProperties()
    {
        return properties;
    }

    public void setProperties(Properties properties)
    {
        this.properties = properties;
    }

    @Override
    @Transactional
    public AcmTimesheet save(AcmTimesheet timesheet) throws PipelineProcessException
    {
        TimesheetPipelineContext pipelineContext = new TimesheetPipelineContext();
        return pipelineManager.executeOperation(timesheet, pipelineContext, () -> getAcmTimesheetDao().save(timesheet));
    }

    @Override
    @Transactional
    public AcmTimesheet save(AcmTimesheet timesheet, Authentication authentication, String submissionName) throws PipelineProcessException
    {
        TimesheetPipelineContext pipelineContext = new TimesheetPipelineContext();
        // populate the context
        pipelineContext.setAuthentication(authentication);
        pipelineContext.setNewTimesheet(timesheet.getId() == null);
        pipelineContext.setSubmissonName(submissionName);
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        pipelineContext.setIpAddress(ipAddress);

        return pipelineManager.executeOperation(timesheet, pipelineContext, () -> {
            timesheet.setStatus(getSubmissionStatusesMap().get(submissionName));
            AcmTimesheet saved = getAcmTimesheetDao().save(timesheet);
            LOG.info("Timesheet with id [{}] and title [{}] was saved", saved.getId(), saved.getTitle());
            return saved;
        });
    }

    @Override
    @Transactional
    public AcmTimesheet save(AcmTimesheet timesheet, String submissionName) throws PipelineProcessException
    {

        TimesheetPipelineContext pipelineContext = new TimesheetPipelineContext();
        timesheet.setStatus(getSubmissionStatusesMap().get(submissionName));

        return pipelineManager.executeOperation(timesheet, pipelineContext, () -> getAcmTimesheetDao().save(timesheet));

    }

    @Override
    public AcmTimesheet get(Long id)
    {
        AcmTimesheet timesheet = getAcmTimesheetDao().find(id);

        return timesheet;
    }

    /**
     * Return timesheets with times only for specified object id
     */
    @Override
    public List<AcmTimesheet> getByObjectIdAndType(Long objectId, String objectType, int startRow, int maxRows, String sortParams)
    {
        List<AcmTimesheet> retval = null;

        if (objectId != null)
        {
            // Get timesheets form database for given object id
            List<AcmTimesheet> timesheets = getAcmTimesheetDao().findByObjectIdAndType(objectId, objectType, startRow, maxRows, sortParams);

            if (timesheets != null)
            {
                for (AcmTimesheet timesheet : timesheets)
                {
                    List<AcmTime> times = getTimesForObjectId(timesheet.getTimes(), objectId);
                    timesheet.setTimes(times);

                    if (retval == null)
                    {
                        retval = new ArrayList<>();
                    }

                    retval.add(timesheet);
                }
            }
        }

        return retval;
    }

    private List<AcmTime> getTimesForObjectId(List<AcmTime> times, Long objectId)
    {
        List<AcmTime> retval = new ArrayList<>();

        if (times != null)
        {
            for (AcmTime time : times)
            {
                // If provided objectId is equal with id in the AcmTime, then we should show on UI, otherwise not - skip
                if (objectId.equals(time.getObjectId()))
                {
                    retval.add(time);
                }
            }
        }

        return retval;
    }

    @Override
    public String getObjectsFromSolr(String objectType,
            Authentication authentication,
            int startRow,
            int maxRows,
            String sortParams,
            String searchQuery,
            String userId)
    {
        String retval = null;

        LOG.debug("Taking objects from Solr for object type {}", objectType);

        String authorQuery = "";
        if (!searchQuery.equals("*"))
        {
            searchQuery = "\"" + searchQuery.replace("\"", "\\\"").replace("\\", "\\\\") + "\"";
        }

        if (userId != null)
        {
            authorQuery = " AND author_s:" + userId;
        }

        String query = "object_type_s:" + objectType + authorQuery + " AND name:" + searchQuery + " AND -status_s:DELETE";

        try
        {
            retval = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, query, startRow, maxRows,
                    sortParams);

            LOG.debug("Objects was retrieved.");
        }
        catch (MuleException e)
        {
            LOG.error("Cannot retrieve objects from Solr.", e);
        }

        return retval;
    }

    @Override
    public String getObjectsFromSolr(String objectType,
            Authentication authentication,
            int startRow,
            int maxRows,
            String sortParams,
            String userId)
    {
        String retval = null;

        LOG.debug("Taking objects from Solr for object type {}", objectType);

        String authorQuery = "";
        if (userId != null)
        {
            authorQuery = " AND author_s:" + userId;
        }

        String query = "object_type_s:" + objectType + authorQuery + " AND -status_s:DELETE";

        try
        {
            retval = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, query, startRow, maxRows,
                    sortParams);

            LOG.debug("Objects was retrieved.");
        }
        catch (MuleException e)
        {
            LOG.error("Cannot retrieve objects from Solr.", e);
        }

        return retval;
    }

    @Override
    public boolean checkWorkflowStartup(String type)
    {
        if (getStartWorkflowEvents() != null && getStartWorkflowEvents().contains(type))
        {
            return true;
        }

        return false;
    }

    @Override
    public String createName(AcmTimesheet timesheet)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(DateFormats.TIMESHEET_DATE_FORMAT);

        String objectType = StringUtils.capitalise(TimesheetConstants.OBJECT_TYPE.toLowerCase());
        String startDate = formatter.format(timesheet.getStartDate());
        String endDate = formatter.format(timesheet.getEndDate());

        return objectType + " " + startDate + "-" + endDate;
    }

    public AcmTimesheetDao getAcmTimesheetDao()
    {
        return acmTimesheetDao;
    }

    public void setAcmTimesheetDao(AcmTimesheetDao acmTimesheetDao)
    {
        this.acmTimesheetDao = acmTimesheetDao;
    }

    public Map<String, String> getSubmissionStatusesMap()
    {
        return submissionStatusesMap;
    }

    public void setSubmissionStatusesMap(Map<String, String> submissionStatusesMap)
    {
        this.submissionStatusesMap = submissionStatusesMap;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public List<String> getStartWorkflowEvents()
    {
        return startWorkflowEvents;
    }

    public void setStartWorkflowEvents(List<String> startWorkflowEvents)
    {
        this.startWorkflowEvents = startWorkflowEvents;
    }

    public PipelineManager<AcmTimesheet, TimesheetPipelineContext> getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager<AcmTimesheet, TimesheetPipelineContext> pipelineManager)
    {
        this.pipelineManager = pipelineManager;
    }
}
