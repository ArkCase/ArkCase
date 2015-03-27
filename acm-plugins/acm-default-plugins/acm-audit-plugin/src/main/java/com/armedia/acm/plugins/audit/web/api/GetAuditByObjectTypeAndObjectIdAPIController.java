package com.armedia.acm.plugins.audit.web.api;


import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.core.query.QueryResultPageWithTotalCount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping( { "/api/v1/plugin/audit", "/api/latest/plugin/audit"})
public class GetAuditByObjectTypeAndObjectIdAPIController {
	
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    
    private AuditDao auditDao;
    
    @RequestMapping(value = "/{objectType}/{objectId}",method = RequestMethod.GET,produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public QueryResultPageWithTotalCount<AuditEvent> getComplaintEventsById(
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

        List<AuditEvent> pagedResult = getAuditDao().findPagedResults(objectId, objectType, startRow, maxRows);
        int totalCount = getAuditDao().countAll(objectId, objectType);

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
}

