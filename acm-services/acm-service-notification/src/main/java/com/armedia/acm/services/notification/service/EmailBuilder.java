package com.armedia.acm.services.notification.service;

import java.util.Map;

/**
 * Extracts data needed to send an email from <code>emailData</code> and inserts it in the <code>messageProps</code>
 * map.
 *
 * @param <T> contains data needed to send an email in arbitrary format, may be a POJO.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Oct 14, 2016
 *
 */
@FunctionalInterface
public interface EmailBuilder<T>
{

    /**
     * @param emailData contains data needed to send an email in arbitrary format, may be a POJO.
     * @param messageProps a map that contains data needed to send an email.
     */
    void buildEmail(T emailData, Map<String, Object> messageProps);

}