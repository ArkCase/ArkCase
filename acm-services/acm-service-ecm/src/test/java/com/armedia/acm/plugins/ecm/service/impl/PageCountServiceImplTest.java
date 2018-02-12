package com.armedia.acm.plugins.ecm.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Created by armdev on 3/11/15.
 */
public class PageCountServiceImplTest
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
        Resource classPathResource = new ClassPathResource("adobe/oversized.pdf");

        String mimeType = "application/pdf";
        int numberOfPages = unit.getNumberOfPages(mimeType, classPathResource.getFile());
        LOGGER.debug("Number of pages found: [{}]", numberOfPages);
    }

}
