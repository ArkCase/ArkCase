package com.armedia.acm.calendar.service;

/*-
 * #%L
 * ACM Service: Calendar Service
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
     * @param retrieveMaster
     * @return
     * @throws CalendarServiceException
     */
    AcmCalendarEvent getEvent(String eventId, boolean retrieveMaster) throws CalendarServiceException;

    /**
     * @param eventId
     * @param attachmentId
     * @return
     * @throws CalendarServiceException
     */
    AcmEventAttachmentDTO getEventAttachment(String eventId, String attachmentId) throws CalendarServiceException;

}
