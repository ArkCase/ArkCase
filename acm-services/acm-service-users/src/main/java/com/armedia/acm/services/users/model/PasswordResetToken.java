package com.armedia.acm.services.users.model;

import com.armedia.acm.data.converter.LocalDateTimeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Embeddable
public class PasswordResetToken
{
    private static final long EXPIRATION_HOURS = 24L;

    @Column(name = "cm_token", unique = true)
    private String token;

    @Column(name = "cm_token_ex_date")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime expiryDate;

    public PasswordResetToken()
    {
        this.token = UUID.randomUUID().toString();
        this.expiryDate = LocalDateTime.now().plusHours(EXPIRATION_HOURS);
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public LocalDateTime getExpiryDate()
    {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate)
    {
        this.expiryDate = expiryDate;
    }
}
