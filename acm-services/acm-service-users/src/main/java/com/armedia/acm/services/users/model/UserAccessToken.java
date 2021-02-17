package com.armedia.acm.services.users.model;

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
