package com.armedia.acm.audit.dao;

/*-
 * #%L
 * ACM Service: Audit Library
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

import static org.junit.Assert.assertNotNull;

import com.armedia.acm.audit.model.AuditEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring-library-audit-test.xml",
        "/spring/spring-library-object-converter.xml"
})
public class AuditDaoIT
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private AuditDao dao;

    @Test
    public void findEvents() throws Exception
    {
        String objectType = "DASHBOARD";
        Long dashboardId = 500L; // just want to make sure the query executes, doesn't have to return data

        List<AuditEvent> events = dao.findAuditsByEventPatternAndObjectId(objectType, dashboardId);

        assertNotNull(events);

        log.info("# of dashboard events: " + events.size());

    }

    @Test
    public void findPagedEvents() throws Exception
    {
        int maxRows = 10;
        int startRow = 0;
        String objectType = "TASK";
        Long objectId = 100L; // just want to make sure the query executes, doesn't have to return data
        List<String> eventTypes = null;
        String sortBy = "eventDate";
        String sort = "DESC";

        List<AuditEvent> events = dao.findPagedResults(objectId, objectType, startRow, maxRows, eventTypes, sortBy, sort);

        assertNotNull(events);

        log.info("# of task events: " + events.size());
    }
}
