package com.armedia.acm.services.holiday.service;

import com.armedia.acm.core.model.ApplicationConfig;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateTimeService {

    private ApplicationConfig appConfig;
    private static DateTimeService dateTimeService;

    public void init() {
        DateTimeService.dateTimeService = this;
    }

    public LocalDateTime toLocalDateTime(Date date) {
        return getZonedDateTimeAtDefaultClientTimezone(date).toLocalDateTime();
    }

    public LocalDateTime toLocalDateTime(LocalDateTime date) {
        return getZonedDateTimeAtDefaultClientTimezone(date).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    public LocalDate toLocalDate(Date date) {
        return getZonedDateTimeAtDefaultClientTimezone(date).toLocalDate();
    }

    public LocalDate toLocalDate(LocalDateTime date) {
        return getZonedDateTimeAtDefaultClientTimezone(date).withZoneSameInstant(ZoneOffset.UTC).toLocalDate();
    }

    public LocalTime toLocalTime(Date date) {
        return getZonedDateTimeAtDefaultClientTimezone(date).toLocalTime();
    }

    public LocalTime toLocalTime(LocalDateTime date) {
        return getZonedDateTimeAtDefaultClientTimezone(date).withZoneSameInstant(ZoneOffset.UTC).toLocalTime();
    }

    public LocalDateTime toUTCDateTime(Date date)
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
