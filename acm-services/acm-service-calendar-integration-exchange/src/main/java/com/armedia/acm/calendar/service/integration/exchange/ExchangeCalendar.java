package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.calendar.service.AcmCalendar;
import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarEventInfo;
import com.armedia.acm.calendar.service.AcmCalendarInfo;
import com.armedia.acm.calendar.service.CalendarServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.List;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
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

    private Logger log = LoggerFactory.getLogger(getClass());

    private ExchangeService service;

    private CalendarEntityHandler handler;

    private String objectType;

    private String objectId;

    /**
     * @param service
     * @param handler
     * @param objectType
     * @param objectId
     */
    public ExchangeCalendar(ExchangeService service, CalendarEntityHandler handler, String objectType, String objectId)
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
        log.debug("Getting calendar info for object with id: [{}] of [{}] type.", objectId, objectType);
        try
        {
            CalendarFolder folder = CalendarFolder.bind(service, new FolderId(handler.getCalendarId(objectId)));
            // TODO: fill out the 'description' properly.
            return new AcmCalendarInfo(folder.getId().getUniqueId(), objectType, objectId, folder.getDisplayName(), "");
        } catch (Exception e)
        {
            log.warn("Error while trying to get calendar info for object with id: [{}] of [{}] type.", objectId, objectType, e);
            throw new CalendarServiceBindToRemoteException(e);
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
    public AcmCalendarEvent getEvent(String eventId, boolean retrieveMaster) throws CalendarServiceException
    {

        log.debug("Getting calendar item for event id [{}] for object with id: [{}] of [{}] type.", eventId, objectId, objectType);

        Appointment appointment;
        try
        {
            appointment = Appointment.bind(service, new ItemId(eventId));
        } catch (Exception e)
        {
            log.warn("Error while trying to bind to calendar folder for object with id: [{}] of [{}] type.", objectId, objectType, e);
            throw new CalendarServiceBindToRemoteException(e);
        }

        try
        {
            PropertySet allProperties = new PropertySet();
            allProperties.addRange(PropertyDefinitionHolder.standardProperties);
            appointment.load(allProperties);

            AcmCalendarEvent event = new AcmCalendarEvent();
            if (appointment.getIsRecurring())
            {
                Appointment recurringMaster = Appointment.bindToRecurringMaster(service, new ItemId(eventId));
                recurringMaster.load(allProperties);
                if (retrieveMaster)
                {
                    ExchangeTypesConverter.setEventProperties(event, recurringMaster);
                    return event;
                }
                appointment.setRecurrence(recurringMaster.getRecurrence());
            }

            ExchangeTypesConverter.setEventProperties(event, appointment);
            return event;
        } catch (Exception e)
        {
            log.warn("Error while trying to get event with {} id for object with id: {} of {} type.", eventId, objectId, objectType, e);
            throw new CalendarServiceException(e);
        }
    }

}
