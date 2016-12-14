package com.armedia.acm.services.notification.service;

/**
 * Builds the email body text.
 *
 * @param <T> contains data needed to send an email in arbitrary format, may be a POJO.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Oct 14, 2016
 */
@FunctionalInterface
public interface EmailBodyBuilder<T>
{

    /**
     * @param emailData contains data needed to send an email in arbitrary format, may be a POJO.
     * @return the email body text.
     */
    String buildEmailBody(T emailData);

}