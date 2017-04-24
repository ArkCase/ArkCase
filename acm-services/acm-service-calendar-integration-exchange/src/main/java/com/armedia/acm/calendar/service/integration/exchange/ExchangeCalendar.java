package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.calendar.service.AcmCalendar;
import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarEventInfo;
import com.armedia.acm.calendar.service.AcmCalendarInfo;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 12, 2017
 *
 */
public class ExchangeCalendar implements AcmCalendar
{

    /**
     * @param handler
     * @param objectType
     * @param objectId
     */
    public ExchangeCalendar(EntityHandler handler, String objectType, String objectId)
    {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.service.AcmCalendar#getInfo()
     */
    @Override
    public AcmCalendarInfo getInfo()
    {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.service.AcmCalendar#listItems(java.time.ZonedDateTime, java.time.ZonedDateTime,
     * java.lang.String, java.lang.String, int, int)
     */
    @Override
    public List<AcmCalendarEventInfo> listItems(ZonedDateTime after, ZonedDateTime before, String sort, String sortDirection, int start,
            int maxItems)
    {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.service.AcmCalendar#listItems(java.time.ZonedDateTime, java.time.ZonedDateTime,
     * java.lang.String, java.lang.String)
     */
    @Override
    public List<AcmCalendarEvent> listItems(ZonedDateTime after, ZonedDateTime before, String sort, String sortDirection)
    {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.service.AcmCalendar#getEvent(java.lang.String)
     */
    @Override
    public AcmCalendarEvent getEvent(String eventId)
    {
        throw new UnsupportedOperationException();
    }

}
