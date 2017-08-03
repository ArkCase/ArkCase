package com.armedia.acm.services.users.model.ldap;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public enum Directory
{
    ACTIVE_DIRECTORY("activedirectory", "yyyyMMddHHmmss.0VV"),
    OPEN_LDAP("openldap", "yyyyMMddHHmmssVV");

    private final String type;
    private final String datePattern;

    Directory(String type, String datePattern)
    {
        this.type = type;
        this.datePattern = datePattern;
    }

    public String getType()
    {
        return type;
    }

    public String getDatePattern()
    {
        return datePattern;
    }

    public String convertToDirectorySpecificTimestamp(String date)
    {
        ZonedDateTime dateTime = ZonedDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
        return dateTime.format(formatter);
    }

}
