package com.armedia.acm.services.search.service;

import com.armedia.acm.activiti.AcmTaskEvent;
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
public class TaskToQuickSearchJsonConverter
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String TASK_TYPE = "TASK";

    public String toQuickSearchJson(
            @Payload AcmTaskEvent event) throws JsonProcessingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Converting an ACM Task Event to JSON (Task ID: '" + event.getObjectId() + "'");
        }

        Map<String, Object> quickSearchMap = new HashMap<>();
        quickSearchMap.put("title_t", event.getDescription());
        quickSearchMap.put("object_id_s", event.getObjectId());
        quickSearchMap.put("create_dt", event.getTaskCreated());
        quickSearchMap.put("name", event.getTaskName());
        quickSearchMap.put("status_s", event.getTaskEvent() == null ? null : event.getTaskEvent().toUpperCase());
        quickSearchMap.put("assignee_s", event.getAssignee());
        quickSearchMap.put("owner_s", event.getAssignee());
        quickSearchMap.put("id", event.getObjectId() + "-" + TASK_TYPE);
        quickSearchMap.put(SearchConstants.SOLR_OBJECT_TYPE_FIELD_NAME, TASK_TYPE);

        // We have to send SOLR a JSON array.  If we send a JSON object, SOLR will interpret it as a SOLR command,
        // instead of a document to be indexed.
        ObjectMapper mapper = new ObjectMapperFactory().createObjectMapper();

        String json = mapper.writeValueAsString(Collections.singletonList(quickSearchMap));

        if ( log.isDebugEnabled() )
        {
            log.debug("Returning JSON: " + json);
        }
        return json;

    }
}
