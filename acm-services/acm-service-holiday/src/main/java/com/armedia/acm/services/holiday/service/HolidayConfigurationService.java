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
import com.armedia.acm.services.holiday.model.BusinessHoursConfig;
import com.armedia.acm.services.holiday.model.HolidayConfiguration;
import com.armedia.acm.services.holiday.model.HolidayConfigurationProps;
import com.armedia.acm.services.holiday.model.HolidayItem;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private DateTimeService dateTimeService;
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

    /**
     *
     * @param date
     * @param workingDays
     * @return Date at the start of day UTC time
     */
    public Date addWorkingDaysToDate(Date date, int workingDays)
    {
        LocalDate localDate = getLocalDateAtSystemDefault(date);
        localDate = addWorkingDaysToDate(localDate, workingDays);

        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     *
     * @param date
     * @param workingDays
     * @return Date with added BusinessHours depending on configuration (TimeZone and EndOfBusinessHours)
     */
    public Date addWorkingDaysToDateAndSetTimeToBusinessHours(Date date, int workingDays)
    {
        LocalDate dateWithAddedWorkingDays = addWorkingDaysToDate(getLocalDateAtSystemDefault(date), workingDays);

        return setEndOfLocalTimeBusinessHoursToDate(dateWithAddedWorkingDays);
    }

    /**
     *
     * @param date
     * @param workingDays
     * @return Date with added Working Days and Businees Hours depending on configuration
     * (endOfBusinessDaysEnabled & endOfBusinessDayTime)
     */
    public Date addWorkingDaysAndWorkingHoursToDateWithBusinessHours(Date date, int workingDays)
    {

    /**
     *
     * @param date
     * @param workingDays
     * @return Date with added Working Days and Businees Hours depending on configuration
     * (endOfBusinessDaysEnabled & endOfBusinessDayTime)
     */
    public Date addWorkingDaysAndWorkingHoursToDateWithBusinessHours(Date date, int workingDays)
    {

        LocalDateTime ldt = getDateTimeService().fromDateToLocalDateTime(date);

        if (getBusinessHoursConfig().getBusinessDayHoursEnabled() && isTimeAfterBusinessHours(ldt))
        {
            return addWorkingDaysToDateAndSetTimeToBusinessHours(date, workingDays + 1);
        }
        else
        {
            return addWorkingDaysToDateAndSetTimeToBusinessHours(date, workingDays);
        }
    }

    /**
     *
     * @param date
     * @return Date with added BusinessHours depending on configuration (endOfBusinessDay) adjusted to defaultClientTimezone
     */
    public Date setEndOfLocalTimeBusinessHoursToDate(LocalDate date)
    {
        LocalTime endOfLocalTimeBusinessHoursToUTC = LocalDateTime.of(date, getEndOfClientBusinessDayTime())
                .atZone(getDateTimeService().getDefaultClientZoneId())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalTime();

        LocalDateTime localDateTimeWithSetBusinessHours = date.atTime(endOfLocalTimeBusinessHoursToUTC);

        return Date.from(localDateTimeWithSetBusinessHours.atZone(ZoneId.systemDefault()).toInstant());
    }

    public boolean isTimeAfterBusinessHours(Date date)
    {
        LocalTime localTimeInSetTimezone = getDateTimeService().toLocalTime(date);

        return localTimeInSetTimezone.isAfter(getEndOfClientBusinessDayTime());
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

    public LocalDateTime getFirstWorkingDay(LocalDateTime date)
    {
        LocalDateTime resultDate = date;
        while (!isWorkingDay(resultDate.toLocalDate()))
        {
            resultDate = resultDate.plusDays(1);
        }

        return resultDate;
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
        LocalDateTime resultDate = getDateTimeService().toClientLocalDateTime(date);

        if (isWorkingDay(resultDate.toLocalDate()))
        {
            if (getBusinessHoursConfig().getBusinessDayHoursEnabled() && isTimeBeforeBusinessHours(resultDate))
            {
                return getStartofDay(resultDate);
            }

            if (getBusinessHoursConfig().getBusinessDayHoursEnabled() && isTimeAfterBusinessHours(resultDate))
            {

                return getStartOfNextDay(resultDate);
            }
        }
        else
        {
            return getFirstWorkingDayAtStartOfDay(resultDate);
        }

        return getDateTimeService().fromClientLocalDateTimeToUTCDateTime(resultDate);
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

    public LocalDateTime getStartOfNextDay(LocalDateTime date)
    {
        date = date.plusDays(1);
        return getFirstWorkingDayAtStartOfDay(date);
    }

    private LocalDateTime getFirstWorkingDayAtStartOfDay(LocalDateTime resultDate)
    {
        resultDate = getFirstWorkingDay(resultDate);
        return getStartofDay(resultDate);
    }

    private LocalDateTime getStartofDay(LocalDateTime date)
    {
        return date.toLocalDate().atTime(getStartOfLocalTimeBusinessHoursToUTC(date.toLocalDate()));
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

    private LocalDate getLocalDateAtSystemDefault(Date date)
    {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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

    private LocalTime getEndOfClientBusinessDayTime()
    {
        return LocalTime.parse(getBusinessHoursConfig().getEndOfBusinessDayTime());
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

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
