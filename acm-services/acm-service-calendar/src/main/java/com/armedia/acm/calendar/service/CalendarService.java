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
     */
    Optional<AcmCalendar> retrieveCalendar(AcmUser user, Authentication auth, String objectType, String objectId);

    /**
     * @param user
     * @param auth
     * @param maxItems
     * @param start
     * @param sortDirection
     * @param sort
     * @param objectType
     * @return
     */
    List<AcmCalendarInfo> listCalendars(AcmUser user, Authentication auth, String objectType, String sort, String sortDirection, int start,
            int maxItems);

    /**
     * @param user
     * @param auth
     * @param calendarId
     * @param calendarEvent
     * @param attachments
     */
    void addCalendarEvent(AcmUser user, Authentication auth, String calendarId, AcmCalendarEvent calendarEvent,
            MultipartFile[] attachments);

    /**
     * @param user
     * @param auth
     * @param calendarEvent
     * @param attachments
     */
    void updateCalendarEvent(AcmUser user, Authentication auth, AcmCalendarEvent calendarEvent, MultipartFile[] attachments);

    /**
     * @param user
     * @param auth
     * @param calendarEventId
     */
    void deleteCalendarEvent(AcmUser user, Authentication auth, String calendarEventId);

    <CSE extends CalendarServiceException> CalendarExceptionMapper<CSE> getExceptionMapper(CalendarServiceException e);

}
