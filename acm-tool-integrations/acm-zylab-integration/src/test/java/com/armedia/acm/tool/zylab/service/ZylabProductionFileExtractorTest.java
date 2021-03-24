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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.junit.After;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class ZylabProductionFileExtractorTest
{

    @After
    public void cleanUp() throws Exception
    {
        ClassPathResource uncompressedFolderResource = new ClassPathResource("ProductionFiles_unzipped");
        assertTrue(uncompressedFolderResource.exists());

        FileUtils.deleteDirectory(uncompressedFolderResource.getFile());
    }

    @Test
    public void testUnzip() throws IOException
    {
        ClassPathResource compressedFileResource = new ClassPathResource("ProductionFiles.zip");
        assertTrue(compressedFileResource.exists());

        ClassPathResource extractedDirectoryResource = new ClassPathResource("ProductionFiles");
        assertTrue(extractedDirectoryResource.exists());

        File unzipDir = ZylabProductionFileExtractor.unzip(compressedFileResource.getFile());

        List<File> uncompressedFiles = (List<File>) FileUtils.listFiles(unzipDir, FileFilterUtils.trueFileFilter(),
                FileFilterUtils.trueFileFilter());
        List<File> directoryFiles = (List<File>) FileUtils.listFiles(extractedDirectoryResource.getFile(), FileFilterUtils.trueFileFilter(),
                FileFilterUtils.trueFileFilter());

        assertEquals("Number of files should be same", uncompressedFiles.size(), directoryFiles.size());

        for (int i = 0; i < uncompressedFiles.size(); i++)
        {
            File uncompressedFile = uncompressedFiles.get(i);
            File directoryFile = directoryFiles.get(i);

            assertEquals("File name not equal", uncompressedFile.getName(), directoryFile.getName());
            assertEquals("File size not equal", uncompressedFile.length(), directoryFile.length());
            assertTrue("File contents not equal", FileUtils.contentEquals(uncompressedFile, directoryFile));
        }
    }
}
