package com.armedia.acm.calendar.service;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 28, 2017
 *
 */
public interface AcmCalendar
{

    AcmCalendarInfo getInfo();

    /**
     * @param after
     * @param before
     * @param sort
     * @param sortDirection
     * @param start
     * @param maxItems
     * @return
     */
    List<AcmCalendarEventInfo> listItems(ZonedDateTime after, ZonedDateTime before, String sort, String sortDirection, int start,
            int maxItems);

    /**
     * @param before
     * @param after
     * @param sort
     * @param sortDirection
     * @return
     */
    List<AcmCalendarEvent> listItems(ZonedDateTime after, ZonedDateTime before, String sort, String sortDirection);

    /**
     * @param eventId
     * @return
     */
    AcmCalendarEvent getEvent(String eventId);

}
