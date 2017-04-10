package com.armedia.acm.calendar.service;

import org.springframework.http.HttpStatus;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 4, 2017
 *
 */
public interface CalendarExceptionMapper<CSE extends CalendarServiceException>
{

    /**
     * @param ce
     * @return
     */
    Object mapException(CSE ce);

    /**
     * @return
     */
    HttpStatus getStatusCode();

}
