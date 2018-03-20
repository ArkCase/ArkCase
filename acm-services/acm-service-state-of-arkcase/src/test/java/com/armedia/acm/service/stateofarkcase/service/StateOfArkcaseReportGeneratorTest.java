package com.armedia.acm.service.stateofarkcase.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-object-converter.xml",
        "classpath:/spring/spring-library-state-of-arkcase-test.xml"
})
public class StateOfArkcaseReportGeneratorTest
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ObjectMapper sourceObjectMapper;
    @Autowired
    private StateOfArkcaseReportGenerator stateOfArkcaseReportGenerator;

    @Test
    public void generateReportAsJSON() throws IOException
    {
        assertNotNull(stateOfArkcaseReportGenerator);
        String reportJsonString = stateOfArkcaseReportGenerator.generateReportAsJSON();
        log.info(reportJsonString);
        JsonNode report = sourceObjectMapper.readTree(reportJsonString);
        assertNotNull(report.get("dateGenerated"));
        JsonNode testModuleUsersJsonNode = report.get("test_module_users");
        assertNotNull(testModuleUsersJsonNode);
        assertEquals(1, testModuleUsersJsonNode.get("addedNewUsers").intValue());
        assertEquals(2, testModuleUsersJsonNode.get("removedUsers").intValue());
        assertEquals(36, testModuleUsersJsonNode.get("weekOfReport").intValue());
        assertEquals(5, testModuleUsersJsonNode.get("numberOfUsers").intValue());

    }
}