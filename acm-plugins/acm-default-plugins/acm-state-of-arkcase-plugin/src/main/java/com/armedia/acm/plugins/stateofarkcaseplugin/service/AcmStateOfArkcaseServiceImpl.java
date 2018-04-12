package com.armedia.acm.plugins.stateofarkcaseplugin.service;

import com.armedia.acm.service.stateofarkcase.exceptions.ErrorLogFileException;
import com.armedia.acm.service.stateofarkcase.model.StateOfArkcase;
import com.armedia.acm.service.stateofarkcase.service.ErrorsLogFileService;
import com.armedia.acm.service.stateofarkcase.service.StateOfArkcaseReportGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class AcmStateOfArkcaseServiceImpl implements AcmStateOfArkcaseService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private StateOfArkcaseReportGenerator stateOfArkcaseReportGenerator;
    private ErrorsLogFileService errorsLogFileService;
    private int deleteReportsOlderThanDays = 7;
    private ObjectMapper objectMapper;

    /**
     * Generates state of Arkcase report and save is provided folder
     */
    @Override
    public File generateReportForDay(LocalDate day)
    {
        StateOfArkcase stateOfArkcase = stateOfArkcaseReportGenerator.generateReport(LocalDate.now());

        String stateReportName = String.format("state-of-arkcase-%s", day.format(DateTimeFormatter.ISO_DATE));
        String errorsLogName = String.format("errors-log-%s.json", day.format(DateTimeFormatter.ISO_DATE));

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        Path tempFile = null;
        try
        {

            tempFile = Files.createTempFile(stateReportName, ".zip");
            // files must be removed since FileSystem complains if file exists, however we will use generated file path
            // in the temp folder
            Files.deleteIfExists(tempFile);
        }
        catch (IOException e)
        {
            log.error("Error creating temp file for state of arkcase dally report.", e.getMessage());
        }

        File errorsLogFile = null;
        try
        {
            errorsLogFile = getErrorsLogFile(day);
        }
        catch (ErrorLogFileException e)
        {
            log.error("Error retrieving errors log file.", e.getMessage());
        }

        URI uri = URI.create(String.format("jar:%s", tempFile.toUri().toString()));
        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env))
        {
            // create byte array input stream from report
            ByteArrayInputStream stateReportBytesStream = new ByteArrayInputStream(objectMapper.writeValueAsBytes(stateOfArkcase));

            // copy a state of arkcase report file
            Files.copy(stateReportBytesStream, zipfs.getPath(stateReportName + ".json"),
                    StandardCopyOption.REPLACE_EXISTING);

            // copy error log if not null
            if (errorsLogFile != null)
            {
                Files.copy(errorsLogFile.toPath(), zipfs.getPath(errorsLogName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (Exception e)
        {
            log.error("Error creating state of arkcase dally report.", e.getMessage());
        }
        return tempFile.toFile();
    }

    private File getErrorsLogFile(LocalDate day) throws ErrorLogFileException
    {

        LocalDate today = LocalDate.now();
        if (day.isEqual(today))
        {
            return errorsLogFileService.getCurrentFile();
        }

        if (day.isAfter(today))
        {
            // date is in future and there is no errors logged
            return null;
        }

        long numberOfDaysInPast = ChronoUnit.DAYS.between(day, today);

        return errorsLogFileService.getFileInPastDays(Long.valueOf(numberOfDaysInPast).intValue());

    }

    public void setStateOfArkcaseReportGenerator(StateOfArkcaseReportGenerator stateOfArkcaseReportGenerator)
    {
        this.stateOfArkcaseReportGenerator = stateOfArkcaseReportGenerator;
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
