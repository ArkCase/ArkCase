package com.armedia.acm.plugins.ecm.utils;

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

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by joseph.mcgrady on 9/14/2015.
 */
public class PDFUtils
{
    /**
     * Use no more than 32MB of main memory when merging PDFs, the disk is used for the rest.
     */
    public static final int MAX_MAIN_MEMORY_BYTES = 1024 * 1024 * 32;
    private static transient final Logger log = LoggerFactory.getLogger(PDFUtils.class);

    /**
     * Merges together the two supplied PDF documents
     *
     * @param originalFileStream
     *            - this PDF will be first in the combined document
     * @param newFileStream
     *            - this PDF will be appended to the end of the original document
     */
    public static byte[] mergeFiles(InputStream originalFileStream, InputStream newFileStream)
    {
        byte[] mergedDocument = null;
        try
        {
            log.debug("Attempting to merge");

            // The original file data is first and the new file data is merged at the end (appended)
            PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
            pdfMergerUtility.addSource(originalFileStream);
            pdfMergerUtility.addSource(newFileStream);

            // Merges the documents together and creates an in-memory copy of the new combined document
            ByteArrayOutputStream outputStreamBytes = new ByteArrayOutputStream();
            pdfMergerUtility.setDestinationStream(outputStreamBytes);
            // using at most 32MB memory, the rest goes to disk
            MemoryUsageSetting memoryUsageSetting = MemoryUsageSetting.setupMixed(MAX_MAIN_MEMORY_BYTES);
            pdfMergerUtility.mergeDocuments(memoryUsageSetting);

            mergedDocument = outputStreamBytes.toByteArray();
            log.debug("merged length: " + mergedDocument.length);

        }
        catch (Exception e)
        {
            log.error("pdf merge failed: {}", e.getMessage(), e);
        }
        return mergedDocument;
    }
}
