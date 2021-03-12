package com.armedia.acm.tool.zylab.service;

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