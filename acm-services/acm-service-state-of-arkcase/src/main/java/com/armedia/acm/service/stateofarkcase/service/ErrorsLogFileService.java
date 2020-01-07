package com.armedia.acm.service.stateofarkcase.service;

/*-
 * #%L
 * ACM Service: State of Arkcase
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

import com.armedia.acm.service.stateofarkcase.exceptions.ErrorLogFileException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.PatternProcessor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.annotation.PostConstruct;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Service for retrieving arkcase-errors log files.
 */
public class ErrorsLogFileService
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private String appenderName;
    private LoggerContext ctx;
    private String reason;
    private RollingFileAppender errorsLogAppender;

    /**
     * 
     * @param appenderName
     *            name of the appender of type RollingFileAppender
     */
    ErrorsLogFileService(String appenderName)
    {
        this.appenderName = appenderName;
        ctx = (LoggerContext) LogManager.getContext();
    }

    @PostConstruct
    private void init()
    {
        if (appenderName == null)
        {
            reason = "Appender name is not provided";
            log.warn("ErrorsLogFileService won't work unless appender name from existing appender is provided.");
            return;
        }
        Map<String, Appender> appenders = ctx.getConfiguration().getAppenders();
        if (appenders.containsKey(appenderName) && (appenders.get(appenderName) instanceof RollingFileAppender))
        {
            errorsLogAppender = (RollingFileAppender) appenders.get("file-errors-log");
        }
        else
        {
            reason = appenders.containsKey(appenderName) ? "Appender with name [" + appenderName + "] is not found."
                    : "Appender with name [" + appenderName
                            + "] is not RollingFileAppender. Make sure that appenderName is from RollingFileAppender.";
            log.warn(reason);
        }
    }

    /**
     * Returns File instance of generated error log file. If not found throws exception FileNotFound wrapped with
     * ErrorLogFileException.
     * 
     * @return File
     * @throws ErrorLogFileException
     *             if fileNameProcessor is not initialized or file not found
     */
    public File getCurrentFile() throws ErrorLogFileException
    {
        File file = new File(getCurrentFileName());
        if (file.exists())
        {
            return file;
        }
        else
        {
            throw new ErrorLogFileException(new FileNotFoundException());
        }
    }

    /**
     * Returns File instance of generated error log file. If not found throws exception FileNotFound wrapped with
     * ErrorLogFileException.
     * 
     * @param days
     *            how much days in past
     * @return File
     * @throws ErrorLogFileException
     *             if filePatternProcessor is not initialized or file not found
     */
    public File getFileInPastDays(int days) throws ErrorLogFileException
    {
        File file = new File(getFileNameInPastDays(days));
        if (file.exists())
        {
            return file;
        }
        else
        {
            throw new ErrorLogFileException(new FileNotFoundException());
        }
    }

    /**
     * Returns absolute File name
     *
     * @param days
     *            how much days in past. Zero(0) is last day.
     * @return File
     * @throws ErrorLogFileException
     *             if filePatternProcessor is not initialized
     */
    public String getFileNameInPastDays(int days) throws ErrorLogFileException
    {
        // PatternProcessor calculates days minus 1.
        // Example if we provide 0 days in past, gives file name pattern for previous day.
        // because of above we need to adjust days
        days--;
        days = days * -1;
        PatternProcessor filePatternProcessor = new PatternProcessor(errorsLogAppender.getFilePattern());
        if (filePatternProcessor == null || errorsLogAppender == null)
        {
            throw new ErrorLogFileException("FileNameProcessor is not initialized. Reason: " + reason);
        }
        StringBuilder fileName = new StringBuilder();
        filePatternProcessor.getNextTime(System.currentTimeMillis(), days, false);
        filePatternProcessor.updateTime();
        filePatternProcessor.formatFileName(fileName, false, new Object[] {});
        return fileName.toString();
    }

    /**
     * Returns File name
     *
     * @return File
     * @throws ErrorLogFileException
     *             if fileNameProcessor is not initialized
     */
    public String getCurrentFileName() throws ErrorLogFileException
    {
        PatternProcessor fileNameProcessor = new PatternProcessor(errorsLogAppender.getFileName());
        if (fileNameProcessor == null || errorsLogAppender == null)
        {
            throw new ErrorLogFileException("FileNameProcessor is not initialized. Reason: " + reason);
        }
        StringBuilder fileName = new StringBuilder();
        fileNameProcessor.formatFileName(fileName, true, new Object[] {});
        return fileName.toString();
    }
}
