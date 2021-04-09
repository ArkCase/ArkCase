package com.armedia.acm.services.holiday.service;

/*-
 * #%L
 * ACM Service: Holiday
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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
import com.armedia.acm.services.holiday.model.HolidayConfigurationProps;
import com.armedia.acm.services.holiday.model.HolidayItem;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class HolidayConfigurationService
{
    private HolidayConfigurationProps holidayConfigurationProps;
    private ConfigurationPropertyService configurationPropertyService;
    private BusinessHoursConfig businessHoursConfig;
    private ApplicationConfig applicationConfig;
    public static HolidayConfigurationService util;

    public void init()
    {
        HolidayConfigurationService.util = this;
    }

    public void saveHolidayConfig(HolidayConfiguration config)
    {
        HolidayConfigurationProps props = getPropsFromHolidayConfiguration(config);
        configurationPropertyService.updateProperties(props);
    }

    public HolidayConfiguration getHolidayConfiguration()
    {
        return getHolidayConfigurationFromProps(holidayConfigurationProps);
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
        LocalDate localDate = getLocalDateAtSystemDefault(date);
        localDate = addWorkingDaysToDate(localDate, workingDays);

        return addEndOfLocalTimeBusinessHoursToDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public Date addWorkingDaysToDateWithBusinessHours(Date date, int workingDays)
    {
        LocalDate localDate = getLocalDateAtSystemDefault(date);

        if (getBusinessHoursConfig().getEndOfBusinessDayEnabled() && isTimeAfterBusinessHours(date))
        {
            localDate = addWorkingDaysToDate(localDate, workingDays + 1);
        }
        else
        {
            localDate = addWorkingDaysToDate(localDate, workingDays);
        }

        return addEndOfLocalTimeBusinessHoursToDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public boolean isTimeAfterBusinessHours(Date date)
    {
        LocalTime localTimeInSetTimezone = date.toInstant().atZone(getDefaultClientZoneId()).toLocalTime();

        return localTimeInSetTimezone.isAfter(getEndOfClientBusinessDayTime());
    }

    public Date addEndOfLocalTimeBusinessHoursToDate(Date date)
    {
        LocalTime endOfLocalTimeBusinessHoursToUTC = LocalDateTime.of(getLocalDateAtSystemDefault(date), getEndOfClientBusinessDayTime())
                .atZone(getDefaultClientZoneId())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalTime();

        LocalDateTime ldt = getLocalDateAtSystemDefault(date).atTime(endOfLocalTimeBusinessHoursToUTC);

        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    private LocalDate getLocalDateAtSystemDefault(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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

    public LocalDateTime getFirstWorkingDateWithBusinessHoursCalculation(LocalDateTime date)
    {
        LocalDateTime resultDate = date;

        if (getBusinessHoursConfig().getEndOfBusinessDayEnabled() && isWorkingDay(resultDate.toLocalDate())
                && isTimeAfterBusinessHours(Date.from(date.atZone(ZoneId.systemDefault()).toInstant())))
        {
            resultDate = resultDate.plusDays(1);
        }

        while (!isWorkingDay(resultDate.toLocalDate()))
        {
            resultDate = resultDate.plusDays(1);
        }

        if (resultDate.isEqual(date))
        {
            return resultDate;
        }
        else
        {
            return resultDate.toLocalDate().atStartOfDay();
        }
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

    private static HolidayConfiguration getHolidayConfigurationFromProps(HolidayConfigurationProps props)
    {
        HolidayConfiguration config = new HolidayConfiguration();
        config.setIncludeWeekends(props.getIncludeWeekends());
        config.setHolidays(props.getHolidays().entrySet().stream().map(entry -> {
            HolidayItem holiday = new HolidayItem();
            holiday.setHolidayName(entry.getValue());
            holiday.setHolidayDate(LocalDate.parse(entry.getKey(), DateTimeFormatter.ISO_LOCAL_DATE));
            return holiday;
        }).sorted(Comparator.comparing(HolidayItem::getHolidayDate)).collect(Collectors.toList()));
        return config;
    }

    private static HolidayConfigurationProps getPropsFromHolidayConfiguration(HolidayConfiguration config)
    {
        HolidayConfigurationProps props = new HolidayConfigurationProps();
        props.setIncludeWeekends(config.getIncludeWeekends());
        props.setHolidays(config.getHolidays().stream().collect(Collectors.toMap(
                holiday -> holiday.getHolidayDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                HolidayItem::getHolidayName,
                (oldValue, newValue) -> newValue,
                LinkedHashMap::new)));
        return props;
    }

    private LocalTime getEndOfClientBusinessDayTime() {
        return LocalTime.parse(getBusinessHoursConfig().getEndOfBusinessDayTime());
    }

    private ZoneId getDefaultClientZoneId() {
        return ZoneId.of(getApplicationConfig().getDefaultTimezone());
    }

    public HolidayConfigurationProps getHolidayConfigurationProps()
    {
        return holidayConfigurationProps;
    }

    public void setHolidayConfigurationProps(HolidayConfigurationProps holidayConfigurationProps)
    {
        this.holidayConfigurationProps = holidayConfigurationProps;
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
