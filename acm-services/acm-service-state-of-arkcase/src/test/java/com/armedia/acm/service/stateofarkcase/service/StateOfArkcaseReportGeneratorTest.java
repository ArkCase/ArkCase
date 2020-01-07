package com.armedia.acm.service.stateofarkcase.service;

/*-
 * #%L
 * ACM Service: State of Arkcase
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-object-converter.xml",
        "classpath:/spring/spring-library-state-of-arkcase-test.xml"
})
public class StateOfArkcaseReportGeneratorTest
{
    private transient final Logger log = LogManager.getLogger(getClass());

    @Autowired
    ObjectMapper sourceObjectMapper;
    @Autowired
    private StateOfArkcaseReportGenerator stateOfArkcaseReportGenerator;

    @Test
    public void generateReportAsJSON()
    {
        assertNotNull(stateOfArkcaseReportGenerator);
        JsonNode reportJson = stateOfArkcaseReportGenerator.generateReportAsJSON(LocalDate.now());

        assertNotNull(reportJson.get("dateGenerated"));
        JsonNode testModuleUsersJsonNode = reportJson.get("test_module_users");
        assertNotNull(testModuleUsersJsonNode);
        assertEquals(1, testModuleUsersJsonNode.get("addedNewUsers").intValue());
        assertEquals(2, testModuleUsersJsonNode.get("removedUsers").intValue());
        assertEquals(36, testModuleUsersJsonNode.get("weekOfReport").intValue());
        assertEquals(5, testModuleUsersJsonNode.get("numberOfUsers").intValue());

    }
}
