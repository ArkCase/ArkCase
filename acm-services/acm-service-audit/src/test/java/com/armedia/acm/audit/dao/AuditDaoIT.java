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
import static org.junit.Assert.fail;

import com.armedia.acm.audit.model.AuditEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring-library-audit-test.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-configuration.xml"
})
public class AuditDaoIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
    }

    private final Logger log = LogManager.getLogger(getClass());
    @Autowired
    private AuditDao dao;

    @Test
    public void findPage() throws Exception
    {
        // just make sure the query executes
        dao.findPage(0, 10, "parentObjectId", "ASC");
    }

    @Test
    public void findPage_breakOnUnknownProperty() throws Exception
    {
        try
        {
            dao.findPage(0, 10, "parentObjectId; DELETE * FROM ACM_AUDIT_LOG;", "ASC");
            fail("Should have thrown an exception on unsanitized input");
        }
        catch (IllegalArgumentException e)
        {
            log.info("Got the expected exception [{}]", e.getClass().getName());
        }
    }

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

    @Test
    public void getCountAuditEventSince()
    {
        Long countSince = dao.getCountAuditEventSince("com.armedia.acm.login", LocalDateTime.now().minus(7, ChronoUnit.DAYS),
                LocalDateTime.now());
        assertNotNull(countSince);
    }
}
