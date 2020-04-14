/**
 * 
 */
package com.armedia.acm.plugins.audit.web.api;

/*-
 * #%L
 * ACM Default Plugin: Audit
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.core.query.QueryResultPageWithTotalCount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/audit", "/api/latest/plugin/audit" })
public class GetAuditAPIController
{

    private final Logger LOG = LogManager.getLogger(getClass());
    private AuditDao auditDao;

    @RequestMapping(value = "/page", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public QueryResultPageWithTotalCount<AuditEvent> auditPage(
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n,
            @RequestParam(value = "s", required = false, defaultValue = "eventDate DESC") String s)
    {
        QueryResultPageWithTotalCount<AuditEvent> page = new QueryResultPageWithTotalCount<>();
        List<AuditEvent> result = new ArrayList<>();

        String sortBy = "eventDate";
        String sort = "DESC";

        String[] sArray = s.split(" ");
        if (sArray != null)
        {
            if (sArray.length == 1)
            {
                sortBy = sArray[0];
            }
            else if (sArray.length > 1)
            {
                sortBy = sArray[0];
                sort = sArray[1];
            }
        }

        LOG.info("Taking audit records: start=" + start + ", n=" + n);
        result = getAuditDao().findPage(start, n, sortBy, sort);

        LOG.info("Taking total records ...");

        int total = getAuditDao().count();

        LOG.info("Total records: " + total);

        page.setStartRow(start);
        page.setMaxRows(n);
        page.setResultPage(result);
        page.setTotalCount(total);

        return page;
    }

    public AuditDao getAuditDao()
    {
        return auditDao;
    }

    public void setAuditDao(AuditDao auditDao)
    {
        this.auditDao = auditDao;
    }

}
