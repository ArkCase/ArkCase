package com.armedia.acm.plugins.outlook.model;

import static org.junit.Assert.assertTrue;

import com.armedia.acm.objectonverter.json.validator.JsonSchemaValidator;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

public class MsOutlookJsonSchemaValidTest
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private JsonSchemaValidator jsonSchemaValidator;

    @Before
    public void setUp() throws Exception
    {
        jsonSchemaValidator = new JsonSchemaValidator();
    }

    @Test
    public void validateInboxSchema() throws Exception
    {
        validate("/jsonSchemas/outlook-inbox-schema.json");
    }

    @Test
    public void validateCalendarSchema() throws Exception
    {
        validate("/jsonSchemas/outlook-appointment-schema.json");
        validate("/jsonSchemas/outlook-calendar-schema.json");
    }

    @Test
    public void validateContactsSchema() throws Exception
    {
        validate("/jsonSchemas/outlook-contact-schema.json");
        validate("/jsonSchemas/outlook-contacts-schema.json");
    }

    @Test
    public void validateTasksSchema() throws Exception
    {
        validate("/jsonSchemas/outlook-task-schema.json");
        validate("/jsonSchemas/outlook-tasks-schema.json");
    }

    private void validate(String path) throws IOException, ProcessingException
    {
        Resource schemaResource = new ClassPathResource(path);
        ProcessingReport report = jsonSchemaValidator.validate(schemaResource.getFile());

        log.debug("Schema Report: " + report);

        // any warning messages will appear in the report.toString, but report.isSuccess() will still
        // be true; so to verify there are no warnings, we have to examine the report.toString itself
        assertTrue(report.toString().trim().endsWith("ListProcessingReport: success"));
    }

}
