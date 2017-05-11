package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.calendar.service.AcmCalendar;
import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarEventInfo;
import com.armedia.acm.calendar.service.AcmCalendarInfo;
import com.armedia.acm.calendar.service.CalendarServiceException;

import java.time.ZonedDateTime;
import java.util.List;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
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

    private CalendarEntityHandler handler;

    private String objectType;

    private String objectId;

    private boolean restricted;

    /**
     * @param service
     * @param handler
     * @param objectType
     * @param objectId
     * @param restricted
     */
    public ExchangeCalendar(ExchangeService service, CalendarEntityHandler handler, String objectType, String objectId, boolean restricted)
    {
        this.service = service;
        this.handler = handler;
        this.objectType = objectType;
        this.objectId = objectId;
        this.restricted = restricted;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.service.AcmCalendar#getInfo()
     */
    @Override
    public AcmCalendarInfo getInfo() throws CalendarServiceException
    {
        try
        {
            CalendarFolder folder = restricted ? CalendarFolder.bind(service, new FolderId(handler.getCalendarId(objectId)))
                    : CalendarFolder.bind(service, WellKnownFolderName.Calendar);
            // TODO: fill out the 'description' properly.
            return new AcmCalendarInfo(folder.getId().getUniqueId(), objectType, objectId, folder.getDisplayName(), "");
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
        return handler.listItemsInfo(service, objectId, restricted, after, before, sort, sortDirection, start, maxItems);
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
        return handler.listItems(service, objectId, restricted, after, before, sort, sortDirection, start, maxItems);
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
            PropertySet allProperties = new PropertySet();
            allProperties.addRange(PropertyDefinitionHolder.standardProperties);
            appointment.load(allProperties);

            if (appointment.getIsRecurring())
            {
                Appointment recurringMaster = Appointment.bindToRecurringMaster(service, new ItemId(eventId));
                recurringMaster.load(allProperties);
                appointment.setRecurrence(recurringMaster.getRecurrence());
            }

            AcmCalendarEvent event = new AcmCalendarEvent();
            ExchangeTypesConverter.setEventProperties(event, appointment);
            return event;
        } catch (Exception e)
        {
            throw new CalendarServiceException(e);
        }
    }

}
