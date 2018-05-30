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

import com.armedia.acm.calendar.config.service.CalendarConfiguration;
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
     * @throws CalendarServiceException
     */
    Optional<AcmCalendar> retrieveCalendar(AcmUser user, Authentication auth, String objectType, String objectId)
            throws CalendarServiceException;

    /**
     * @param user
     * @param auth
     * @param maxItems
     * @param start
     * @param sortDirection
     * @param sort
     * @param objectType
     * @return
     * @throws CalendarServiceException
     */
    List<AcmCalendarInfo> listCalendars(AcmUser user, Authentication auth, String objectType, String sort, String sortDirection, int start,
            int maxItems) throws CalendarServiceException;

    /**
     * @param user
     * @param auth
     * @param calendarEvent
     * @param attachments
     * @throws CalendarServiceException
     */
    void addCalendarEvent(AcmUser user, Authentication auth, AcmCalendarEvent calendarEvent, MultipartFile[] attachments)
            throws CalendarServiceException;

    /**
     * @param user
     * @param auth
     * @param calendarEvent
     * @param attachments
     * @throws CalendarServiceException
     */
    void updateCalendarEvent(AcmUser user, Authentication auth, boolean updateMaster, AcmCalendarEvent calendarEvent,
            MultipartFile[] attachments) throws CalendarServiceException;

    /**
     * @param user
     * @param auth
     * @param calendarEventId
     * @throws CalendarServiceException
     */
    void deleteCalendarEvent(AcmUser user, Authentication auth, String objectType, String objectId, String calendarEventId,
            boolean deleteRecurring) throws CalendarServiceException;

    /**
     * @param objectType
     * @param config
     * @throws CalendarServiceException
     */
    void purgeEvents(String objectType, CalendarConfiguration config) throws CalendarServiceException;

    <CSE extends CalendarServiceException> CalendarExceptionMapper<CSE> getExceptionMapper(CalendarServiceException e);

}
