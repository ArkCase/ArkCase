package com.armedia.acm.plugins.outlook.web.api;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.plugins.outlook.service.CalendarAdminService;
import com.armedia.acm.plugins.outlook.service.CalendarConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 9, 2017
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/outlook/calendar/admin", "/api/latest/plugin/outlook/calendar/admin" })
public class CalendarManagementAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private CalendarAdminService calendarService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CalendarConfiguration getConfiguration()
    {
        return calendarService.readConfiguration();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> updateConfiguration(@RequestBody CalendarConfiguration configuration) throws CalendarConfigurationException
    {
        calendarService.writeConfiguration(configuration);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(CalendarConfigurationException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleConfigurationException(CalendarConfigurationException ce)
    {

        BodyBuilder response;
        Map<String, String> errorDetails = new HashMap<>();

        Throwable cause = ce.getCause();
        if (cause != null)
        {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
            Class<? extends Throwable> causeClass = cause.getClass();
            if (causeClass.equals(AcmEncryptionException.class))
            {
                errorDetails.put("error_cause", "Encryption exception.");
            } else if (causeClass.equals(IOException.class))
            {
                errorDetails.put("error_cause", "Properties writing exception.");
            }
        } else
        {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST);
            errorDetails.put("error_cause", "Invalid input excpetion.");
        }

        errorDetails.put("error_message", ce.getMessage());

        return response.body(errorDetails);
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
