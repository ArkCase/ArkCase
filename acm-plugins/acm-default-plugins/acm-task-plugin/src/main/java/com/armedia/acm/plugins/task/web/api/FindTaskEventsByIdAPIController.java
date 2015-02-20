package com.armedia.acm.plugins.task.web.api;


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
 * Created by manoj.dhungana on 11/24/2014.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/task", "/api/latest/plugin/task"})
public class FindTaskEventsByIdAPIController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private AuditDao auditDao;
    @RequestMapping(value = "/events/{taskId}",method = RequestMethod.GET,produces = { MediaType.APPLICATION_JSON_VALUE })
        public @ResponseBody
    QueryResultPageWithTotalCount<AuditEvent> taskEvents(
            @PathVariable(value = "taskId") Long taskId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            Authentication authentication)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Finding default access control; start row: " + startRow + "; max rows: " + maxRows);
        }

        List<AuditEvent> pagedResult = getAuditDao().findPagedResults(taskId,"TASK",startRow, maxRows);
        int totalCount = getAuditDao().countAll(taskId,"TASK");

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

