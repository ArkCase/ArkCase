package com.armedia.acm.plugins.task.web.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.SolrResponse;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class CreateAdHocTaskAPIController
{
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;
    private MuleClient muleClient;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/adHocTask", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask createAdHocTask(
            @RequestBody AcmTask in,
            Authentication authentication,
            HttpSession httpSession
    ) throws AcmCreateObjectFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Creating ad-hoc task.");
        }

        try
        {
        	in.setOwner(authentication.getName());
            //find the complaint id by name
            String objectId;
            String objectNumber;
            if(in.getAttachedToObjectName() != ""){
                objectNumber = in.getAttachedToObjectName();
                in.setAttachedToObjectName(objectNumber);
                objectId  = findObjectIdByName(in.getAttachedToObjectType(),in.getAttachedToObjectName(), authentication);
            }
            else{
                objectId = null;
            }
            if(objectId != null){
                in.setAttachedToObjectId(Long.parseLong(objectId));
            }
            else{
                in.setAttachedToObjectId(null);
            }

            AcmTask adHocTask = getTaskDao().createAdHocTask(in);
            publishAdHocTaskCreatedEvent(authentication, httpSession, adHocTask, true);

            return adHocTask;
        }
        catch (MuleException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(null);  // no object id since the task could not be created
            publishAdHocTaskCreatedEvent(authentication, httpSession, fakeTask, false);
            throw new AcmCreateObjectFailedException("task", e.getMessage(), e);
        }
        catch (AcmTaskException e)
        {
            // gen up a fake task so we can audit the failure
            AcmTask fakeTask = new AcmTask();
            fakeTask.setTaskId(null);  // no object id since the task could not be created
            publishAdHocTaskCreatedEvent(authentication, httpSession, fakeTask, false);
            throw new AcmCreateObjectFailedException("task", e.getMessage(), e);
        }
    }
    
    protected void publishAdHocTaskCreatedEvent(
            Authentication authentication,
            HttpSession httpSession,
            AcmTask created,
            boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(created, "create", authentication.getName(), succeeded, ipAddress);
        getTaskEventPublisher().publishTaskEvent(event);
    }

    /**
     * Find the complaint or case object id by the complaint or case name. The object id is 
     * used to associate the task to a complaint or case.
     * 
     * @param name
     * @param authentication
     * @return
     * @throws MuleException
     */
    private String findObjectIdByName(String type, String name, Authentication authentication) throws MuleException {
        if(name != null){
            String query = "name:" + name;

            query += " AND (object_type_s:" + type + ")";


            if ( log.isDebugEnabled() )
            {
                log.debug("User '" + authentication.getName() + "' is searching for '" + query + "'");
            }

            Map<String, Object> headers = new HashMap<>();
            headers.put("query", query);
            headers.put("firstRow", 0);
            headers.put("maxRows", 10);
            headers.put("sort", "");

            MuleMessage response = getMuleClient().send("vm://quickSearchQuery.in", "", headers);
            log.debug("Response type: " + response.getPayload().getClass());

            SolrResponse solrResponse = getSolrData(response);

            if ( solrResponse == null ) {
                throw new NullPointerException("Object id not found.");
            }
            else {
                return solrResponse.getResponse().getDocs().get(0).getObject_id_s();
            }
        }
        return null;
    }
    
    /**
     * Retrieve the solr data (json) of the given complaint or case
     * 
     * @param response
     * @return
     */
    private SolrResponse getSolrData(MuleMessage response) {
        String responsePayload = (String) response.getPayload();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SolrResponse solrResponse = gson.fromJson(responsePayload, SolrResponse.class);
        int numFound = solrResponse.getResponse().getNumFound();
    	
    	return numFound > 0? solrResponse : null;
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

	public MuleClient getMuleClient() {
		return muleClient;
	}

	public void setMuleClient(MuleClient muleClient) {
		this.muleClient = muleClient;
	}

}
