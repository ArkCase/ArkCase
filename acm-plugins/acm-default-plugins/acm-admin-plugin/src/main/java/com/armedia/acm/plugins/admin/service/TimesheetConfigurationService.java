package com.armedia.acm.plugins.admin.service;

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

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.admin.model.TimesheetConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TimesheetConfigurationService
{

    private Logger log = LoggerFactory.getLogger(getClass());
    private Resource timesheetResource;
    private ObjectConverter objectConverter;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public TimesheetConfig getConfig()
    {
        String currentLine;
        String timesheetConfigJson = "";
        TimesheetConfig timesheetConfig = null;

        try (FileReader fileReader = new FileReader(getTimesheetResource().getFile());
                BufferedReader bufferedReader = new BufferedReader(fileReader))
        {
            log.info("Trying to read from config file: " + getTimesheetResource().getFile().getAbsolutePath());

            lock.readLock().lock();

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

        try (FileWriter fileWriter = new FileWriter(getTimesheetResource().getFile(), false))
        {
            log.info("Trying to write to config file: " + getTimesheetResource().getFile().getAbsolutePath());
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

    public Resource getTimesheetResource()
    {
        return timesheetResource;
    }

    public void setTimesheetResource(Resource timesheetResource)
    {
        this.timesheetResource = timesheetResource;
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
