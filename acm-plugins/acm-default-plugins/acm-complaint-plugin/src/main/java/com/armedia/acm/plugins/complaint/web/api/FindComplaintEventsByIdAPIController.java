package com.armedia.acm.plugins.complaint.web.api;


import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.core.query.QueryResultPageWithTotalCount;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;

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
@RequestMapping( { "/api/v1/plugin/complaint", "/api/latest/plugin/complaint"})
public class FindComplaintEventsByIdAPIController {
	
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    
    private AuditDao auditDao;
    
    @RequestMapping(value = "/events/{complaintId}",method = RequestMethod.GET,produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public QueryResultPageWithTotalCount<AuditEvent> getComplaintEventsById(
            @PathVariable(value = "complaintId") Long complaintId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            Authentication authentication)
    {
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug("Finding complaint events; start row: " + startRow + "; max rows: " + maxRows);
        }

        List<AuditEvent> pagedResult = getAuditDao().findPagedResults(complaintId, ComplaintConstants.OBJECT_TYPE, startRow, maxRows);
        int totalCount = getAuditDao().countAll(complaintId, ComplaintConstants.OBJECT_TYPE);

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

