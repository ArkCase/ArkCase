package com.armedia.acm.plugins.task.service.impl;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.LinkAlreadyExistException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.service.AcmTaskService;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

public class CreateAdHocTaskService
{
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;
    private ExecuteSolrQuery executeSolrQuery;
    private EcmFileService ecmFileService;
    private AcmTaskService acmTaskService;

    private SearchResults searchResults = new SearchResults();
    private Logger log = LogManager.getLogger(getClass());

    public AcmTask createAdHocTask(AcmTask in, List<MultipartFile> filesToUpload, Authentication authentication, String ipAddress)
            throws AcmCreateObjectFailedException, AcmAppErrorJsonMsg, AcmUserActionFailedException, LinkAlreadyExistException,
            AcmObjectNotFoundException
    {
        log.info("Creating ad-hoc task.");
        String user = authentication.getName();
        String attachedToObjectName = in.getAttachedToObjectName();
        try
        {
            in.setOwner(user);
            // On creation task is always ACTIVE
            in.setStatus(TaskConstants.STATE_ACTIVE);

            String parentObjectType = in.getAttachedToObjectType() != null ? in.getAttachedToObjectType() : in.getParentObjectType();
            Long parentObjectId = in.getAttachedToObjectId() != null ? in.getAttachedToObjectId() : in.getParentObjectId();

            if (StringUtils.isNotBlank(attachedToObjectName) && StringUtils.isNotBlank(parentObjectType) && parentObjectId == null)
            {
                // find the associated object (CASE/COMPLAINT) id by it's name
                String obj = getObjectsFromSolr(parentObjectType, attachedToObjectName, authentication, 0, 10, "", null);
                if (obj != null && getSearchResults().getNumFound(obj) > 0)
                {
                    JSONArray results = getSearchResults().getDocuments(obj);
                    JSONObject result = results.getJSONObject(0);
                    parentObjectId = getSearchResults().extractLong(result, SearchConstants.PROPERTY_OBJECT_ID_S);
                    parentObjectType = getSearchResults().extractString(result, SearchConstants.PROPERTY_OBJECT_TYPE_S);
                }
                else
                {
                    throw new AcmAppErrorJsonMsg(
                            String.format("Task failed to create. Associated object" + " with name [%s] not found.", attachedToObjectName),
                            TaskConstants.OBJECT_TYPE, "associated-object", null);
                }
            }

            if (parentObjectId != null)
            {
                in.setAttachedToObjectId(parentObjectId);
                in.setAttachedToObjectType(parentObjectType);
                in.setAttachedToObjectName(attachedToObjectName);
                in.setParentObjectId(parentObjectId);
                in.setParentObjectType(parentObjectType);
                in.setParentObjectName(attachedToObjectName);
            }
            else
            {
                in.setAttachedToObjectId(null);
                in.setAttachedToObjectType(null);
                in.setAttachedToObjectName(null);
            }

            AcmTask adHocTask = getTaskDao().createAdHocTask(in);
            AcmFolder folder = adHocTask.getContainer().getAttachmentFolder();
            if (filesToUpload != null)
            {
                for (MultipartFile file : filesToUpload)
                {

                    ecmFileService.upload(file.getOriginalFilename(), "Other", file, authentication,
                            folder.getCmisFolderId(),
                            adHocTask.getObjectType(),
                            adHocTask.getTaskId());
                }
            }

            if (adHocTask.getParentObjectId() != null)
            {
                getAcmTaskService().createTaskFolderStructureInParentObject(adHocTask);
            }

            publishAdHocTaskCreatedEvent(authentication, ipAddress, adHocTask, true);
            return adHocTask;
        }
        catch (AcmTaskException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(null); // no object id since the task could not be created
            publishAdHocTaskCreatedEvent(authentication, ipAddress, fakeTask, false);
            throw new AcmCreateObjectFailedException("task", e.getMessage(), e);
        }
    }

    protected void publishAdHocTaskCreatedEvent(Authentication authentication, String ipAddress, AcmTask created, boolean succeeded)
    {
        AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(created, "create", authentication.getName(), succeeded, ipAddress);
        getTaskEventPublisher().publishTaskEvent(event);
        if (created.getStatus() != null && created.getStatus().equalsIgnoreCase(TaskConstants.STATE_CLOSED))
        {
            event = new AcmApplicationTaskEvent(created, "complete", authentication.getName(), succeeded, ipAddress);
            getTaskEventPublisher().publishTaskEvent(event);
        }
    }

    public String getObjectsFromSolr(String objectType, String objectName, Authentication authentication, int startRow, int maxRows,
            String sortParams, String userId)
    {
        String retval = null;

        log.debug("Taking objects from Solr for objectType:{}", objectType);

        String authorQuery = "";
        if (userId != null)
        {
            authorQuery = " AND author_s:" + userId;
        }

        String query = "object_type_s:" + objectType + " AND name:" + objectName + authorQuery + " AND -status_s:DELETE";

        try
        {
            retval = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, query, startRow, maxRows,
                    sortParams);

            log.debug("Objects was retrieved.");
        }
        catch (SolrException e)
        {
            log.error("Cannot retrieve objects from Solr.", e);
        }

        return retval;
    }

    public AcmTask createTask(AcmTask in, Authentication authentication, String ipAddress)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        log.info("Creating task.");
        String user = authentication.getName();

        try
        {
            in.setOwner(user);
            // On creation task is always ACTIVE
            in.setStatus(TaskConstants.STATE_ACTIVE);

            AcmTask adHocTask = getTaskDao().createAdHocTask(in);

            publishAdHocTaskCreatedEvent(authentication, ipAddress, adHocTask, true);

            return adHocTask;
        }
        catch (AcmTaskException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(null); // no object id since the task could not be created
            publishAdHocTaskCreatedEvent(authentication, ipAddress, fakeTask, false);
            throw new AcmCreateObjectFailedException("task", e.getMessage(), e);
        }
    }

    public TaskDao getTaskDao()
    {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }

    public TaskEventPublisher getTaskEventPublisher()
    {
        return taskEventPublisher;
    }

    public void setTaskEventPublisher(TaskEventPublisher taskEventPublisher)
    {
        this.taskEventPublisher = taskEventPublisher;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public SearchResults getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults)
    {
        this.searchResults = searchResults;
    }

    public Logger getLog()
    {
        return log;
    }

    public void setLog(Logger log)
    {
        this.log = log;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public AcmTaskService getAcmTaskService()
    {
        return acmTaskService;
    }

    public void setAcmTaskService(AcmTaskService acmTaskService)
    {
        this.acmTaskService = acmTaskService;
    }
}
