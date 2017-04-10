package com.armedia.acm.calendar.config.service;

import org.springframework.http.HttpStatus;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 22, 2017
 *
 */
public interface CalendarConfigurationExceptionMapper<CCE extends CalendarConfigurationException>
{

    /**
     * @param ce
     * @return
     */
    Object mapException(CCE ce);

    /**
     * @return
     */
    HttpStatus getStatusCode();

}
