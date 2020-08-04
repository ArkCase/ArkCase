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
import com.armedia.acm.audit.model.AuditConfig;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.core.query.QueryResultPageWithTotalCount;
import com.armedia.acm.plugins.audit.model.AuditConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

/**
 * @author riste.tutureski
 */
@Controller
@RequestMapping({ "/api/v1/plugin/audit", "/api/latest/plugin/audit" })
public class GetAuditByObjectTypeAndObjectIdAPIController
{

    private final Logger LOG = LogManager.getLogger(getClass());

    private AuditDao auditDao;
    private AuditConfig auditConfig;

    @RequestMapping(value = "/{objectType}/{objectId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public QueryResultPageWithTotalCount<AuditEvent> getEventsByObjectTypeAndObjectId(
            @PathVariable(value = "objectType") String objectType,
            @PathVariable(value = "objectId") Long objectId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "eventDate DESC") String s)
    {

        LOG.debug("Finding audit for {} with id {}; start row: {}; max rows: {}", objectType, objectId, startRow, maxRows);

        String key = String.format("%s.%s", objectType, AuditConstants.HISTORY_TYPES);
        String eventTypesString = auditConfig.getEventTypeByKey(objectType);
        List<String> eventTypes = null;
        if (StringUtils.isNotEmpty(eventTypesString))
        {
            eventTypesString = eventTypesString.trim();
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

    public AuditConfig getAuditConfig()
    {
        return auditConfig;
    }

    public void setAuditConfig(AuditConfig auditConfig)
    {
        this.auditConfig = auditConfig;
    }
}
