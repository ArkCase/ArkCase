package com.armedia.acm.plugins.ecm.service.impl;

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

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StopWatch;

import java.io.IOException;

/**
 * Created by armdev on 3/11/15.
 */
public class PageCountServiceImplTest
{
    private static final Logger LOGGER = LogManager.getLogger(PageCountServiceImplTest.class);
    private PageCountServiceImpl unit;

    @Before
    public void setUp() throws Exception
    {
        unit = new PageCountServiceImpl();
    }

    @Test
    public void findPageCountForLargePdf() throws Exception
    {
        Resource classPathResource = new ClassPathResource("adobe/oversized.pdf");
        String MIME_TYPE_PDF = "application/pdf";
        int numberOfPagesExpected = 41944;
        int numberOfPagesActual = findNumberOfPagesAndLogTime(classPathResource, MIME_TYPE_PDF);

        assertEquals(numberOfPagesExpected, numberOfPagesActual);
    }

    @Test
    public void findPageCountForDocFile() throws Exception
    {
        Resource classPathResource = new ClassPathResource("office/Testdoc.doc");
        String MIME_TYPE_DOC = "application/msword";
        int numberOfPagesExpected = 1;
        int numberOfPagesActual = findNumberOfPagesAndLogTime(classPathResource, MIME_TYPE_DOC);

        assertEquals(numberOfPagesExpected, numberOfPagesActual);
    }

    @Test
    public void findPageCountForDocxFile() throws Exception
    {
        Resource classPathResource = new ClassPathResource("office/hds.docx");
        String MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        int numberOfPagesExpected = 145;
        int numberOfPagesActual = findNumberOfPagesAndLogTime(classPathResource, MIME_TYPE_DOCX);

        assertEquals(numberOfPagesExpected, numberOfPagesActual);
    }

    @Test
    public void findPageCountForPptxFile() throws Exception
    {
        Resource classPathResource = new ClassPathResource("office/slideshow.pptx");
        String MIME_TYPE_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        int numberOfPagesExpected = 4;
        int numberOfPagesActual = findNumberOfPagesAndLogTime(classPathResource, MIME_TYPE_PPTX);

        assertEquals(numberOfPagesExpected, numberOfPagesActual);
    }

    private int findNumberOfPagesAndLogTime(Resource classPathResource, String mimeType) throws IOException
    {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        int numberOfPagesActual = unit.getNumberOfPages(mimeType, classPathResource.getFile());

        stopWatch.stop();

        LOGGER.debug("Number of pages found: [{}]", numberOfPagesActual);
        LOGGER.debug("Time needed to find the pages: [{}]", stopWatch.getTotalTimeSeconds());
        return numberOfPagesActual;
    }
}
