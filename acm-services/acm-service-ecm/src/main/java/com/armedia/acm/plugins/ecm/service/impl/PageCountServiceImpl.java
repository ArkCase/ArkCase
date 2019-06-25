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

import com.armedia.acm.plugins.ecm.service.PageCountService;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PageCountServiceImpl implements PageCountService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private MemoryUsageSetting mixedMemoryAndTempFile = MemoryUsageSetting.setupMixed(1024 * 1024).setTempDir(
            new File(System.getProperty("java.io.tmpdir")));

    @Override
    @Deprecated
    /**
     * @deprecated use getNumberOfPages(String, File)
     */
    public int getNumberOfPages(String mimeType, byte[] data) throws IOException
    {
        File file = null;
        try
        {
            file = File.createTempFile("arkcase-get-number-of-pages-", null);
            FileUtils.writeByteArrayToFile(file, data);
            return getNumberOfPages(mimeType, file);
        }
        finally
        {
            FileUtils.deleteQuietly(file);
        }
    }

    @Override
    public int getNumberOfPages(String mimeType, File file) throws IOException
    {
        int numberOfPages = -1;
        if ("application/pdf".equals(mimeType))
        {
            try (PDDocument pdDocument = PDDocument.load(new FileInputStream(file), mixedMemoryAndTempFile))
            {
                numberOfPages = pdDocument.getNumberOfPages();
            }
            catch (Exception e)
            {
                log.warn("Failed to find number of pages for PDF document", e);
            }
        }
        else
        {
            log.warn("Still don't know how to retrieve the page count for [{}] mime type", mimeType);
        }
        return numberOfPages;
    }

}
