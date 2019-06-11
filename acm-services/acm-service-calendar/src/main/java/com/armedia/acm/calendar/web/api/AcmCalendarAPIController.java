package com.armedia.acm.calendar.web.api;

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

import static com.armedia.acm.calendar.DateTimeAdjuster.adjustDateTimeString;

import com.armedia.acm.calendar.config.service.CalendarAdminService;
import com.armedia.acm.calendar.config.service.CalendarConfiguration;
import com.armedia.acm.calendar.config.service.CalendarConfigurationException;
import com.armedia.acm.calendar.service.AcmCalendar;
import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarEventInfo;
import com.armedia.acm.calendar.service.AcmCalendarInfo;
import com.armedia.acm.calendar.service.AcmEventAttachmentDTO;
import com.armedia.acm.calendar.service.CalendarExceptionMapper;
import com.armedia.acm.calendar.service.CalendarService;
import com.armedia.acm.calendar.service.CalendarServiceConfigurationException;
import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 28, 2017
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/calendar", "/api/latest/service/calendar" })
public class AcmCalendarAPIController
{

    private CalendarService calendarService;

    private CalendarAdminService calendarAdminService;

    @RequestMapping(value = "/calendars", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmCalendarInfo> listCalendars(HttpSession session, Authentication auth,
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sort,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "maxItems", required = false, defaultValue = "50") int maxItems) throws CalendarServiceException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        return calendarService.listCalendars(user, auth, null, sort, sortDirection, start, maxItems);
    }

    @RequestMapping(value = "/calendars/{objectType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmCalendarInfo> listCalendars(HttpSession session, Authentication auth,
            @PathVariable(value = "objectType") String objectType,
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sort,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "maxItems", required = false, defaultValue = "50") int maxItems) throws CalendarServiceException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        return calendarService.listCalendars(user, auth, objectType, sort, sortDirection, start, maxItems);
    }

    @RequestMapping(value = "/calendars/{objectType}/{objectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmCalendarInfo getCalendar(HttpSession session, Authentication auth, @PathVariable(value = "objectType") String objectType,
            @PathVariable(value = "objectId") String objectId) throws CalendarServiceException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        AcmCalendar calendar = calendarService.retrieveCalendar(user, auth, objectType, objectId)
                .orElseThrow(() -> new CalendarServiceConfigurationException(
                        "Error while retrieving calendar configuration, most likely integration not enabled."));
        return calendar.getInfo();
    }

    @RequestMapping(value = "/calendarevents/info/{objectType}/{objectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmCalendarEventInfo> listEvents(HttpSession session, Authentication auth,
            @PathVariable(value = "objectType") String objectType, @PathVariable(value = "objectId") String objectId,
            @RequestParam(value = "after", required = false) String after, @RequestParam(value = "before", required = false) String before,
            @RequestParam(value = "sort", required = false, defaultValue = "eventDate") String sort,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "maxItems", required = false, defaultValue = "50") int maxItems) throws CalendarServiceException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        AcmCalendar calendar = calendarService.retrieveCalendar(user, auth, objectType, objectId)
                .orElseThrow(() -> new CalendarServiceConfigurationException(
                        "Error while retrieving calendar configuration, most likely integration not enabled."));
        return calendar.listItemsInfo(toZonedDate(setDefaultStart(after)), toZonedDate(setDefaultEnd(before)), sort, sortDirection, start,
                maxItems);
    }

    @RequestMapping(value = "/calendarevents/{objectType}/{objectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmCalendarEvent> listEventsFullDetails(HttpSession session, Authentication auth,
            @PathVariable(value = "objectType") String objectType, @PathVariable(value = "objectId") String objectId,
            @RequestParam(value = "after", required = false) String after, @RequestParam(value = "before", required = false) String before,
            @RequestParam(value = "sort", required = false, defaultValue = "eventDate") String sort,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "maxItems", required = false, defaultValue = "50") int maxItems) throws CalendarServiceException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        AcmCalendar calendar = calendarService.retrieveCalendar(user, auth, objectType, objectId)
                .orElseThrow(() -> new CalendarServiceConfigurationException(
                        "Error while retrieving calendar configuration, most likely integration not enabled."));
        return calendar.listItems(toZonedDate(setDefaultStart(after)), toZonedDate(setDefaultEnd(before)), sort, sortDirection, start,
                maxItems);
    }

    @RequestMapping(value = "/calendarevents/event/{objectType}/{objectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmCalendarEvent getEvent(HttpSession session, Authentication auth, @PathVariable(value = "objectType") String objectType,
            @PathVariable(value = "objectId") String objectId, @RequestParam(value = "eventId") String eventId,
            @RequestParam(value = "retrieveMaster", required = false, defaultValue = "false") boolean retrieveMaster)
            throws CalendarServiceException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        AcmCalendar calendar = calendarService.retrieveCalendar(user, auth, objectType, objectId)
                .orElseThrow(() -> new CalendarServiceConfigurationException(
                        "Error while retrieving calendar configuration, most likely integration not enabled."));
        return calendar.getEvent(eventId, retrieveMaster);
    }

    @RequestMapping(value = "/calendarevents/attachment/{objectType}/{objectId}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getEventAttachment(HttpSession session, Authentication auth,
            @PathVariable(value = "objectType") String objectType, @PathVariable(value = "objectId") String objectId,
            @RequestParam(value = "eventId") String eventId, @RequestParam(value = "attachmentId") String attachmentId)
            throws CalendarServiceException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        AcmCalendar calendar = calendarService.retrieveCalendar(user, auth, objectType, objectId)
                .orElseThrow(() -> new CalendarServiceConfigurationException(
                        "Error while retrieving calendar configuration, most likely integration not enabled."));
        AcmEventAttachmentDTO attachment = calendar.getEventAttachment(eventId, attachmentId);

        ResponseEntity<InputStreamResource> response = ResponseEntity.ok().headers(attachment.getHttpHeaders())
                .contentLength(attachment.getContentLength()).contentType(attachment.getMediaType())
                .body(new InputStreamResource(attachment.getContent()));
        return response;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = { "multipart/mixed", MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> addCalendarEvent(HttpSession session, Authentication auth, @RequestPart("data") AcmCalendarEvent calendarEvent,
            @RequestPart(value = "file", required = false) MultipartFile[] attachments) throws CalendarServiceException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        calendarService.addCalendarEvent(user, auth, calendarEvent, attachments);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = { "multipart/mixed", MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updateCalendarEvent(HttpSession session, Authentication auth,
            @RequestParam(value = "updateMaster", required = false, defaultValue = "false") boolean updateMaster,
            @RequestPart("data") AcmCalendarEvent calendarEvent, @RequestPart(value = "file", required = false) MultipartFile[] attachments)
            throws CalendarServiceException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        calendarService.updateCalendarEvent(user, auth, updateMaster, calendarEvent, attachments);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/calendarevents/{objectType}/{objectId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCalendarEvent(HttpSession session, Authentication auth,
            @PathVariable(value = "objectType") String objectType, @PathVariable(value = "objectId") String objectId,
            @RequestParam(value = "calendarEventId") String calendarEventId,
            @RequestParam(value = "deleteRecurring", required = false, defaultValue = "false") boolean deleteRecurring)
            throws CalendarServiceException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        calendarService.deleteCalendarEvent(user, auth, objectType, objectId, calendarEventId, deleteRecurring);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{objectType}/integration", method = RequestMethod.GET)
    public ResponseEntity<String> isIntegrationEnabled(@PathVariable(value = "objectType") String objectType)
            throws CalendarConfigurationException
    {
        CalendarConfiguration configuration = calendarAdminService.readConfiguration(false).getConfiguration(objectType);
        return ResponseEntity.status(HttpStatus.OK).body(Boolean.toString(configuration.isIntegrationEnabled()));
    }

    @ExceptionHandler(CalendarServiceException.class)
    @ResponseBody
    public ResponseEntity<?> handleConfigurationException(CalendarServiceException ce)
    {
        CalendarExceptionMapper<CalendarServiceException> exceptionMapper = calendarService.getExceptionMapper(ce);
        Object errorDetails = exceptionMapper.mapException(ce);
        return ResponseEntity.status(exceptionMapper.getStatusCode()).body(errorDetails);
    }

    /**
     * @param after
     * @return
     */
    private String setDefaultStart(String after)
    {
        if (after != null && !after.isEmpty())
        {
            return adjustDateTimeString(after);
        }
        return ZonedDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).toString();
    }

    /**
     * @param before
     * @return
     */
    private String setDefaultEnd(String before)
    {
        if (before != null && !before.isEmpty())
        {
            return adjustDateTimeString(before);
        }
        return ZonedDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).toString();
    }

    /**
     * @param after
     * @return
     */
    private ZonedDateTime toZonedDate(String date)
    {
        return ZonedDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
    }

    /**
     * @param calendarService
     *            the calendarService to set
     */
    public void setCalendarService(CalendarService calendarService)
    {
        this.calendarService = calendarService;
    }

    /**
     * @param calendarAdminService
     *            the calendarAdminService to set
     */
    public void setCalendarAdminService(CalendarAdminService calendarAdminService)
    {
        this.calendarAdminService = calendarAdminService;
    }

}
