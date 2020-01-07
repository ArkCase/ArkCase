package com.armedia.acm.calendar.config.web.api;

/*-
 * #%L
 * ACM Service: Calendar Service
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.calendar.config.service.CalendarAdminService;
import com.armedia.acm.calendar.config.service.CalendarConfigurationException;
import com.armedia.acm.calendar.config.service.CalendarConfigurationExceptionMapper;
import com.armedia.acm.calendar.config.service.CalendarConfigurationsByObjectType;
import com.armedia.acm.calendar.config.service.EmailCredentials;
import com.armedia.acm.calendar.config.service.EmailCredentialsVerifierService;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 9, 2017
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/calendar/configure", "/api/latest/service/calendar/configure" })
public class AcmCalendarManagementAPIController
{
    private Logger log = LogManager.getLogger(getClass());

    private CalendarAdminService calendarService;

    private EmailCredentialsVerifierService verifierService;

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

    @RequestMapping(value = "/validate", method = RequestMethod.PUT, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> validateEmail(HttpSession session, @RequestBody EmailCredentials emailCredentials)
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        return ResponseEntity.status(HttpStatus.OK)
                .body(Boolean.toString(verifierService.verifyEmailCredentials(user.getUserId(), emailCredentials)));
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

    /**
     * @param verifierService
     *            the verifierService to set
     */
    public void setVerifierService(EmailCredentialsVerifierService verifierService)
    {
        this.verifierService = verifierService;
    }

}
