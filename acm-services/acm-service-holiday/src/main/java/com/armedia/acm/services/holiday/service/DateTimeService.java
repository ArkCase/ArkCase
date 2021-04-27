package com.armedia.acm.services.holiday.service;

/*-
 * #%L
 * ACM Service: Holiday
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import com.armedia.acm.core.model.ApplicationConfig;
import org.joda.time.DateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeService {

    private ApplicationConfig appConfig;
    public static DateTimeService dateTimeService;

    private String dateTimePattern = "MM/dd/yyyy HH:mm [VV]";
    private String datePattern = "MM/dd/yyyy [VV]";

    public void init()
    {
        DateTimeService.dateTimeService = this;
    }

    public static String toClientDateTimeTimezone(LocalDateTime date)
    {
        return dateTimeService.getZonedDateTimeAtDefaultClientTimezone(date).format(DateTimeFormatter.ofPattern(dateTimeService.datePattern));
    }

    public static String toClientDateTimezone(LocalDateTime date)
    {
        return dateTimeService.getZonedDateTimeAtDefaultClientTimezone(date).format(DateTimeFormatter.ofPattern(dateTimeService.dateTimePattern));
    }

    public static String toUTCDateTimeTimezone(LocalDateTime date)
    {
        return dateTimeService.getZonedDateTimeAtUTC(date).format(DateTimeFormatter.ofPattern(dateTimeService.dateTimePattern));
    }

    public static String toUTCDateTimezone(LocalDateTime date)
    {
        return dateTimeService.getZonedDateTimeAtUTC(date).format(DateTimeFormatter.ofPattern(dateTimeService.datePattern));
    }

    public LocalDateTime fromDateToClientLocalDateTime(Date date)
    {
        return getZonedDateTimeAtDefaultClientTimezone(date).toLocalDateTime();
    }

    public LocalDateTime toClientLocalDateTime(LocalDateTime date)
    {
        return getZonedDateTimeAtDefaultClientTimezone(date).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    public LocalDate fromDateToClientLocalDate(Date date)
    {
        return getZonedDateTimeAtDefaultClientTimezone(date).toLocalDate();
    }

    public LocalDate toClientLocalDate(LocalDateTime date)
    {
        return getZonedDateTimeAtDefaultClientTimezone(date).withZoneSameInstant(ZoneOffset.UTC).toLocalDate();
    }

    public LocalTime fromDateToClientTimeTimezone(Date date)
    {
        return getZonedDateTimeAtDefaultClientTimezone(date).toLocalTime();
    }

    public LocalTime toClientLocalTime(LocalDateTime date)
    {
        return getZonedDateTimeAtDefaultClientTimezone(date).withZoneSameInstant(ZoneOffset.UTC).toLocalTime();
    }

    public LocalDateTime fromDateToLocalDateTime(Date date)
    {
        return getZonedDateTimeAtUTC(date).toLocalDateTime();
    }

    public LocalDateTime toUTCDateTime(LocalDateTime date)
    {
        return getZonedDateTimeAtUTC(date).toLocalDateTime();
    }

    public LocalDate toUTCDate(Date date)
    {
        return getZonedDateTimeAtUTC(date).toLocalDate();
    }

    public LocalDate toUTCDate(LocalDateTime date)
    {
        return getZonedDateTimeAtUTC(date).toLocalDate();
    }

    public ZonedDateTime getZonedDateTimeAtDefaultClientTimezone(Date date)
    {
        return date.toInstant().atZone(getDefaultClientZoneId());
    }

    public ZonedDateTime getZonedDateTimeAtDefaultClientTimezone(LocalDateTime date)
    {
        return date.atZone(getDefaultClientZoneId());
    }

    public ZonedDateTime getZonedDateTimeAtUTC(Date date)
    {
        return date.toInstant().atZone(ZoneId.systemDefault());
    }

    public ZonedDateTime getZonedDateTimeAtUTC(LocalDateTime date)
    {
        return date.atZone(ZoneId.systemDefault());
    }

    public ZoneId getDefaultClientZoneId()
    {
        return ZoneId.of(getAppConfig().getDefaultTimezone());
    }

    public ApplicationConfig getAppConfig()
    {
        return appConfig;
    }

    public void setAppConfig(ApplicationConfig appConfig)
    {
        this.appConfig = appConfig;
    }
}
