package com.armedia.acm.services.config.service;

/*-
 * #%L
 * ACM Service: Config
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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.services.config.model.AcmConfig;
import com.armedia.acm.services.config.model.AppConfig;
import com.armedia.acm.services.config.model.JsonConfig;
import com.armedia.acm.services.config.model.PropertyConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConfigServiceTest extends EasyMockSupport
{
    private List<AcmConfig> mockConfigList;
    private AppConfig mockConfig1;
    private PropertyConfig mockConfig2;
    private PropertyConfig mockConfig3;

    private ConfigService unit;

    @Before
    public void setUp() throws Exception
    {
        mockConfig1 = createMock(AppConfig.class);
        mockConfig2 = createMock(PropertyConfig.class);
        mockConfig3 = createMock(PropertyConfig.class);

        mockConfigList = new ArrayList<>();
        mockConfigList.add(mockConfig1);
        mockConfigList.add(mockConfig2);
        mockConfigList.add(mockConfig3);

        unit = new ConfigService();
        unit.setConfigList(mockConfigList);
    }

    @Test
    public void getConfig1() throws Exception
    {
        // given
        expect(mockConfig1.getConfigName()).andReturn("config1");
        expect(mockConfig1.getConfigAsJson()).andReturn("{\"some1\":\"value1\"}");

        // when
        replayAll();
        String returned = unit.getConfigAsJson("config1");

        // then
        verifyAll();

        assertNotNull(returned);

        ObjectMapper om = new ObjectMapper();
        JsonNode actualObj = om.readTree(returned);
        JsonNode someNode = actualObj.path("some1");
        String someValue = someNode.textValue();
        assertEquals(someValue, "value1");
    }

    @Test
    public void getConfig2() throws Exception
    {
        // given
        expect(mockConfig1.getConfigName()).andReturn("config1");
        expect(mockConfig2.getConfigName()).andReturn("config2");
        expect(mockConfig2.getConfigAsJson()).andReturn("{\"some2\":\"value2\"}");

        // when
        replayAll();
        String returned = unit.getConfigAsJson("config2");

        // then
        verifyAll();
        assertNotNull(returned);

        ObjectMapper om = new ObjectMapper();
        JsonNode actualObj = om.readTree(returned);
        JsonNode someNode = actualObj.path("some2");
        String someValue = someNode.textValue();
        assertEquals(someValue, "value2");
    }

    @Test
    public void getConfig3() throws Exception
    {
        // given
        expect(mockConfig1.getConfigName()).andReturn("config1");
        expect(mockConfig2.getConfigName()).andReturn("config2");
        expect(mockConfig3.getConfigName()).andReturn("config3");
        expect(mockConfig3.getConfigAsJson()).andReturn("{\"some3\":\"value3\"}");

        // when
        replayAll();
        String returned = unit.getConfigAsJson("config3");

        // then
        verifyAll();
        assertNotNull(returned);

        ObjectMapper om = new ObjectMapper();
        JsonNode actualObj = om.readTree(returned);
        JsonNode someNode = actualObj.path("some3");
        String someValue = someNode.textValue();
        assertEquals(someValue, "value3");
    }

    @Test
    public void getConfig4() throws Exception
    {
        // given
        expect(mockConfig1.getConfigName()).andReturn("config1");
        expect(mockConfig2.getConfigName()).andReturn("config2");
        expect(mockConfig3.getConfigName()).andReturn("config3");

        // when
        replayAll();
        String returned = unit.getConfigAsJson("config_no_such");

        // then
        verifyAll();
        assertEquals(returned, "{}");
    }

    @Test
    public void getInfo() throws Exception
    {
        // given
        AppConfig acmConfig1 = new AppConfig();
        PropertyConfig acmConfig2 = new PropertyConfig();
        JsonConfig acmConfig3 = new JsonConfig();

        acmConfig1.setConfigName("appConfigName");
        acmConfig1.setConfigDescription("appConfigDescription");
        acmConfig2.setConfigName("propertyConfigName");
        acmConfig2.setConfigDescription("propertyConfigDescription");
        acmConfig3.setConfigName("jsonConfigName");
        acmConfig3.setConfigDescription("jsonConfigDescription");

        List<AcmConfig> configList = Arrays.asList(acmConfig1, acmConfig2, acmConfig3);

        unit.setConfigList(configList);

        // when
        List<Map<String, String>> returned = unit.getInfo();

        // then
        assertNotNull(returned);

        assertNotNull(returned);
        assertEquals(acmConfig1.getConfigName(), returned.get(0).get("name"));
        assertEquals(acmConfig1.getConfigDescription(), returned.get(0).get("description"));
        assertEquals(acmConfig2.getConfigName(), returned.get(1).get("name"));
        assertEquals(acmConfig2.getConfigDescription(), returned.get(1).get("description"));
        assertEquals(acmConfig3.getConfigName(), returned.get(2).get("name"));
        assertEquals(acmConfig3.getConfigDescription(), returned.get(2).get("description"));
    }
}
