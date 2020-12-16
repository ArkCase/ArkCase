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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.core.model.ApplicationConfig;
import com.armedia.acm.services.holiday.model.BusinessHoursConfig;
import com.armedia.acm.services.holiday.model.HolidayConfiguration;
import com.armedia.acm.services.holiday.model.HolidayItem;
import com.armedia.acm.services.holiday.model.HolidayPropsHolder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HolidayConfigurationService
{
    private HolidayPropsHolder holidayPropsHolder;
    private ConfigurationPropertyService configurationPropertyService;
    private BusinessHoursConfig businessHoursConfig;
    private ApplicationConfig applicationConfig;

    public void saveHolidayConfig(HolidayConfiguration holidayConfiguration)
    {
        HolidayPropsHolder holidayPropsHolder = getPropsFromHolidayConfiguration(holidayConfiguration);
        configurationPropertyService.updateProperties(holidayPropsHolder);
    }

    public HolidayConfiguration getHolidayConfiguration()
    {
        return getHolidayConfigurationFromProps(holidayPropsHolder);
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

    public Date addWorkingDaysToDateWithBusinessHours(Date date, int workingDays)
    {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (getBusinessHoursConfig().getEndOfBusinessDayEnabled() && isTimeAfterBusinessHours(date))
        {
            localDate = addWorkingDaysToDate(localDate, workingDays + 1);
        }
        else
        {
            localDate = addWorkingDaysToDate(localDate, workingDays);
        }

        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public boolean isTimeAfterBusinessHours(Date date)
    {
        ZoneId defaultClientTimezone = ZoneId.of(getApplicationConfig().getDefaultTimezone());

        LocalTime localTimeInSetTimezone = date.toInstant().atZone(defaultClientTimezone).toLocalTime();
        LocalTime endOfBusinessDayTime = LocalTime.parse(getBusinessHoursConfig().getEndOfBusinessDayTime());

        return localTimeInSetTimezone.isAfter(endOfBusinessDayTime);
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

    private HolidayConfiguration getHolidayConfigurationFromProps(HolidayPropsHolder holidayPropsHolder)
    {
        HolidayConfiguration holidayConfiguration = new HolidayConfiguration();
        holidayConfiguration.setIncludeWeekends(holidayPropsHolder.getIncludeWeekends());
        List<HolidayItem> holidays = new ArrayList<>();
        for (Map.Entry<String, String> entry : holidayPropsHolder.getHolidays().entrySet())
        {
            HolidayItem holiday = new HolidayItem();
            holiday.setHolidayName(entry.getValue());
            holiday.setHolidayDate(LocalDate.parse(entry.getKey(), DateTimeFormatter.ISO_LOCAL_DATE));
            holidays.add(holiday);
        }
        holidays.sort(Comparator.comparing(HolidayItem::getHolidayDate));
        holidayConfiguration.setHolidays(holidays);
        return holidayConfiguration;
    }

    private HolidayPropsHolder getPropsFromHolidayConfiguration(HolidayConfiguration holidayConfiguration)
    {
        HolidayPropsHolder holidayPropsHolder = new HolidayPropsHolder();
        holidayPropsHolder.setIncludeWeekends(holidayConfiguration.getIncludeWeekends());
        Map<String, String> holidays = new LinkedHashMap<>();
        for (HolidayItem holiday : holidayConfiguration.getHolidays())
        {
            holidays.put(holiday.getHolidayDate().format(DateTimeFormatter.ISO_LOCAL_DATE), holiday.getHolidayName());
        }
        holidayPropsHolder.setHolidays(holidays);
        return holidayPropsHolder;
    }

    public HolidayPropsHolder getHolidayPropsHolder()
    {
        return holidayPropsHolder;
    }

    public void setHolidayPropsHolder(HolidayPropsHolder holidayPropsHolder)
    {
        this.holidayPropsHolder = holidayPropsHolder;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }

    public BusinessHoursConfig getBusinessHoursConfig()
    {
        return businessHoursConfig;
    }

    public void setBusinessHoursConfig(BusinessHoursConfig businessHoursConfig)
    {
        this.businessHoursConfig = businessHoursConfig;
    }

    public ApplicationConfig getApplicationConfig()
    {
        return applicationConfig;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig)
    {
        this.applicationConfig = applicationConfig;
    }
}
