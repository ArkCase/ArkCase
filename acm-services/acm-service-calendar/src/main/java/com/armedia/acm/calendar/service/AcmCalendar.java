package com.armedia.acm.calendar.service;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 28, 2017
 *
 */
public interface AcmCalendar
{

    AcmCalendarInfo getInfo() throws CalendarServiceException;

    /**
     * @param after
     * @param before
     * @param sort
     * @param sortDirection
     * @param start
     * @param maxItems
     * @return
     * @throws CalendarServiceException
     */
    List<AcmCalendarEventInfo> listItemsInfo(ZonedDateTime after, ZonedDateTime before, String sort, String sortDirection, int start,
            int maxItems) throws CalendarServiceException;

    /**
     * @param before
     * @param after
     * @param sort
     * @param sortDirection
     * @return
     * @throws CalendarServiceException
     */
    List<AcmCalendarEvent> listItems(ZonedDateTime after, ZonedDateTime before, String sort, String sortDirection, int start, int maxItems)
            throws CalendarServiceException;

    /**
     * @param eventId
     * @return
     * @throws CalendarServiceException
     */
    AcmCalendarEvent getEvent(String eventId) throws CalendarServiceException;

}
