package com.armedia.acm.calendar.web.api;

import com.armedia.acm.calendar.service.AcmCalendar;
import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarEventInfo;
import com.armedia.acm.calendar.service.AcmCalendarInfo;
import com.armedia.acm.calendar.service.CalendarExceptionMapper;
import com.armedia.acm.calendar.service.CalendarService;
import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.services.users.model.AcmUser;

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
                .orElseThrow(() -> new CalendarServiceException(""));
        return calendar.getInfo();
    }

    @RequestMapping(value = "/calendarevents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmCalendarEventInfo> listEvents(HttpSession session, Authentication auth,
            @RequestParam(value = "objectType") String objectType, @RequestParam(value = "objectId") String objectId,
            @RequestParam(value = "after", required = false) String after, @RequestParam(value = "before", required = false) String before,
            @RequestParam(value = "sort", required = false, defaultValue = "eventDate") String sort,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "maxItems", required = false, defaultValue = "50") int maxItems) throws CalendarServiceException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        AcmCalendar calendar = calendarService.retrieveCalendar(user, auth, objectType, objectId)
                .orElseThrow(() -> new CalendarServiceException(""));
        return calendar.listItems(toZonedDate(after), toZonedDate(before), sort, sortDirection, start, maxItems);
    }

    @RequestMapping(value = "/calendarevents/{objectType}/{objectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmCalendarEvent> listEventsFullDetails(HttpSession session, Authentication auth,
            @PathVariable(value = "objectType") String objectType, @PathVariable(value = "objectId") String objectId,
            @RequestParam(value = "after", required = false) String after, @RequestParam(value = "before", required = false) String before,
            @RequestParam(value = "sort", required = false, defaultValue = "eventDate") String sort,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection)
            throws CalendarServiceException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        AcmCalendar calendar = calendarService.retrieveCalendar(user, auth, objectType, objectId)
                .orElseThrow(() -> new CalendarServiceException(""));
        return calendar.listItems(toZonedDate(after), toZonedDate(before), sort, sortDirection);
    }

    @RequestMapping(value = "/calendarevents/{objectType}/{objectId}/{eventId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmCalendarEvent getEvent(HttpSession session, Authentication auth, @PathVariable(value = "objectType") String objectType,
            @PathVariable(value = "objectId") String objectId, @PathVariable(value = "eventId") String eventId)
            throws CalendarServiceException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        AcmCalendar calendar = calendarService.retrieveCalendar(user, auth, objectType, objectId)
                .orElseThrow(() -> new CalendarServiceException(""));
        return calendar.getEvent(eventId);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addCalendarEvent(HttpSession session, Authentication auth,
            @RequestParam(value = "calendarId", required = false) String calendarId, @RequestPart("event") AcmCalendarEvent calendarEvent,
            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments) throws CalendarServiceException

    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        calendarService.addCalendarEvent(user, auth, calendarId, calendarEvent, attachments);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCalendarEvent(HttpSession session, Authentication auth,
            @RequestPart("event") AcmCalendarEvent calendarEvent,
            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments) throws CalendarServiceException

    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        calendarService.updateCalendarEvent(user, auth, calendarEvent, attachments);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/calendarevents/{objectType}/{objectId}/{eventId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCalendarEvent(HttpSession session, Authentication auth,
            @PathVariable(value = "objectType") String objectType, @PathVariable(value = "objectId") String objectId,
            @PathVariable(value = "calendarEventId") String calendarEventId) throws CalendarServiceException

    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        calendarService.deleteCalendarEvent(user, auth, objectType, objectId, calendarEventId);
        return new ResponseEntity<>(HttpStatus.OK);
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

}
