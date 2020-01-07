package com.armedia.acm.plugins.ecm.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import static org.junit.Assert.assertTrue;

import com.armedia.acm.objectonverter.json.validator.JsonSchemaValidator;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import org.junit.Before;
import org.junit.Test;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

public class EcmFileJsonSchemaValidTest
{

    private transient final Logger log = LogManager.getLogger(getClass());

    private JsonSchemaValidator jsonSchemaValidator;

    @Before
    public void setUp() throws Exception
    {
        jsonSchemaValidator = new JsonSchemaValidator();
    }

    @Test
    public void validateEcmFileSchema() throws Exception
    {
        validate("/jsonSchemas/ecm-file-schema.json");
    }

    @Test
    public void validateFolderListSchema() throws Exception
    {
        validate("/jsonSchemas/folder-list-schema.json");
    }

    @Test
    public void validateCopyFileDtoSchema() throws Exception
    {
        validate("/jsonSchemas/copy-file-dto-schema.json");
    }

    @Test
    public void validateFolderSchema() throws Exception
    {
        validate("/jsonSchemas/folder-schema.json");
    }

    @Test
    public void validateMoveCopyFileRequestSchema() throws Exception
    {
        validate("/jsonSchemas/move-copy-file-schema.json");
    }

    @Test
    public void validateCopyFolderDtoSchema() throws Exception
    {
        validate("/jsonSchemas/copy-folder-dto-schema.json");
    }

    @Test
    public void deleteFileResultsSchema() throws Exception
    {
        validate("/jsonSchemas/delete-file-results-schema.json");
    }

    @Test
    public void deleteFolderResultsSchema() throws Exception
    {
        validate("/jsonSchemas/delete-folder-results-schema.json");
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
