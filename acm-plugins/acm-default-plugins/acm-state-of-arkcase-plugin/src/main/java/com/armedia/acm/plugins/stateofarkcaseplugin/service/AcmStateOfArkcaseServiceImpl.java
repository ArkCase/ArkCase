package com.armedia.acm.plugins.stateofarkcaseplugin.service;

import com.armedia.acm.service.stateofarkcase.model.StateOfArkcase;
import com.armedia.acm.service.stateofarkcase.service.ErrorsLogFileService;
import com.armedia.acm.service.stateofarkcase.service.StateOfArkcaseReportGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AcmStateOfArkcaseServiceImpl implements AcmStateOfArkcaseService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private String stateOfArkcaseFolderPath;
    private StateOfArkcaseReportGenerator stateOfArkcaseReportGenerator;
    private ErrorsLogFileService errorsLogFileService;
    private int deleteReportsOlderThanDays = 7;
    private ObjectMapper objectMapper;

    /**
     * Generates state of Arkcase report and save is provided folder
     */
    @Override
    public void generateDailyReport()
    {
        StateOfArkcase stateOfArkcase = stateOfArkcaseReportGenerator.generateReport();

        LocalDate now = LocalDate.now();
        String stateReportName = String.format("state-of-arkcase-%s", now.format(DateTimeFormatter.ISO_DATE));
        String errorsLogName = String.format("errors-log-%s.txt", now.format(DateTimeFormatter.ISO_DATE));

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        // locate file system by using the syntax
        // defined in java.net.JarURLConnection
        URI uri = URI.create(String.format("jar:file:/%s/%s.zip", stateOfArkcaseFolderPath, stateReportName));
        File zipFile = new File(String.format("%s/%s.zip", stateOfArkcaseFolderPath, stateReportName));
        // Path zipfilePath = zipFile.toPath()
        try (FileSystem zipfs = FileSystems.newFileSystem(zipFile.toURI(), env))
        {
            // create byte array input stream from report
            ByteArrayInputStream stateReportBytesStream = new ByteArrayInputStream(objectMapper.writeValueAsBytes(stateOfArkcase));

            // copy a state of arkcase report file
            Files.copy(stateReportBytesStream, Paths.get("/" + stateReportName),
                    StandardCopyOption.REPLACE_EXISTING);
            // TODO check if already exist file for previous day
            File errorsLogFile = errorsLogFileService.getCurrentFile();
            // copy error log
            Files.copy(errorsLogFile.toPath(), Paths.get("/" + errorsLogName),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e)
        {
            log.error("Error creating state of arkcase dally report.", e);
        }
    }

    public void setStateOfArkcaseReportGenerator(StateOfArkcaseReportGenerator stateOfArkcaseReportGenerator)
    {
        this.stateOfArkcaseReportGenerator = stateOfArkcaseReportGenerator;
    }

    public void setStateOfArkcaseFolderPath(String stateOfArkcaseFolderPath)
    {
        this.stateOfArkcaseFolderPath = stateOfArkcaseFolderPath;
        File reportDirectory = new File(stateOfArkcaseFolderPath);
        if (!reportDirectory.exists())
        {
            reportDirectory.mkdirs();
        }
    }

    public void setDeleteReportsOlderThanDays(int deleteReportsOlderThanDays)
    {
        this.deleteReportsOlderThanDays = deleteReportsOlderThanDays;
    }

    public void setErrorsLogFileService(ErrorsLogFileService errorsLogFileService)
    {
        this.errorsLogFileService = errorsLogFileService;
    }

    public void setObjectMapper(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }
}
