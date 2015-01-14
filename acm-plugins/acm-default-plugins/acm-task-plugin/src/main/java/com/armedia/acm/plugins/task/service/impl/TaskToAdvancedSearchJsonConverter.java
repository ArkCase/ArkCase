package com.armedia.acm.plugins.task.service.impl;

import com.armedia.acm.activiti.AcmTaskEvent;
import com.armedia.acm.services.dataaccess.model.AcmAccess;
import com.armedia.acm.services.dataaccess.service.DataAccessEntryService;
import com.armedia.acm.services.search.service.ObjectMapperFactory;
import com.armedia.acm.services.search.service.SearchConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mule.api.annotations.param.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by armdev on 6/24/14.
 */
public class TaskToAdvancedSearchJsonConverter
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String TASK_TYPE = "TASK";
    private DataAccessEntryService dataAccessEntryService;


    // This method is used by Mule when sending tasks to SOLR. Don't act on IDEA's not-used warning.
    public String toAdvancedSearchJson(
            @Payload AcmTaskEvent event) throws JsonProcessingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Converting an ACM Task Event to JSON (Task ID: '" + event.getObjectId() + "'");
        }

        Map<String, Object> advancedSearchMap = new HashMap<>();
        advancedSearchMap.put("title_t", event.getDescription());
        advancedSearchMap.put("object_id_s", event.getObjectId());
        advancedSearchMap.put("create_dt", event.getTaskCreated());
        advancedSearchMap.put("name", event.getTaskName());
        advancedSearchMap.put("status_s", event.getTaskEvent() == null ? null : event.getTaskEvent().toUpperCase()); //
        advancedSearchMap.put("assignee_s", event.getAssignee());
        advancedSearchMap.put("id", event.getObjectId() + "-" + TASK_TYPE);
        advancedSearchMap.put("priority_s", event.getPriority());//
        advancedSearchMap.put("parent_object_id_i", event.getParentObjectId());
        advancedSearchMap.put("parent_object_type_s", event.getParentObjectType());//
        advancedSearchMap.put("due_dt", event.getDueDate());
        advancedSearchMap.put("adhocTask_b", event.isAdhocTask());
        advancedSearchMap.put("owner_s", event.getOwner());

        advancedSearchMap.put("assignee_full_name_lcs",event.getAssignee());
        advancedSearchMap.put("buisenes_process_name_lcs",event.getBusinessProcessName());


        advancedSearchMap.put(SearchConstants.SOLR_OBJECT_TYPE_FIELD_NAME, TASK_TYPE);

        // retrieve acm read access for object and enrich business object map with acls
        AcmAccess acmAccess = getDataAccessEntryService().getAcmReadAccess((Long)advancedSearchMap.get("object_id_s"), TASK_TYPE, (String)advancedSearchMap.get("status_s"));
        acmAccess.enrichWithAcls(advancedSearchMap);

        // We have to send SOLR a JSON array.  If we send a JSON object, SOLR will interpret it as a SOLR command,
        // instead of a document to be indexed.
        ObjectMapper mapper = new ObjectMapperFactory().createObjectMapper();

        String json = mapper.writeValueAsString(Collections.singletonList(advancedSearchMap));

        if ( log.isDebugEnabled() )
        {
            log.debug("Returning JSON: " + json);
        }
        return json;
    }

    public DataAccessEntryService getDataAccessEntryService() {
        return dataAccessEntryService;
    }

    public void setDataAccessEntryService(DataAccessEntryService dataAccessEntryService) {
        this.dataAccessEntryService = dataAccessEntryService;
    }
}
