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

    public int calculateAmountOfWorkingDays(LocalDate startDate, LocalDate endDate)
    {
        int i = 0;
        while (startDate.isBefore(endDate))
        {
            if (isWorkingDay(endDate))
            {
                i++;
            }
            endDate = endDate.minusDays(1);
        }
        return i;
    }

    /**
     *
     * @param date
     * @param workingDays
     * @return Date at the start of day UTC time
     */
    public Date addWorkingDaysToDate(Date date, int workingDays)
    {
        LocalDateTime localDateTime = getDateTimeService().fromDateToUTCLocalDateTime(date);
        localDateTime = addWorkingDaysToDate(localDateTime, workingDays);

        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public LocalDateTime addWorkingDaysToDate(LocalDateTime date, int workingDays)
    {

        LocalDateTime localDateTime = getDateTimeService().toClientLocalDateTime(date);

        LocalTime localTime = localDateTime.toLocalTime();
        LocalDate localDate = addWorkingDaysToDate(localDateTime.toLocalDate(), workingDays);

        localDateTime = LocalDateTime.of(localDate, localTime);

        return getDateTimeService().fromClientLocalDateTimeToUTCDateTime(localDateTime);
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

    /**
     *
     * @param date
     * @param workingDays
     * @return Date with added BusinessHours depending on configuration (TimeZone and EndOfBusinessHours)
     */
    public Date addWorkingDaysToDateAndSetTimeToBusinessHours(Date date, int workingDays)
    {
        LocalDateTime ldt = getDateTimeService().fromDateToUTCLocalDateTime(date);

        if (isFirstDayFullWorkingDay(ldt))
        {
            // if first day is full working day we need to subtrack it
            ldt = addWorkingDaysToDate(ldt, workingDays - 1);
        }
        else
        {
            ldt = addWorkingDaysToDate(ldt, workingDays);
        }

        return setEndOfLocalTimeBusinessHoursToDate(ldt);
    }

    /**
     *
     * @param date
     * @param workingDays
     * @return Date with added Working Days and Businees Hours depending on configuration
     *         (endOfBusinessDaysEnabled & endOfBusinessDayTime)
     */
    public Date addWorkingDaysAndWorkingHoursToDateWithBusinessHours(Date date, int workingDays)
    {

        LocalDateTime ldt = getDateTimeService().fromDateToClientLocalDateTime(date);

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
     * @return Date with added BusinessHours depending on configuration (endOfBusinessDay) adjusted to
     *         defaultClientTimezone
     */
    public Date setEndOfLocalTimeBusinessHoursToDate(LocalDateTime date)
    {
        LocalDateTime localDateTimeWithSetBusinessHours = getEndOfLocalTimeBusinessHours(date);

        return Date.from(localDateTimeWithSetBusinessHours.atZone(ZoneId.systemDefault()).toInstant());
    }

    public boolean isTimeAfterBusinessHours(LocalDateTime date)
    {
        LocalTime localTimeInSetTimezone = date.toLocalTime();

        return localTimeInSetTimezone.isAfter(getEndOfClientBusinessDayTime());
    }

    public boolean isTimeBeforeBusinessHours(LocalDateTime date)
    {
        LocalTime localTimeInSetTimezone = date.toLocalTime();

        return localTimeInSetTimezone.isBefore(getStartOfClientBusinessDayTime());
    }

    public boolean isFirstDayFullWorkingDay(LocalDateTime date)
    {
        LocalTime localTime = getDateTimeService().toClientLocalDateTime(date).toLocalTime();

        return localTime.equals(getStartOfClientBusinessDayTime());
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
                return getStartOfDayWithBusinessHours(resultDate);
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
        return getStartOfDayWithBusinessHours(resultDate);
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

    private LocalDateTime getEndOfLocalTimeBusinessHours(LocalDateTime date)
    {
        return date.with(getEndOfClientBusinessDayTime())
                .atZone(getDateTimeService().getDefaultClientZoneId())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    private LocalDateTime getStartOfDayWithBusinessHours(LocalDateTime date)
    {
        return date.with(getStartOfClientBusinessDayTime())
                .atZone(getDateTimeService().getDefaultClientZoneId())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    private LocalTime getEndOfClientBusinessDayTime()
    {
        return LocalTime.parse(getBusinessHoursConfig().getEndOfBusinessDayTime());
    }

    private LocalTime getStartOfClientBusinessDayTime()
    {
        return LocalTime.parse(getBusinessHoursConfig().getStartOfBusinessDayTime());
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

    public DateTimeService getDateTimeService()
    {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService)
    {
        this.dateTimeService = dateTimeService;
    }
}
