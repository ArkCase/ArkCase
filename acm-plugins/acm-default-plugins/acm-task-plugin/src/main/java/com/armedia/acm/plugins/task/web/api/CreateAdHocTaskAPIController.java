package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;

@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class CreateAdHocTaskAPIController
{
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;
    private ExecuteSolrQuery executeSolrQuery;
    private EcmFileParticipantService fileParticipantService;

    private SearchResults searchResults = new SearchResults();

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/adHocTask", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask createAdHocTask(@RequestBody AcmTask in, Authentication authentication, HttpSession httpSession)
            throws AcmCreateObjectFailedException, AcmAppErrorJsonMsg
    {

        log.info("Creating ad-hoc task.");

        String attachedToObjectType = in.getAttachedToObjectType();
        String attachedToObjectName = in.getAttachedToObjectName();
        try
        {
            in.setOwner(authentication.getName());
            // On creation task is always ACTIVE
            in.setStatus(TaskConstants.STATE_ACTIVE);

            String parentObjectType = null;
            Long objectId = null;
            if (attachedToObjectName != "")
            {
                // find the associated object (CASE/COMPLAINT) id by it's name
                String obj = getObjectsFromSolr(attachedToObjectType, attachedToObjectName, authentication, 0, 10, "", null);
                if (obj != null && getSearchResults().getNumFound(obj) > 0)
                {
                    JSONArray results = getSearchResults().getDocuments(obj);
                    JSONObject result = results.getJSONObject(0);
                    objectId = getSearchResults().extractLong(result, SearchConstants.PROPERTY_OBJECT_ID_S);
                    parentObjectType = getSearchResults().extractString(result, SearchConstants.PROPERTY_OBJECT_TYPE_S);
                }
                else
                {
                    throw new AcmAppErrorJsonMsg(
                            String.format("Task failed to create. Associated object" + " with name [%s] not found.", attachedToObjectName),
                            TaskConstants.OBJECT_TYPE, "associated-object", null);
                }
            }

            if (objectId != null)
            {
                in.setAttachedToObjectId(objectId);
                in.setAttachedToObjectType(parentObjectType);
                in.setAttachedToObjectName(attachedToObjectName);
                in.setParentObjectId(objectId);
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
            try
            {
                adHocTask.getParticipants().forEach(participant -> participant.setReplaceChildrenParticipant(true));
                getFileParticipantService().inheritParticipantsFromAssignedObject(adHocTask.getParticipants(), new ArrayList<>(),
                        adHocTask.getContainer());
                getFileParticipantService().setRestrictedFlagRecursively(adHocTask.getRestricted(), adHocTask.getContainer());
            }
            catch (AcmAccessControlException e)
            {
                throw new AcmCreateObjectFailedException(adHocTask.getObjectType(), "Failed to set participants on child folders!", e);
            }
            publishAdHocTaskCreatedEvent(authentication, httpSession, adHocTask, true);

            return adHocTask;
        }
        catch (AcmTaskException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(null); // no object id since the task could not be created
            publishAdHocTaskCreatedEvent(authentication, httpSession, fakeTask, false);
            throw new AcmCreateObjectFailedException("task", e.getMessage(), e);
        }
    }

    protected void publishAdHocTaskCreatedEvent(Authentication authentication, HttpSession httpSession, AcmTask created, boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
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
        catch (MuleException e)
        {
            log.error("Cannot retrieve objects from Solr.", e);
        }

        return retval;
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

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
    }
}
