/**
 *
 */
package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarEventInfo;
import com.armedia.acm.calendar.service.AcmCalendarInfo;
import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.permission.folder.FolderPermissionLevel;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.SortDirection;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.FolderPermission;
import microsoft.exchange.webservices.data.property.complex.FolderPermissionCollection;
import microsoft.exchange.webservices.data.property.definition.PropertyDefinition;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 11, 2017
 *
 */
public abstract class CalendarEntityHandlerBase implements CalendarEntityHandler
{

    @PersistenceContext
    private EntityManager em;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Map<String, PropertyDefinition> sortFields;

    /**
     *
     */
    public CalendarEntityHandlerBase()
    {
        sortFields = new HashMap<>();
        sortFields.put("subject", ItemSchema.Subject);
        sortFields.put("dateTimeCreated", ItemSchema.DateTimeCreated);
        sortFields.put("dateTimeReceived", ItemSchema.DateTimeReceived);
        sortFields.put("dateTimeSent", ItemSchema.DateTimeSent);
        sortFields.put("hasAttachments", ItemSchema.HasAttachments);
        sortFields.put("displayTo", ItemSchema.DisplayTo);
        sortFields.put("size", ItemSchema.Size);
        sortFields.put("dateTimeStart", AppointmentSchema.Start);
    }

    protected abstract String getEntityType();

    /**
     * @param objectId
     * @param restrictedOnly
     * @return
     */
    protected abstract Object getEntity(String objectId, boolean restrictedOnly);

    @Override
    public boolean isRestricted(String objectId)
    {
        return getEntity(objectId, true) != null;
    }

    @Override
    public boolean checkPermission(ExchangeService service, AcmUser user, Authentication auth, String objectId,
            PermissionType permissionType) throws CalendarServiceException
    {
        Object caseFile = getEntity(objectId, true);
        if (caseFile == null)
        {
            return true;
        }
        AcmContainerEntity entity = (AcmContainerEntity) caseFile;
        String calendarId = entity.getContainer().getCalendarFolderId();
        try
        {
            CalendarFolder folder = CalendarFolder.bind(service, new FolderId(calendarId));
            FolderPermissionCollection permissions = folder.getPermissions();
            for (FolderPermission permission : permissions.getItems())
            {
                if (permission.getUserId().getPrimarySmtpAddress().equals(user.getMail()))
                {
                    if (hasPermission(permission.getPermissionLevel(), permissionType))
                    {
                        return true;
                    }
                }
            }
        } catch (Exception e)
        {
            log.debug("Error while evaluationg permission of user {} to object with {} id of type {}.", user.getFullName(), objectId,
                    getEntityType(), e);
            throw new CalendarServiceException(e);
        }
        return false;
    }

    /**
     * @param permissionLevel
     * @param permissionType
     * @return
     */
    private boolean hasPermission(FolderPermissionLevel permissionLevel, PermissionType permissionType)
    {
        switch (permissionType)
        {
        case READ:
            return permissionLevel.equals(FolderPermissionLevel.Owner) || permissionLevel.equals(FolderPermissionLevel.PublishingEditor)
                    || permissionLevel.equals(FolderPermissionLevel.Editor)
                    || permissionLevel.equals(FolderPermissionLevel.PublishingAuthor)
                    || permissionLevel.equals(FolderPermissionLevel.Author)
                    || permissionLevel.equals(FolderPermissionLevel.NoneditingAuthor)
                    || permissionLevel.equals(FolderPermissionLevel.Reviewer) || permissionLevel.equals(FolderPermissionLevel.Contributor);
        case WRITE:
            return permissionLevel.equals(FolderPermissionLevel.Owner) || permissionLevel.equals(FolderPermissionLevel.PublishingEditor)
                    || permissionLevel.equals(FolderPermissionLevel.Editor)
                    || permissionLevel.equals(FolderPermissionLevel.PublishingAuthor)
                    || permissionLevel.equals(FolderPermissionLevel.Author) || permissionLevel.equals(FolderPermissionLevel.Contributor);

        case DELETE:
            return permissionLevel.equals(FolderPermissionLevel.Owner) || permissionLevel.equals(FolderPermissionLevel.PublishingAuthor)
                    || permissionLevel.equals(FolderPermissionLevel.Author);
        default:
            return false;
        }
    }

    @Override
    public List<AcmCalendarInfo> listCalendars(ExchangeService service, AcmUser user, Authentication auth, String sort,
            String sortDirection, int start, int maxItems)
    {
        throw new UnsupportedOperationException("This operation is not supported by Exchnage.");
    }

