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

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.timesheet.model.TimesheetConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TimesheetConfigurationService
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private String configurationFile;
    private String propertiesFile;

    private PropertyFileManager propertyFileManager;
    private ObjectConverter objectConverter;

    private FileWriter fileWriter = null;
    private FileReader fileReader = null;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public TimesheetConfig getConfig()
    {
        String currentLine;
        String timesheetConfigJson = "";
        TimesheetConfig timesheetConfig = null;

        try
        {
            log.info("Trying to read from config file: " + getConfigurationFile());

            lock.readLock().lock();
            fileReader = new FileReader(new File(getConfigurationFile()));
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((currentLine = bufferedReader.readLine()) != null)
            {
                timesheetConfigJson += currentLine;
            }

            fileReader.close();
            timesheetConfig = getObjectConverter().getJsonUnmarshaller().unmarshall(timesheetConfigJson, TimesheetConfig.class);
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

    public void saveConfig(TimesheetConfig config)
    {
        String timesheetConfigJson = Objects.nonNull(config) ? getObjectConverter().getJsonMarshaller().marshal(config) : "";

        try
        {
            log.info("Trying to write to config file: " + getConfigurationFile());
            lock.writeLock().lock();
            fileWriter = new FileWriter(new File(getConfigurationFile()), false);
            fileWriter.write(timesheetConfigJson);
            fileWriter.close();
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

    public void  saveProperties(Map<String, String> properties)
    {
        getPropertyFileManager().storeMultiple(properties, getPropertiesFile(), true);
    }

    public Map<String, String> loadProperties() throws IOException
    {
        Map<String, String> propertiesMap = new HashMap<>();

        Properties properties =  getPropertyFileManager().readFromFile(new File(getPropertiesFile()));
        properties.forEach((o, o2) -> propertiesMap.put((String)o, (String)o2));

        return propertiesMap;
    }

    public String getConfigurationFile()
    {
        return configurationFile;
    }

    public void setConfigurationFile(String configurationFile)
    {
        this.configurationFile = configurationFile;
    }

    public String getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile)
    {
        this.propertiesFile = propertiesFile;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }
}
