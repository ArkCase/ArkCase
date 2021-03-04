package com.armedia.acm.services.users.model;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.data.converter.LocalDateTimeConverter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "acm_user_access_token")
public class UserAccessToken
{
    @Id
    @TableGenerator(name = "acm_user_access_token_gen", table = "acm_user_access_token_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_user_access_token", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_user_access_token_gen")
    @Column(name = "cm_id")
    private Long id;

    @Column(name = "cm_value", nullable = false, updatable = false)
    private String value;

    @Column(name = "cm_user_email", nullable = false, updatable = false)
    private String userEmail;

    @Column(name = "cm_expiration_in_sec")
    private Long expirationInSec;

    @Column(name = "cm_provider", nullable = false, updatable = false)
    private String provider;

    @Column(name = "cm_created_date_time")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdDateTime;

    public boolean isExpired()
    {
        return LocalDateTime.now()
                .isAfter(createdDateTime
                        .plusSeconds(expirationInSec));
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getUserEmail()
    {
        return userEmail;
    }

    public void setUserEmail(String userEmail)
    {
        this.userEmail = userEmail;
    }

    public Long getExpirationInSec()
    {
        return expirationInSec;
    }

    public void setExpirationInSec(Long expirationInSec)
    {
        this.expirationInSec = expirationInSec;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public LocalDateTime getCreatedDateTime()
    {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime)
    {
        this.createdDateTime = createdDateTime;
    }
}
