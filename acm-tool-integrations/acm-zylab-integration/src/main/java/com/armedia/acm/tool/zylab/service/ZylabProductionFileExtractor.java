package com.armedia.acm.tool.zylab.service;

/*-
 * #%L
 * Tool Integrations: Arkcase ZyLAB Integration
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on March, 2021
 */
public class ZylabProductionFileExtractor
{
    private final static Logger log = LogManager.getLogger(ZylabProductionFileExtractor.class);

    public static File unzip(File fileToUnzip) throws IOException
    {
        String extractedDirectoryPath = fileToUnzip.getAbsolutePath().replace(".zip", "_unzipped");
        log.info("Extracting file: " + fileToUnzip.getName());
        try (ZipFile zipFile = new ZipFile(fileToUnzip))
        {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();
                String destPath = extractedDirectoryPath + File.separator + entry.getName();
                File destinationFile = new File(destPath);

                if (entry.isDirectory())
                {
                    FileUtils.forceMkdir(destinationFile);
                    log.info("Folder entry extracted: " + entry.getName());
                }
                else
                {
                    FileUtils.forceMkdir(destinationFile.getParentFile());
                    extractZipEntry(zipFile, entry, destPath);
                    log.info("File entry extracted: " + entry.getName());
                }
            }
        }
        return new File(extractedDirectoryPath);
    }

    private static void extractZipEntry(ZipFile zipFile, ZipEntry entry, String destPath) throws IOException
    {

        try (BufferedInputStream inputStream = new BufferedInputStream(zipFile.getInputStream(entry));
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destPath)))
        {
            IOUtils.copyLarge(inputStream, out);
        }
    }

}
