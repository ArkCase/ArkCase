package com.armedia.acm.calendar.config.web.api;

import com.armedia.acm.calendar.config.service.CalendarAdminService;
import com.armedia.acm.calendar.config.service.CalendarConfigurationException;
import com.armedia.acm.calendar.config.service.CalendarConfigurationExceptionMapper;
import com.armedia.acm.calendar.config.service.CalendarConfigurationsByObjectType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 9, 2017
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/calendar/configure", "/api/latest/service/calendar/configure" })
public class AcmCalendarManagementAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private CalendarAdminService calendarService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CalendarConfigurationsByObjectType getConfiguration() throws CalendarConfigurationException
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

    @RequestMapping(value = "/{email}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> validateEmail(@PathVariable(value = "email") String email)
    {
        return ResponseEntity.status(HttpStatus.OK).body("true");
    }

    @ExceptionHandler(CalendarConfigurationException.class)
    @ResponseBody
    public ResponseEntity<?> handleConfigurationException(CalendarConfigurationException ce)
    {
        CalendarConfigurationExceptionMapper<CalendarConfigurationException> exceptionMapper = calendarService.getExceptionMapper(ce);
        Object errorDetails = exceptionMapper.mapException(ce);
        return ResponseEntity.status(exceptionMapper.getStatusCode()).body(errorDetails);
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
