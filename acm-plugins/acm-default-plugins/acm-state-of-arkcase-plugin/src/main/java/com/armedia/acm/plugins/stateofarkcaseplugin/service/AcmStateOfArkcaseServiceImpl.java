package com.armedia.acm.plugins.stateofarkcaseplugin.service;

/*-
 * #%L
 * ACM Plugins: Plugin State of Arkcase
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

import com.armedia.acm.core.exceptions.AcmStateOfArkcaseGenerateReportException;
import com.armedia.acm.service.stateofarkcase.exceptions.ErrorLogFileException;
import com.armedia.acm.service.stateofarkcase.exceptions.StateOfArkcaseReportException;
import com.armedia.acm.service.stateofarkcase.model.StateOfArkcase;
import com.armedia.acm.service.stateofarkcase.service.ErrorsLogFileService;
import com.armedia.acm.service.stateofarkcase.service.StateOfArkcaseReportGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
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
    private Logger log = LogManager.getLogger(getClass());
    private StateOfArkcaseReportGenerator stateOfArkcaseReportGenerator;
    private ErrorsLogFileService errorsLogFileService;
    private int deleteReportsOlderThanDays = 7;
    private ObjectMapper objectMapper;

    /**
     * Generates state of Arkcase report and save is provided folder
     */
    @Override
    public File generateReportForDay(LocalDate day) throws AcmStateOfArkcaseGenerateReportException
    {
        final String errorMessage = "TempFile creation failed";
        if (LocalDate.now().isBefore(day))
        {
            // no report for tomorrow
            throw new StateOfArkcaseReportException("Can't generate report for day in future.");

        }
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
            throw new AcmStateOfArkcaseGenerateReportException(errorMessage);
        }

        File errorsLogFile = null;
        try
        {
            errorsLogFile = getErrorsLogFile(day);
        }
        catch (ErrorLogFileException e)
        {
            log.error("Error retrieving errors log file.", e.getMessage());
            throw new AcmStateOfArkcaseGenerateReportException(errorMessage);
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
                if (LocalDate.now().equals(day))
                {
                    // log file for today is not complete and needs to be closed in order json to be valid
                    // errors log file input stream, append closing brackets to the json and combine both input streams
                    try (InputStream fis = new FileInputStream(errorsLogFile);
                            InputStream bais = new ByteArrayInputStream("]".getBytes());
                            SequenceInputStream sis = new SequenceInputStream(fis, bais))
                    {
                        Files.copy(sis, zipfs.getPath(errorsLogName),
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                else
                {
                    try (InputStream fis = new FileInputStream(errorsLogFile))
                    {
                        Files.copy(fis, zipfs.getPath(errorsLogName),
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("Error creating state of arkcase dally report.", e.getMessage());
            throw new AcmStateOfArkcaseGenerateReportException(errorMessage);
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
