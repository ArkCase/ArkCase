package com.armedia.acm.calendar.config.web.api;

import com.armedia.acm.calendar.config.service.CalendarAdminService;
import com.armedia.acm.calendar.config.service.CalendarConfigurationException;
import com.armedia.acm.calendar.config.service.CalendarConfigurationsByObjectType;
import com.armedia.acm.core.exceptions.AcmEncryptionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 9, 2017
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/calendar/configuration", "/api/latest/service/calendar/configuration" })
public class CalendarManagementAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private CalendarAdminService calendarService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CalendarConfigurationsByObjectType getConfiguration() throws AcmEncryptionException, CalendarConfigurationException
    {
        return calendarService.readConfiguration(false);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> updateConfiguration(@RequestBody CalendarConfigurationsByObjectType configuration)
            throws CalendarConfigurationException
    {
        calendarService.writeConfiguration(configuration);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(CalendarConfigurationException.class)
    @ResponseBody
    public ResponseEntity<?> handleConfigurationException(CalendarConfigurationException ce)
    {
        Object errorDetails = calendarService.getExceptionMapper().mapException(ce);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }

    /**
     * @param calendarService
     *            the calendarService to set
     */
    public void setCalendarService(CalendarAdminService calendarService)
    {
        this.calendarService = calendarService;
    }

}
