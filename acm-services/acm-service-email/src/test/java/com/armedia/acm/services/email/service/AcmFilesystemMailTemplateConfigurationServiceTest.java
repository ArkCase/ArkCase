package com.armedia.acm.services.email.service;

/*-
 * #%L
 * ACM Service: Email
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


import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 6, 2017
 */
@RunWith(MockitoJUnitRunner.class)
public class AcmFilesystemMailTemplateConfigurationServiceTest
{

    /**
     *
     */
    private static final String TEMPLATE_NAME = "testTemplate";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @InjectMocks
    private AcmFilesystemMailTemplateConfigurationService service;

    @Before
    public void setUp() throws Exception
    {
        service.setTemplateFolderPath(getTemplatesFolderPath());
    }

    @After
    public void cleanUp() throws Exception
    {
        File templatesFolder = new File(getTemplatesFolderPath());
        if (templatesFolder.exists() & templatesFolder.isDirectory())
        {
            List<Path> testFiles = Files.list(templatesFolder.toPath())
                    .filter(f -> f.getFileName().toFile().getName().startsWith(TEMPLATE_NAME)).collect(Collectors.toList());
            for (Path testFile : testFiles)
            {
                Files.delete(testFile);
            }
        }
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.service.AcmFilesystemMailTemplateConfigurationService#getTemplate(java.lang.String)}.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void testGetTemplate() throws Exception
    {
        fail("Not implemented due to interaction with the filesystem.");
    }

    private String getTemplatesFolderPath()
    {
        return System.getProperty("user.dir");
    }

}
