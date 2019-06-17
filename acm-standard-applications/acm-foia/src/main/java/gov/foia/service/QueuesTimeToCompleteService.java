package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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
import com.armedia.acm.services.holiday.service.HolidayConfigurationService;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import gov.foia.model.FOIAConstants;
import gov.foia.model.QueueTimeToComplete;

public class QueuesTimeToCompleteService
{

    private Resource queueConfigFile;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Logger log = LogManager.getLogger(getClass());
    private HolidayConfigurationService holidayConfigurationService;

    private ObjectConverter objectConverter;

    public void saveTimeToComplete(QueueTimeToComplete queueTimeToComplete)
    {
        String queueConfigJson = Objects.nonNull(queueTimeToComplete)
                ? getObjectConverter().getIndentedJsonMarshaller().marshal(queueTimeToComplete)
                : "{}";
        try
        {
            log.info("Trying to write to config file: {}", getQueueConfigFile().getFile().getAbsolutePath());
            lock.writeLock().lock();
            FileUtils.writeStringToFile(getQueueConfigFile().getFile(), queueConfigJson);
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

    public QueueTimeToComplete getTimeToComplete()
    {
        QueueTimeToComplete queueTimeToComplete = new QueueTimeToComplete();

        try
        {
            log.info("Trying to read from config file: {}", getQueueConfigFile().getFile().getAbsolutePath());

            lock.readLock().lock();
            String queueConfigJson = FileUtils.readFileToString(getQueueConfigFile().getFile());
            queueTimeToComplete = getObjectConverter().getJsonUnmarshaller().unmarshall(queueConfigJson, QueueTimeToComplete.class);
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
        finally
        {
            lock.readLock().unlock();
        }

        return queueTimeToComplete;

    }

    public Date addWorkingDaysToDate(Date date, String requestType)
    {
        if(date == null)
        {
            date = new Date();
        }
        switch (requestType)
        {
        case FOIAConstants.NEW_REQUEST_TYPE:
            return getHolidayConfigurationService().addWorkingDaysToDate(date,
                    getTimeToComplete().getRequest().getTotalTimeToComplete());
        case FOIAConstants.APPEAL_REQUEST_TYPE:
            return getHolidayConfigurationService().addWorkingDaysToDate(date,
                    getTimeToComplete().getAppeal().getTotalTimeToComplete());
        default:
            throw new RuntimeException("Unknown FOIA request type: " + requestType);
        }
    }

    public Resource getQueueConfigFile()
    {
        return queueConfigFile;
    }

    public void setQueueConfigFile(Resource queueConfigFile)
    {
        this.queueConfigFile = queueConfigFile;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public HolidayConfigurationService getHolidayConfigurationService()
    {
        return holidayConfigurationService;
    }

    public void setHolidayConfigurationService(HolidayConfigurationService holidayConfigurationService)
    {
        this.holidayConfigurationService = holidayConfigurationService;
    }
}
