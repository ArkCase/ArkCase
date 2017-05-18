package com.armedia.acm.calendar.service.integration.exchange;

import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 10, 2017
 *
 */
public class PropertyDefinitionHolder
{

    static final PropertySet standardProperties = new PropertySet(BasePropertySet.FirstClassProperties, ItemSchema.Subject,
            AppointmentSchema.Location, AppointmentSchema.Start, AppointmentSchema.StartTimeZone, AppointmentSchema.End,
            AppointmentSchema.EndTimeZone, AppointmentSchema.IsAllDayEvent, ItemSchema.DateTimeSent, ItemSchema.DateTimeCreated,
            ItemSchema.DateTimeReceived, ItemSchema.LastModifiedTime, ItemSchema.Body, ItemSchema.Size, AppointmentSchema.IsCancelled,
            AppointmentSchema.IsMeeting, AppointmentSchema.IsRecurring, ItemSchema.ParentFolderId, ItemSchema.IsReminderSet,
            ItemSchema.ReminderMinutesBeforeStart, AppointmentSchema.Sensitivity, AppointmentSchema.Importance,
            AppointmentSchema.RequiredAttendees, AppointmentSchema.OptionalAttendees, AppointmentSchema.Resources,
            AppointmentSchema.Recurrence, AppointmentSchema.Organizer, ItemSchema.Attachments);

}