    @Override
    public String getCalendarId(String objectId) throws CalendarServiceException
    {
        Object entity = getEntity(objectId, false);
        if (entity == null)
        {
            throw new CalendarServiceException(String.format("No calendar associated with %s with id %s.", getEntityType(), objectId));
        }
        AcmContainerEntity containerEntity = (AcmContainerEntity) entity;
        return containerEntity.getContainer().getCalendarFolderId();
    }

    @Override
    public List<AcmCalendarEventInfo> listItemsInfo(ExchangeService service, String objectId, boolean restricted, ZonedDateTime after,
            ZonedDateTime before, String sort, String sortDirection, int start, int maxItems) throws CalendarServiceException
    {
        try
        {
            FindItemsResults<Appointment> findResults = retreiveAppointments(service, after, before, sort, sortDirection, start, maxItems,
                    objectId, restricted);

            List<AcmCalendarEventInfo> events = new ArrayList<>();
            for (Appointment appointment : findResults.getItems())
            {
                AcmCalendarEventInfo event = new AcmCalendarEventInfo();
                event.setCalendarId(appointment.getParentFolderId().getUniqueId());
                event.setCreatorId(appointment.getOrganizer().getAddress());
                event.setEventId(appointment.getId().getUniqueId());
                event.setObjectId(objectId);
                event.setObjectType(getEntityType());
                event.setSubject(appointment.getSubject());

                events.add(event);
            }
            return events;
        } catch (Exception e)
        {
            log.debug("Error while trying to retrieve appointment items info for ibject with {} it, of {} type..", objectId,
                    getEntityType(), e);
            throw new CalendarServiceException(e);
        }
    }

    @Override
    public List<AcmCalendarEvent> listItems(ExchangeService service, String objectId, boolean restricted, ZonedDateTime after,
            ZonedDateTime before, String sort, String sortDirection, int start, int maxItems) throws CalendarServiceException
    {

        try
        {
            FindItemsResults<Appointment> findResults = retreiveAppointments(service, after, before, sort, sortDirection, start, maxItems,
                    objectId, restricted);

            List<AcmCalendarEvent> events = new ArrayList<>();
            for (Appointment appointment : findResults.getItems())
            {
                AcmCalendarEvent event = new AcmCalendarEvent();
                ExchangeTypesConverter.setEventProperties(event, appointment);
                event.setObjectId(objectId);
                event.setCalendarId(appointment.getParentFolderId().getUniqueId());
                events.add(event);
            }
            return events;
        } catch (Exception e)
        {
            log.debug("Error while trying to retrieve appointment items details for ibject with {} it, of {} type..", objectId,
                    getEntityType(), e);
            throw new CalendarServiceException(e);
        }
    }

    /**
     * @param service
     * @param after
     * @param before
     * @param sort
     * @param sortDirection
     * @param start
     * @param maxItems
     * @param objectId
     * @return
     * @throws ServiceLocalException
     * @throws Exception
     */
    private FindItemsResults<Appointment> retreiveAppointments(ExchangeService service, ZonedDateTime after, ZonedDateTime before,
            String sort, String sortDirection, int start, int maxItems, String objectId, boolean restricted)
            throws ServiceLocalException, Exception
    {
        ItemView view = new ItemView(maxItems, start);
        Date startDate = Date.from(after.toInstant());
        Date endDate = Date.from(before.toInstant());
        CalendarView calendarView = new CalendarView(startDate, endDate, maxItems);

        PropertyDefinition orderBy = sort == null || sort.trim().isEmpty() || !sortFields.containsKey(sort) ? ItemSchema.DateTimeReceived
                : sortFields.get(sort);

        view.getOrderBy().add(orderBy, "ASC".equals(sortDirection) ? SortDirection.Ascending : SortDirection.Descending);

        PropertySet allProperties = new PropertySet();
        allProperties.addRange(PropertyDefinitionHolder.standardProperties);

        FindItemsResults<Appointment> findResults = restricted
                ? service.findAppointments(new FolderId(getCalendarId(objectId)), calendarView)
                : service.findAppointments(WellKnownFolderName.Calendar, calendarView);
        if (!findResults.getItems().isEmpty())
        {
            List<Item> appointmentItems = findResults.getItems().stream().map(item -> {
                return (Item) item;
            }).collect(Collectors.toList());
            service.loadPropertiesForItems(appointmentItems, allProperties);
        }
        return findResults;
    }

    /**
     * @return the em
     */
    protected EntityManager getEm()
    {
        return em;
    }

}