package com.armedia.acm.plugins.ecm.service.impl;

import org.apache.commons.io.IOUtils;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by armdev on 3/11/15.
 */
public class PageCountServiceImplTest extends EasyMockSupport
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PageCountServiceImplTest.class);
    private PageCountServiceImpl unit;

    @Before
    public void setUp() throws Exception
    {
        unit = new PageCountServiceImpl();
    }

    @Test
    public void findPageCountForLargePdf() throws Exception
    {
        ClassPathResource classPathResource = new ClassPathResource("adobe/oversized.pdf");

        String mimeType = "application/pdf";
        try (InputStream is = classPathResource.getInputStream())
        {
            byte[] bytes = IOUtils.toByteArray(is);
            int numberOfPages = unit.getNumberOfPages(mimeType, bytes);
            LOGGER.debug("Number of pages found: [{}]", numberOfPages);
        }
        catch (IOException | IllegalArgumentException e)
        {
            LOGGER.error("Failed to find pageCount but shouldn't throw exception", e);
            throw e;
        }

    }

}
