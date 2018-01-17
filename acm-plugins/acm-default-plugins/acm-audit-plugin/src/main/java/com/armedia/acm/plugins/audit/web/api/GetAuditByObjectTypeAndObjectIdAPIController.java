package com.armedia.acm.plugins.audit.web.api;

import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.core.query.QueryResultPageWithTotalCount;
import com.armedia.acm.plugins.audit.model.AuditConstants;
import com.armedia.acm.plugins.audit.service.ReplaceEventTypeNames;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 */
@Controller
@RequestMapping({ "/api/v1/plugin/audit", "/api/latest/plugin/audit" })
public class GetAuditByObjectTypeAndObjectIdAPIController
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private AuditDao auditDao;
    private Map<String, String> auditProperties;
    private ReplaceEventTypeNames replaceEventTypeNames;

    @RequestMapping(value = "/{objectType}/{objectId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public QueryResultPageWithTotalCount<AuditEvent> getEventsByObjectTypeAndObjectId(
            @PathVariable(value = "objectType") String objectType,
            @PathVariable(value = "objectId") Long objectId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "eventDate DESC") String s,
            Authentication authentication)
    {

        LOG.debug("Finding audit for {} with id {}; start row: {}; max rows: {}", objectType, objectId, startRow, maxRows);

        String key = String.format("%s.%s", objectType, AuditConstants.HISTORY_TYPES);
        String eventTypesString = getAuditProperties().get(key);
        List<String> eventTypes = null;
        if (StringUtils.isNotEmpty(eventTypesString))
        {
            eventTypesString.trim();
            eventTypes = Arrays.asList(eventTypesString.split("\\s*,\\s*"));
        }

        String sortBy = "eventDate";
        String direction = "DESC";

        String[] sArray = s.split(" ");
        if (sArray.length == 1)
        {
            sortBy = sArray[0];
        }
        else if (sArray.length > 1)
        {
            sortBy = sArray[0];
            direction = sArray[1];
        }
        List<AuditEvent> pagedResult = getAuditDao().findPagedResults(objectId, objectType, startRow, maxRows, eventTypes, sortBy,
                direction);

        int totalCount = getAuditDao().countAll(objectId, objectType, eventTypes);

        QueryResultPageWithTotalCount<AuditEvent> retval = new QueryResultPageWithTotalCount<>();
        retval.setStartRow(startRow);
        retval.setMaxRows(maxRows);
        retval.setTotalCount(totalCount);
        retval.setResultPage(pagedResult);

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

    public Map<String, String> getAuditProperties()
    {
        return auditProperties;
    }

    public void setAuditProperties(Map<String, String> auditProperties)
    {
        this.auditProperties = auditProperties;
    }

    public ReplaceEventTypeNames getReplaceEventTypeNames()
    {
        return replaceEventTypeNames;
    }

    public void setReplaceEventTypeNames(ReplaceEventTypeNames replaceEventTypeNames)
    {
        this.replaceEventTypeNames = replaceEventTypeNames;
    }
}
