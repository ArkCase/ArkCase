package com.armedia.acm.plugins.audit.web.api;


import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.core.query.QueryResultPageWithTotalCount;

import com.armedia.acm.plugins.audit.service.ReplaceEventTypeNames;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping( { "/api/v1/plugin/audit", "/api/latest/plugin/audit"})
public class GetAuditByObjectTypeAndObjectIdAPIController {
	
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    public static final String HISTORY_TYPES = "history.event.types";
    
    private AuditDao auditDao;
    private Map<String, String> auditProperties;
    private ReplaceEventTypeNames replaceEventTypeNames;
    
    @RequestMapping(value = "/{objectType}/{objectId}",method = RequestMethod.GET,produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public QueryResultPageWithTotalCount<AuditEvent> getEventsByObjectTypeAndObjectId(
    		@PathVariable(value = "objectType") String objectType,
            @PathVariable(value = "objectId") Long objectId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            Authentication authentication)
    {
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug("Finding audit for " + objectType + " with id "  + objectId + "; start row: " + startRow + "; max rows: " + maxRows);
        }

        String eventTypesString = getAuditProperties().get(String.format("%s.%s", objectType, HISTORY_TYPES));
        List<String> eventTypes = null;
        if (StringUtils.isNotEmpty(eventTypesString))
        {
            eventTypes = new ArrayList<>(Arrays.asList(eventTypesString.split("\\s*,\\s*")));
        }

        List<AuditEvent> pagedResult = getAuditDao().findPagedResults(objectId, objectType, startRow, maxRows, eventTypes);

        int totalCount = getAuditDao().countAll(objectId, objectType, eventTypes);

        QueryResultPageWithTotalCount<AuditEvent> retval = new QueryResultPageWithTotalCount<>();
        retval.setStartRow(startRow);
        retval.setMaxRows(maxRows);
        retval.setTotalCount(totalCount);
        retval.setResultPage(pagedResult);
        List<AuditEvent> eventList = new ArrayList<>();
        List<AuditEvent> auditEvents = retval.getResultPage();
        for (AuditEvent event : auditEvents){

            event.setFullEventType(getReplaceEventTypeNames().replaceNameInAcmEvent(event).getFullEventType());
            eventList.add(event);
        }
        retval.setResultPage(eventList);
        return retval;
    }

    public AuditDao getAuditDao()
    {
        return auditDao;
    }
    public void setAuditDao(AuditDao dao)
    {
        this.auditDao = dao;
    }

    public Map<String, String> getAuditProperties() {
        return auditProperties;
    }

    public void setAuditProperties(Map<String, String> auditProperties) {
        this.auditProperties = auditProperties;
    }

    public ReplaceEventTypeNames getReplaceEventTypeNames() {
        return replaceEventTypeNames;
    }

    public void setReplaceEventTypeNames(ReplaceEventTypeNames replaceEventTypeNames) {
        this.replaceEventTypeNames = replaceEventTypeNames;
    }
}

