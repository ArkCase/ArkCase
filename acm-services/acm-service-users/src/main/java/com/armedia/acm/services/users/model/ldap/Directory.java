package com.armedia.acm.services.users.model.ldap;

import org.springframework.ldap.core.DirContextAdapter;

import javax.naming.directory.BasicAttribute;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public enum Directory
{
    activedirectory(
            "yyyyMMddHHmmss.0VV",
            "cn",
            MapperUtils.convertFileTimeTimestampToDate,
            MapperUtils.activeDirectoryPasswordToAttribute,
            MapperUtils.activeDirectoryPasswordToAttribute),
    openldap(
            "yyyyMMddHHmmssVV",
            "uid",
            MapperUtils.calculatePasswordExpirationDateByShadowAccount,
            MapperUtils.openLdapPasswordToAttribute,
            MapperUtils.openLdapCurrentPasswordToAttribute);

    private final String datePattern;
    private final String userRdnAttribute;
    private final Function<DirContextAdapter, LocalDate> timestampToLocalDate;
    private final Function<String, BasicAttribute> passwordToAttribute;
    private final Function<String, BasicAttribute> currentPasswordToAttribute;
    private DateTimeFormatter dateTimeFormatter;

    Directory(String datePattern, String userRdnAttribute, Function<DirContextAdapter, LocalDate> timestampToLocalDate,
            Function<String, BasicAttribute> passwordToAttribute, Function<String, BasicAttribute> currentPasswordToAttribute)
    {
        this.datePattern = datePattern;
        this.userRdnAttribute = userRdnAttribute;
        this.timestampToLocalDate = timestampToLocalDate;
        dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);
        this.passwordToAttribute = passwordToAttribute;
        this.currentPasswordToAttribute = currentPasswordToAttribute;
    }

    public String getDatePattern()
    {
        return datePattern;
    }

    public String getUserRdnAttribute()
    {
        return userRdnAttribute;
    }

    public String buildDnForUserEntry(String userId, AcmLdapSyncConfig syncConfig)
    {
        String rdn = String.format("%s=%s", userRdnAttribute, userId);
        return MapperUtils.appendToDn(rdn, syncConfig.getUserSearchBase(), syncConfig.getBaseDC());
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

    public BasicAttribute getCurrentPasswordAttribute(String password)
    {
        return currentPasswordToAttribute.apply(password);
    }

}
