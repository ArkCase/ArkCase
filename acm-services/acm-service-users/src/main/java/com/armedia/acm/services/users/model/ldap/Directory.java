package com.armedia.acm.services.users.model.ldap;

/*-
 * #%L
 * ACM Service: Users
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
            20,
            MapperUtils.convertFileTimeTimestampToDate,
            MapperUtils.activeDirectoryPasswordToAttribute,
            MapperUtils.activeDirectoryPasswordToAttribute),
    openldap(
            "yyyyMMddHHmmssVV",
            "uid",
            20,
            MapperUtils.calculatePasswordExpirationDateByShadowAccount,
            MapperUtils.openLdapPasswordToAttribute,
            MapperUtils.openLdapCurrentPasswordToAttribute);

    private final String datePattern;
    private final String userRdnAttribute;
    private final int userRdnAttributeLength;
    private final Function<DirContextAdapter, LocalDate> timestampToLocalDate;
    private final Function<String, BasicAttribute> passwordToAttribute;
    private final Function<String, BasicAttribute> currentPasswordToAttribute;
    private DateTimeFormatter dateTimeFormatter;

    Directory(String datePattern, String userRdnAttribute, int userRdnAttributeLength,
            Function<DirContextAdapter, LocalDate> timestampToLocalDate,
            Function<String, BasicAttribute> passwordToAttribute, Function<String, BasicAttribute> currentPasswordToAttribute)
    {
        this.datePattern = datePattern;
        this.userRdnAttribute = userRdnAttribute;
        this.userRdnAttributeLength = userRdnAttributeLength;
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

    public int getUserRdnAttributeLength()
    {
        return userRdnAttributeLength;
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
