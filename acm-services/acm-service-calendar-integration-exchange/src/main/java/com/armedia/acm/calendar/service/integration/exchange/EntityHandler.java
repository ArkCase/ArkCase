package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarInfo;
import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.security.core.Authentication;

import java.util.List;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.folder.Folder;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 13, 2017
 *
 */
public interface EntityHandler
{

    /**
     * @param user
     * @param auth
     * @param objectId
     */
    boolean checkPermission(AcmUser user, Authentication auth, String objectId) throws CalendarServiceException;

    /**
     * @param user
     * @param auth
     * @param objectType
     * @param sort
     * @param sortDirection
     * @param start
     * @param maxItems
     * @return
     */
    List<AcmCalendarInfo> listCalendars(AcmUser user, Authentication auth, String objectType, String sort, String sortDirection, int start,
            int maxItems);

    /**
     * @param exchange
     * @param calendarEvent
     * @return
     */
    Folder getFolder(ExchangeService exchange, AcmCalendarEvent calendarEvent);

}
