package com.armedia.acm.tool.zylab.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
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
        String extractedFolderName = fileToUnzip.getName().split("\\.")[0] + "_unzipped";
        String extractedDirectoryPath = fileToUnzip.getParentFile().getAbsolutePath() + File.separator + extractedFolderName;
        log.info("Extracting file: " + fileToUnzip.getName());
        ZipFile zipFile = new ZipFile(fileToUnzip);

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
        return new File(extractedDirectoryPath);
    }

    private static void extractZipEntry(ZipFile zipFile, ZipEntry entry, String destPath) throws IOException
    {

        try (InputStream inputStream = zipFile.getInputStream(entry);
                FileOutputStream outputStream = new FileOutputStream(destPath))
        {
            int data = inputStream.read();
            while (data != -1)
            {
                outputStream.write(data);
                data = inputStream.read();
            }
        }
    }

}