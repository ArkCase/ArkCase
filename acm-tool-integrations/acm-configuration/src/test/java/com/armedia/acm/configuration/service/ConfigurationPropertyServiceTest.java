package com.armedia.acm.configuration.service;

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
