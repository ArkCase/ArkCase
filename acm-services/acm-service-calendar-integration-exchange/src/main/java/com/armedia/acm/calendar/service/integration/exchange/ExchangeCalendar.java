package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.calendar.service.AcmCalendar;
import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarEventInfo;
import com.armedia.acm.calendar.service.AcmCalendarInfo;
import com.armedia.acm.calendar.service.CalendarServiceException;

import java.time.ZonedDateTime;
import java.util.List;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 12, 2017
 *
 */
public class ExchangeCalendar implements AcmCalendar
{

    private ExchangeService service;

    private EntityHandler handler;

    private String objectType;

    private String objectId;

    /**
     * @param service
     * @param handler
     * @param objectType
     * @param objectId
     */
    public ExchangeCalendar(ExchangeService service, EntityHandler handler, String objectType, String objectId)
    {
        this.service = service;
        this.handler = handler;
        this.objectType = objectType;
        this.objectId = objectId;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.service.AcmCalendar#getInfo()
     */
    @Override
    public AcmCalendarInfo getInfo() throws CalendarServiceException
    {
        // return handler.getCalendarInfo(service, objectType, objectId);
        String calendarId = handler.getCalendarId(objectId);
        try
        {
            CalendarFolder folder = CalendarFolder.bind(service, new FolderId(calendarId));
            // TODO: fill out the 'description' properly.
            return new AcmCalendarInfo(calendarId, objectType, objectId, folder.getDisplayName(), "");
        } catch (Exception e)
        {
            throw new CalendarServiceException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.service.AcmCalendar#listItems(java.time.ZonedDateTime, java.time.ZonedDateTime,
     * java.lang.String, java.lang.String, int, int)
     */
    @Override
    public List<AcmCalendarEventInfo> listItemsInfo(ZonedDateTime after, ZonedDateTime before, String sort, String sortDirection, int start,
            int maxItems) throws CalendarServiceException
    {
        return handler.listItemsInfo(service, objectId, after, before, sort, sortDirection, start, maxItems);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.service.AcmCalendar#listItems(java.time.ZonedDateTime, java.time.ZonedDateTime,
     * java.lang.String, java.lang.String)
     */
    @Override
    public List<AcmCalendarEvent> listItems(ZonedDateTime after, ZonedDateTime before, String sort, String sortDirection, int start,
            int maxItems) throws CalendarServiceException
    {
        return handler.listItems(service, objectId, after, before, sort, sortDirection, start, maxItems);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.service.AcmCalendar#getEvent(java.lang.String)
     */
    @Override
    public AcmCalendarEvent getEvent(String eventId) throws CalendarServiceException
    {
        try
        {
            Appointment appointment = Appointment.bind(service, new ItemId(eventId));
            AcmCalendarEvent event = new AcmCalendarEvent();
            ExchangeTypesConverter.setEventProperties(event, appointment);
            return event;
        } catch (Exception e)
        {
            throw new CalendarServiceException(e);
        }
    }

}
