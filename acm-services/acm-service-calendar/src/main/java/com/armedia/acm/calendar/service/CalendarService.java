package com.armedia.acm.calendar.service;

import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 28, 2017
 *
 */
public interface CalendarService
{

    /**
     * @param user
     * @param auth
     * @param objectType
     * @param objectId
     * @return
     * @throws CalendarServiceException
     */
    Optional<AcmCalendar> retrieveCalendar(AcmUser user, Authentication auth, String objectType, String objectId)
            throws CalendarServiceException;

    /**
     * @param user
     * @param auth
     * @param maxItems
     * @param start
     * @param sortDirection
     * @param sort
     * @param objectType
     * @return
     * @throws CalendarServiceException
     */
    List<AcmCalendarInfo> listCalendars(AcmUser user, Authentication auth, String objectType, String sort, String sortDirection, int start,
            int maxItems) throws CalendarServiceException;

    /**
     * @param user
     * @param auth
     * @param calendarId
     * @param calendarEvent
     * @param attachments
     * @throws CalendarServiceException
     */
    void addCalendarEvent(AcmUser user, Authentication auth, String calendarId, AcmCalendarEvent calendarEvent, MultipartFile[] attachments)
            throws CalendarServiceException;

    /**
     * @param user
     * @param auth
     * @param calendarEvent
     * @param attachments
     * @throws CalendarServiceException
     */
    void updateCalendarEvent(AcmUser user, Authentication auth, AcmCalendarEvent calendarEvent, MultipartFile[] attachments)
            throws CalendarServiceException;

    /**
     * @param user
     * @param auth
     * @param calendarEventId
     * @throws CalendarServiceException
     */
    void deleteCalendarEvent(AcmUser user, Authentication auth, String objectType, String objectId, String calendarEventId)
            throws CalendarServiceException;

    <CSE extends CalendarServiceException> CalendarExceptionMapper<CSE> getExceptionMapper(CalendarServiceException e);

}
