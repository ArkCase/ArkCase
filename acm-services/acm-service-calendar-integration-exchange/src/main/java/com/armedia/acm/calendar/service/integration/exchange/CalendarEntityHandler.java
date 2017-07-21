package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.calendar.config.service.CalendarConfiguration.PurgeOptions;
import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarEventInfo;
import com.armedia.acm.calendar.service.AcmCalendarInfo;
import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.security.core.Authentication;

import java.time.ZonedDateTime;
import java.util.List;

import microsoft.exchange.webservices.data.core.ExchangeService;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 13, 2017
 *
 */
public interface CalendarEntityHandler
{

    public static enum PermissionType
    {
        READ, WRITE, DELETE;
    }

    /**
     * @param objectId
     * @return
     */
    boolean isRestricted(String objectId);

    /**
     * @param exchangeService
     * @param user
     * @param auth
     * @param objectId
     */
    boolean checkPermission(ExchangeService exchangeService, AcmUser user, Authentication auth, String objectId,
            PermissionType permissionType) throws CalendarServiceException;

    /**
     * @param user
     * @param auth
     * @param sort
     * @param sortDirection
     * @param start
     * @param maxItems
     * @return
     */
    List<AcmCalendarInfo> listCalendars(ExchangeService service, AcmUser user, Authentication auth, String sort, String sortDirection,
            int start, int maxItems);

    /**
     * @param objectId
     * @return
     * @throws CalendarServiceException
     */
    String getCalendarId(String objectId) throws CalendarServiceException;

    /**
     * @param after
     * @param before
     * @param sort
     * @param sortDirection
     * @param start
     * @param maxItems
     * @return
     */
    List<AcmCalendarEventInfo> listItemsInfo(ExchangeService service, String objectId, ZonedDateTime after, ZonedDateTime before,
            String sort, String sortDirection, int start, int maxItems) throws CalendarServiceException;

    /**
     * @param objectId
     * @param after
     * @param before
     * @param sort
     * @param sortDirection
     * @return
     */
    List<AcmCalendarEvent> listItems(ExchangeService service, String objectId, ZonedDateTime after, ZonedDateTime before, String sort,
            String sortDirection, int start, int maxItems) throws CalendarServiceException;

    /**
     * @param service
     * @param purgeOptions
     * @param daysClosed
     */
    void purgeCalendars(ExchangeService service, PurgeOptions purgeOptions, Integer daysClosed);

}
