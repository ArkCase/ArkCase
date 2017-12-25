package com.armedia.acm.calendar.config.service;

import org.springframework.http.HttpStatus;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 22, 2017
 *
 */
public interface CalendarConfigurationExceptionMapper<CCE extends CalendarConfigurationException>
{

    String INPUT_DATA_EXCEPTION = "INPUT_DATA_EXCEPTION";

    String ENCRYPT_EXCEPTION = "ENCRYPT_EXCEPTION";

    String UPDATE_CONFIGURATION_EXCEPTION = "UPDATE_CONFIGURATION_EXCEPTION.";

    String ERROR_MESSAGE = "error_message";

    String ERROR_CAUSE = "error_cause";

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
