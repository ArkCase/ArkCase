package com.armedia.acm.plugins.casefile.web.api;


import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.core.query.QueryResultPageWithTotalCount;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;

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
@RequestMapping( { "/api/v1/plugin/casefile", "/api/latest/plugin/casefile"})
public class FindCaseFileEventsByIdAPIController {
	
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    
    private AuditDao auditDao;
    
    @RequestMapping(value = "/events/{caseFileId}",method = RequestMethod.GET,produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public QueryResultPageWithTotalCount<AuditEvent> getCaseFileEventsById(
            @PathVariable(value = "caseFileId") Long caseFileId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            Authentication authentication)
    {
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug("Finding case file events; start row: " + startRow + "; max rows: " + maxRows);
        }

        List<AuditEvent> pagedResult = getAuditDao().findPagedResults(caseFileId, CaseFileConstants.OBJECT_TYPE, startRow, maxRows);
        int totalCount = getAuditDao().countAll(caseFileId, CaseFileConstants.OBJECT_TYPE);

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

