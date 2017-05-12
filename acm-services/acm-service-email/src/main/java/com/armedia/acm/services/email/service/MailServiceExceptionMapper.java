package com.armedia.acm.services.email.service;

import org.springframework.http.HttpStatus;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 28, 2017
 *
 */
public interface MailServiceExceptionMapper<ME extends AcmEmailServiceException>
{

    /**
     * @param ce
     * @return
     */
    Object mapException(ME me);

    /**
     * @return
     */
    HttpStatus getStatusCode();

}
