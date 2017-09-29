package com.armedia.acm.services.users.model.ldap;

import org.springframework.ldap.core.DirContextAdapter;

import javax.naming.directory.BasicAttribute;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public enum Directory
{
    activedirectory("yyyyMMddHHmmss.0VV", MapperUtils.convertFileTimeTimestampToDate,
            MapperUtils.activeDirectoryPasswordToAttribute),
    openldap("yyyyMMddHHmmssVV", MapperUtils.calculatePasswordExpirationDateByShadowAccount,
            MapperUtils.openLdapPasswordToAttribute);

    private final String datePattern;
    private final Function<DirContextAdapter, LocalDate> timestampToLocalDate;
    private final Function<String, BasicAttribute> passwordToAttribute;
    private DateTimeFormatter dateTimeFormatter;

    Directory(String datePattern, Function<DirContextAdapter, LocalDate> timestampToLocalDate,
              Function<String, BasicAttribute> passwordToAttribute)
    {
        this.datePattern = datePattern;
        this.timestampToLocalDate = timestampToLocalDate;
        dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);
        this.passwordToAttribute = passwordToAttribute;
    }

    public String getDatePattern()
    {
        return datePattern;
    }

    public String convertToDirectorySpecificTimestamp(String date)
    {
        ZonedDateTime dateTime = ZonedDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        return dateTime.format(dateTimeFormatter);
    }

    public LocalDate getPasswordExpirationDate(DirContextAdapter adapter)
    {
        return timestampToLocalDate.apply(adapter);
    }

    public BasicAttribute getPasswordAttribute(String password)
    {
        return passwordToAttribute.apply(password);
    }

}
