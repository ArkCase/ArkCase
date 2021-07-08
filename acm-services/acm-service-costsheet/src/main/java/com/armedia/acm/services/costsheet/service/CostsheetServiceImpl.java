package com.armedia.acm.services.costsheet.service;

/*-
 * #%L
 * ACM Service: Costsheet
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
import com.armedia.acm.services.costsheet.dao.AcmCostsheetDao;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.costsheet.pipeline.CostsheetPipelineContext;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author riste.tutureski
 */
public class CostsheetServiceImpl implements CostsheetService
{

    private Logger log = LogManager.getLogger(getClass());

    private Properties properties;
    private AcmCostsheetDao acmCostsheetDao;
    private Map<String, String> submissionStatusesMap;
    private ExecuteSolrQuery executeSolrQuery;
    private List<String> startWorkflowEvents;

    private PipelineManager<AcmCostsheet, CostsheetPipelineContext> pipelineManager;

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
    public AcmCostsheet save(AcmCostsheet costsheet, Authentication authentication, String submissionName) throws PipelineProcessException
    {
        CostsheetPipelineContext pipelineContext = new CostsheetPipelineContext();
        // populate the context
        pipelineContext.setAuthentication(authentication);
        pipelineContext.setNewCostsheet(costsheet.getId() == null);
        pipelineContext.setSubmissonName(submissionName);
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        pipelineContext.setIpAddress(ipAddress);

        return pipelineManager.executeOperation(costsheet, pipelineContext, () -> {
            costsheet.setStatus(getSubmissionStatusesMap().get(submissionName));
            AcmCostsheet saved = getAcmCostsheetDao().save(costsheet);
            log.info("Costsheet with id [{}] and title [{}] was saved", saved.getId(), saved.getTitle());
            return saved;
        });
    }

    @Override
    @Transactional
    public AcmCostsheet save(AcmCostsheet costsheet, String submissionName) throws PipelineProcessException
    {
        CostsheetPipelineContext pipelineContext = new CostsheetPipelineContext();

        return pipelineManager.executeOperation(costsheet, pipelineContext, () -> {

            costsheet.setStatus(getSubmissionStatusesMap().get(submissionName));
            return getAcmCostsheetDao().save(costsheet);
        });

    }

    @Override
    public AcmCostsheet get(Long id)
    {
        AcmCostsheet costsheet = getAcmCostsheetDao().find(id);

        return costsheet;
    }

    /**
     * Return costsheets for specified object id and object type
     */
    @Override
    public List<AcmCostsheet> getByObjectIdAndType(Long objectId, String objectType, int startRow, int maxRows, String sortParams)
    {
        List<AcmCostsheet> retval = null;

        if (objectId != null)
        {
            // Get costsheets form database for given object id and type
            retval = getAcmCostsheetDao().findByObjectIdAndType(objectId, objectType, startRow, maxRows, sortParams);
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

        log.debug("Taking objects from Solr for object type {}", objectType);

        String authorQuery = "";
        if (!searchQuery.equals("*"))
        {
            searchQuery = "\"" + searchQuery.replace("\"", "\\\"").replace("\\", "\\\\") + "\"";
        }

        if (userId != null)
        {
            authorQuery = " AND creator_lcs:" + userId;
        }
        String query = "object_type_s:" + objectType + authorQuery + " AND name:" + searchQuery + " AND -status_lcs:DELETE";

        try
        {
            retval = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows,
                    sortParams);

            log.debug("Objects was retrieved.");
        }
        catch (SolrException e)
        {
            log.error("Cannot retrieve objects from Solr.", e);
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

        log.debug("Taking objects from Solr for object type {}", objectType);

        String authorQuery = "";
        if (userId != null)
        {
            authorQuery = " AND creator_lcs:" + userId;
        }
        String query = "object_type_s:" + objectType + authorQuery + " AND -status_lcs:DELETE";

        try
        {
            retval = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows,
                    sortParams);

            log.debug("Objects was retrieved.");
        }
        catch (SolrException e)
        {
            log.error("Cannot retrieve objects from Solr.", e);
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
    public String createName(AcmCostsheet costsheet)
    {
        String objectType = StringUtils.capitalise(CostsheetConstants.OBJECT_TYPE.toLowerCase());
        String objectNumber = costsheet.getParentNumber();

        return objectType + " " + objectNumber;
    }

    public AcmCostsheetDao getAcmCostsheetDao()
    {
        return acmCostsheetDao;
    }

    public void setAcmCostsheetDao(AcmCostsheetDao acmCostsheetDao)
    {
        this.acmCostsheetDao = acmCostsheetDao;
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

    public PipelineManager<AcmCostsheet, CostsheetPipelineContext> getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager<AcmCostsheet, CostsheetPipelineContext> pipelineManager)
    {
        this.pipelineManager = pipelineManager;
    }
}
