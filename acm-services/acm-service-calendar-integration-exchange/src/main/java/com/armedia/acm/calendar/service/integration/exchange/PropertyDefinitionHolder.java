package com.armedia.acm.calendar.service.integration.exchange;

/*-
 * #%L
 * ACM Service: Exchange Integration Calendar Service
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
