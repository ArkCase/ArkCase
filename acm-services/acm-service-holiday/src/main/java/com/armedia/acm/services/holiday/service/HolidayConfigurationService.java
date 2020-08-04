package com.armedia.acm.services.holiday.service;

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
import com.armedia.acm.services.holiday.model.HolidayConfiguration;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HolidayConfigurationService
{
    private Logger log = LogManager.getLogger(getClass());
    private Resource holidayFile;
    private ObjectConverter objectConverter;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private HolidayConfiguration holidayConfiguration;

    public void saveHolidayConfig(HolidayConfiguration holidayConfiguration)
    {
        String holidayConfigJson = Objects.nonNull(holidayConfiguration)
                ? getObjectConverter().getIndentedJsonMarshaller().marshal(holidayConfiguration)
                : "{}";

        try
        {
            log.info("Trying to write to config file: {}", getHolidayFile().getFile().getAbsolutePath());
            lock.writeLock().lock();
            FileUtils.writeStringToFile(getHolidayFile().getFile(), holidayConfigJson);
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
        finally
        {
            lock.writeLock().unlock();
            setHolidayConfigurationFromFile();
        }
    }

    public HolidayConfiguration getHolidayConfiguration()
    {
        if (holidayConfiguration == null)
        {
            setHolidayConfigurationFromFile();
        }
        return holidayConfiguration;
    }

    public LocalDate addWorkingDaysToDate(LocalDate date, int workingDays)
    {
        LocalDate returnDate = date;
        for (int i = 0; i < workingDays;)
        {
            returnDate = returnDate.plusDays(1);
            if (isWorkingDay(returnDate))
            {
                i++;
            }
        }
        return returnDate;
    }

    public LocalDate subtractWorkingDaysFromDate(LocalDate dueDate, Integer workingDays)
    {
        LocalDate returnDate = dueDate;
        for (int i = 0; i < workingDays;)
        {
            returnDate = returnDate.minusDays(1);
            if (isWorkingDay(returnDate))
            {
                i++;
            }
        }
        return returnDate;
    }

    public Date addWorkingDaysToDate(Date date, int workingDays)
    {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        localDate = addWorkingDaysToDate(localDate, workingDays);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public boolean isWeekendNonWorkingDay(LocalDate date)
    {
        return (((date.getDayOfWeek().equals(DayOfWeek.SATURDAY)) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY))
                && !getHolidayConfiguration().getIncludeWeekends());
    }

    public boolean isHoliday(LocalDate date)
    {
        return getHolidayConfiguration().getHolidays().stream().filter(item -> item.getHolidayDate().equals(date)).count() > 0;
    }

    public boolean isWorkingDay(LocalDate date)
    {
        return !isHoliday(date) && !isWeekendNonWorkingDay(date);
    }

    public LocalDate getFirstWorkingDay(LocalDate date)
    {
        LocalDate resultDate = date;
        while (!isWorkingDay(resultDate))
        {
            resultDate = resultDate.plusDays(1);
        }

        return resultDate;
    }

    public int countWorkingDates(LocalDate from, LocalDate to)
    {
        int count = 0;
        while (from.isBefore(to))
        {
            if (isWorkingDay(from))
            {
                count++;
            }
            from = from.plusDays(1);
        }
        return count;
    }

    private void setHolidayConfigurationFromFile()
    {
        holidayConfiguration = new HolidayConfiguration();

        try
        {
            log.info("Trying to read from config file: {}", getHolidayFile().getFile().getAbsolutePath());

            lock.readLock().lock();
            String holidayConfigJson = FileUtils.readFileToString(getHolidayFile().getFile());
            holidayConfiguration = getObjectConverter().getJsonUnmarshaller().unmarshall(holidayConfigJson, HolidayConfiguration.class);
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
        finally
        {
            lock.readLock().unlock();
        }
    }

    public Resource getHolidayFile()
    {
        return holidayFile;
    }

    public void setHolidayFile(Resource holidayFile)
    {
        this.holidayFile = holidayFile;
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
