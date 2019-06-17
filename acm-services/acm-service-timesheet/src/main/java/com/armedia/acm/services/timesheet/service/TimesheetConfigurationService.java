package com.armedia.acm.services.timesheet.service;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.timesheet.model.TimesheetConfig;
import com.armedia.acm.services.timesheet.model.TimesheetConfigDTO;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TimesheetConfigurationService
{
    private Logger log = LogManager.getLogger(getClass());

    private String configurationFile;
    private ObjectConverter objectConverter;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private TimesheetConfig timesheetConfig;
    private ConfigurationPropertyService configurationPropertyService;

    public TimesheetConfigDTO getConfig()
    {
        String currentLine;
        StringBuilder timesheetConfigJson = new StringBuilder();
        TimesheetConfigDTO timesheetConfig = null;

        try (Reader fileReader = new FileReader(new File(getConfigurationFile()));
                BufferedReader bufferedReader = new BufferedReader(fileReader))
        {
            log.info("Trying to read from config file: " + getConfigurationFile());

            lock.readLock().lock();

            while ((currentLine = bufferedReader.readLine()) != null)
            {
                timesheetConfigJson.append(currentLine);
            }

            fileReader.close();
            timesheetConfig = getObjectConverter().getJsonUnmarshaller().unmarshall(timesheetConfigJson.toString(),
                    TimesheetConfigDTO.class);
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
        finally
        {
            lock.readLock().unlock();
        }

        return timesheetConfig;
    }

    public void saveConfig(TimesheetConfigDTO config)
    {
        String timesheetConfigJson = Objects.nonNull(config) ? getObjectConverter().getJsonMarshaller().marshal(config) : "";

        try (FileWriter fileWriter = new FileWriter(new File(getConfigurationFile()), false))
        {
            log.info("Trying to write to config file [{}]", getConfigurationFile());
            lock.writeLock().lock();
            fileWriter.write(timesheetConfigJson);
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    public void saveProperties(TimesheetConfig timesheetConfig)
    {
        configurationPropertyService.updateProperties(timesheetConfig);
    }

    public TimesheetConfig loadProperties()
    {
        return timesheetConfig;
    }

    public String getConfigurationFile()
    {
        return configurationFile;
    }

    public void setConfigurationFile(String configurationFile)
    {
        this.configurationFile = configurationFile;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public TimesheetConfig getTimesheetConfig()
    {
        return timesheetConfig;
    }

    public void setTimesheetConfig(TimesheetConfig timesheetConfig)
    {
        this.timesheetConfig = timesheetConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }
}
