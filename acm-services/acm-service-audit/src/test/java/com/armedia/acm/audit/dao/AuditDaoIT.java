package com.armedia.acm.audit.dao;

import com.armedia.acm.audit.model.AuditEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml"
})
public class AuditDaoIT
{
    @Autowired
    private AuditDao dao;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void findEvents() throws Exception
    {
        String objectType = "DASHBOARD";
        Long dashboardId = 500L;   // just want to make sure the query executes, doesn't have to return data

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
        Long objectId = 100L;   // just want to make sure the query executes, doesn't have to return data

        List<AuditEvent> events = dao.findPagedResults(objectId,objectType,startRow,maxRows);

        assertNotNull(events);

        log.info("# of task events: " + events.size());


    }
}
