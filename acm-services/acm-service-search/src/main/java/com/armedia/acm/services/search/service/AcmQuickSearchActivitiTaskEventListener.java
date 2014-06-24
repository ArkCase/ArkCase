package com.armedia.acm.services.search.service;

import com.armedia.acm.activiti.AcmTaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by armdev on 6/24/14.
 */
public class AcmQuickSearchActivitiTaskEventListener implements ApplicationListener<AcmTaskEvent>
{

    private JmsSender jmsSender;

    private final Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public void onApplicationEvent(AcmTaskEvent event)
    {
        Map<String, Object> quickSearchMap = new HashMap<>();
        quickSearchMap.put("title", event.getDescription());
        quickSearchMap.put("object_id_s", event.getObjectId());
        quickSearchMap.put("create_dt", event.getTaskCreated());
        quickSearchMap.put("name", event.getTaskName());
        quickSearchMap.put("status_s", event.getTaskEvent() == null ? null : event.getTaskEvent().toUpperCase());
        quickSearchMap.put("assignee_s", event.getAssignee());
        quickSearchMap.put("id", event.getObjectId() + "-" + event.getObjectType());
        quickSearchMap.put(SearchConstants.SOLR_OBJECT_TYPE_FIELD_NAME, event.getObjectType());

        getJmsSender().sendToJms(Collections.singletonList(quickSearchMap));


    }

    public JmsSender getJmsSender()
    {
        return jmsSender;
    }

    public void setJmsSender(JmsSender jmsSender)
    {
        this.jmsSender = jmsSender;
    }
}
