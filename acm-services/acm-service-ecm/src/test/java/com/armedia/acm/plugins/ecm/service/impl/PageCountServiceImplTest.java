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

import org.junit.Before;
import org.junit.Test;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

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

        String mimeType = "application/pdf";
        int numberOfPages = unit.getNumberOfPages(mimeType, classPathResource.getFile());
        LOGGER.debug("Number of pages found: [{}]", numberOfPages);
    }

}
