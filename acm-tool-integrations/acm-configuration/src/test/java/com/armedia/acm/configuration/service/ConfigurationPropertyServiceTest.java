package com.armedia.acm.configuration.service;

/*-
 * #%L
 * ACM Tool Integrations: Configuration Library
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import com.armedia.acm.core.model.ApplicationConfig;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ConfigurationPropertyServiceTest
{
    ConfigurationPropertyService service = new ConfigurationPropertyService(null, null,
            null , null, null);

    @Test
    public void testGetObjectDynamicProperties()
    {
        String propertyName = "application.properties.organizationName";
        String propertyValue = "organizationName_Value";
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setOrganizationName(propertyValue);

        Map<String, Object> properties = service.getObjectDynamicProperties(applicationConfig);

        assertTrue(properties.containsKey(propertyName));
        assertEquals(propertyValue, properties.get(propertyName));

        String nullProperty = "application.properties.organizationCity";
        assertTrue(properties.containsKey(nullProperty));
        assertNull(properties.get(nullProperty));

        String unmodifiableProperty = "application.properties.baseUrl";
        assertFalse(properties.containsKey(unmodifiableProperty));
    }
}
