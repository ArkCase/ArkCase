package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.objectonverter.json.validator.JsonSchemaValidator;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.junit.Assert.*;


public class EcmFileJsonSchemaValidTest
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private JsonSchemaValidator jsonSchemaValidator;

    @Before
    public void setUp() throws Exception
    {
        jsonSchemaValidator = new JsonSchemaValidator();
    }

    @Test
    public void validateSchemas() throws Exception
    {
        Resource schemaResource = new ClassPathResource("/jsonSchemas/ecm-file-schema.json");
        ProcessingReport report = jsonSchemaValidator.validate(schemaResource.getFile());

        log.debug("Schema Report: " + report);

        // any warning messages will appear in the report.toString, but report.isSuccess() will still
        // be true; so to verify there are no warnings, we have to examine the report.toString itself
        assertTrue(report.toString().trim().endsWith("ListProcessingReport: success"));
    }


}
