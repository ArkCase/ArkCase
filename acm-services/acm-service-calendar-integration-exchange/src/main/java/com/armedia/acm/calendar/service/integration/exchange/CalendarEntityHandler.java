package com.armedia.acm.calendar.service.integration.exchange;

import static com.armedia.acm.calendar.service.integration.exchange.ExchangeCalendarService.PROCESS_USER;

import com.armedia.acm.calendar.config.service.CalendarConfiguration.PurgeOptions;
import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarEventInfo;
import com.armedia.acm.calendar.service.AcmCalendarInfo;
import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.permission.folder.FolderPermissionLevel;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.FolderPermission;
import microsoft.exchange.webservices.data.property.complex.FolderPermissionCollection;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.definition.PropertyDefinition;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.search.FindItemsResults;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 11, 2017
 *
 */
public class CalendarEntityHandler
{

    public static enum PermissionType
    {
        READ, WRITE, DELETE;
    }

    @FunctionalInterface
    public static interface ServiceConnector
    {
        Optional<ExchangeService> connect(Long objectId);
    }

    @PersistenceContext
    private EntityManager em;

    private Logger log = LoggerFactory.getLogger(getClass());

    private OutlookDao outlookDao;

    private AcmContainerDao containerEntityDao;

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    protected Map<String, PropertyDefinition> sortFields;

    private List<String> closedStates;

    private String entityType;

    private String entityTypeForQuery;

    private String entityIdForQuery;

    public CalendarEntityHandler()
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

    public boolean isRestricted(String objectId)
    {
        // return getEntity(objectId, true) != null;
        // Always return false temporarily until there is a clarification of what `restricted` object means in context
        // of the calendar enhancements.
        return false;
    }

    public boolean checkPermission(ExchangeService service, AcmUser user, Authentication auth, String objectId,
            PermissionType permissionType) throws CalendarServiceException
    {
        AcmContainerEntity entity = getEntity(objectId, true);
        if (entity == null)
        {
            return true;
        }
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
                    entityType, e);
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

    public List<AcmCalendarInfo> listCalendars(ServiceConnector connector, AcmUser user, Authentication auth, String sort,
            String sortDirection, int start, int maxItems)
    {
        throw new UnsupportedOperationException("This operation is not supported by Exchnage.");
    }

    public String getCalendarId(String objectId) throws CalendarServiceException
    {
        AcmContainerEntity entity = getEntity(objectId, false);
        if (entity == null)
        {
            throw new CalendarServiceException(String.format("No calendar associated with %s with id %s.", entityType, objectId));
        }
        return entity.getContainer().getCalendarFolderId();
    }

    public List<AcmCalendarEventInfo> listItemsInfo(ExchangeService service, String objectId, ZonedDateTime after, ZonedDateTime before,
            String sort, String sortDirection, int start, int maxItems) throws CalendarServiceException
    {
        try
        {
            FindItemsResults<Appointment> findResults = retreiveAppointments(service, after, before, sort, sortDirection, start, maxItems,
                    objectId);

            List<AcmCalendarEventInfo> events = new ArrayList<>();
            for (Appointment appointment : findResults.getItems())
            {
                AcmCalendarEventInfo event = new AcmCalendarEventInfo();
                event.setCalendarId(appointment.getParentFolderId().getUniqueId());
                event.setCreatorId(appointment.getOrganizer().getAddress());
                event.setEventId(appointment.getId().getUniqueId());
                event.setObjectId(objectId);
                event.setObjectType(entityType);
                event.setSubject(appointment.getSubject());

                events.add(event);
            }
            return events;
        } catch (Exception e)
        {
            log.debug("Error while trying to retrieve appointment items info for Object with {} id, of {} type.", objectId, entityType, e);
            throw new CalendarServiceException(e);
        }
    }

    public List<AcmCalendarEvent> listItems(ExchangeService service, String objectId, ZonedDateTime after, ZonedDateTime before,
            String sort, String sortDirection, int start, int maxItems) throws CalendarServiceException
    {

        try
        {
            FindItemsResults<Appointment> findResults = retreiveAppointments(service, after, before, sort, sortDirection, start, maxItems,
                    objectId);

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
            log.debug("Error while trying to retrieve appointment items details for object with {} id, of {} type.", objectId, entityType,
                    e);
            throw new CalendarServiceException(e);
        }
    }

    /**
     * @param objectId
     * @param restrictedOnly
     * @return
     */
    private AcmContainerEntity getEntity(String objectId, boolean restrictedOnly)
    {
        TypedQuery<AcmContainerEntity> query;
        if (restrictedOnly)
        {
            query = em.createQuery(String.format("SELECT ot FROM %s ot WHERE ot.%s = :objectId AND ot.restricted = :restricted",
                    entityTypeForQuery, entityIdForQuery), AcmContainerEntity.class);
            query.setParameter("restricted", true);
        } else
        {
            query = em.createQuery(String.format("SELECT ot FROM %s ot WHERE ot.%s = :objectId", entityTypeForQuery, entityIdForQuery),
                    AcmContainerEntity.class);
        }
        query.setParameter("objectId", Long.valueOf(objectId));
        List<AcmContainerEntity> resultList = query.getResultList();
        if (!resultList.isEmpty())
        {
            return resultList.get(0);
        } else
        {
            return null;
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
            String sort, String sortDirection, int start, int maxItems, String objectId) throws ServiceLocalException, Exception
    {
        Date startDate = Date.from(after.toInstant());
        Date endDate = Date.from(before.toInstant());
        CalendarView calendarView = new CalendarView(startDate, endDate, maxItems);

        PropertyDefinition orderBy = sort == null || sort.trim().isEmpty() || !sortFields.containsKey(sort) ? ItemSchema.DateTimeReceived
                : sortFields.get(sort);

        calendarView.setPropertySet(new PropertySet(AppointmentSchema.Subject, AppointmentSchema.Start, AppointmentSchema.End, orderBy));

        CalendarFolder calendar = CalendarFolder.bind(service, new FolderId(getCalendarId(objectId)));
        FindItemsResults<Appointment> findResults = calendar.findAppointments(calendarView);

        if (!findResults.getItems().isEmpty())
        {
            PropertySet allProperties = new PropertySet();
            allProperties.addRange(PropertyDefinitionHolder.standardProperties);

            List<Item> appointmentItems = findResults.getItems().stream().map(item -> {
                return (Item) item;
            }).collect(Collectors.toList());
            service.loadPropertiesForItems(appointmentItems, allProperties);
        }
        return findResults;
    }

    /**
     *
     * @param connector
     * @param purgeOptions
     * @param daysClosed
     */
    public void purgeCalendars(ServiceConnector connector, PurgeOptions purgeOptions, Integer daysClosed)
    {
        switch (purgeOptions)
        {
        case RETAIN_INDEFINITELY:
            return;
        case CLOSED:
        case CLOSED_X_DAYS:
            List<AcmContainerEntity> purgeCandidates = getEntities(daysClosed);
            purgeCalendars(connector, purgeCandidates);
            break;
        }

    }

    private List<AcmContainerEntity> getEntities(Integer daysClosed)
    {
        TypedQuery<AcmContainerEntity> query;
        if (daysClosed == null)
        {
            query = em.createQuery(
                    String.format("SELECT obj FROM %s obj WHERE obj.status IN :statuses AND obj.container.calendarFolderId IS NOT NULL",
                            entityTypeForQuery),
                    AcmContainerEntity.class);
        } else
        {
            query = em.createQuery(String.format(
                    "SELECT obj FROM %s obj WHERE obj.status IN :statuses AND obj.container.calendarFolderId IS NOT NULL AND obj.modified <= :modified",
                    entityTypeForQuery), AcmContainerEntity.class);
            query.setParameter("modified", calculateModifiedDate(daysClosed));
        }
        query.setParameter("statuses", closedStates);
        List<AcmContainerEntity> resultList = query.getResultList();

        return resultList;
    }

    /**
     * @param daysClosed
     * @return
     */
    private Date calculateModifiedDate(Integer daysClosed)
    {
        LocalDate now = LocalDate.now().minusDays(daysClosed);
        return Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * @param purgeCandidates
     */
    private void purgeCalendars(ServiceConnector connector, List<AcmContainerEntity> purgeCandidates)
    {
        for (AcmContainerEntity entity : purgeCandidates)
        {
            AcmContainer container = entity.getContainer();
            auditPropertyEntityAdapter.setUserId(PROCESS_USER);
            try
            {
                // The start date and end date are chosen on the assumption that no events would be created prior to the
                // creation of the object, and that possible events that are 1 year after the event creation are part of
                // recurring events.
                Date startDate = container.getCreated();
                Date endDate = Date.from(LocalDate.now().plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
                CalendarView calendarView = new CalendarView(startDate, endDate);

                Optional<ExchangeService> potentialService = connector.connect(container.getContainerObjectId());
                if (!potentialService.isPresent())
                {
                    continue;
                }
                ExchangeService service = potentialService.get();
                CalendarFolder calendar = CalendarFolder.bind(service, new FolderId(container.getCalendarFolderId()));

                FindItemsResults<Appointment> findResults = calendar.findAppointments(calendarView);

                for (Appointment appointment : findResults.getItems())
                {
                    String calendarEventId = appointment.getId().getUniqueId();
                    if (appointment.getIsRecurring())
                    {
                        appointment = Appointment.bindToRecurringMaster(service, new ItemId(calendarEventId));
                        outlookDao.deleteAppointmentItem(service, appointment.getId().getUniqueId(), true, DeleteMode.MoveToDeletedItems);
                    } else
                    {
                        outlookDao.deleteAppointmentItem(service, calendarEventId, false, DeleteMode.MoveToDeletedItems);
                    }
                }
                calendar.delete(DeleteMode.MoveToDeletedItems);

                container.setCalendarFolderId(null);
                containerEntityDao.save(container);

            } catch (Exception e)
            {
                log.warn("Error while trying to purge calendar events for calendar folder with id: {}", container.getCalendarFolderId(), e);
            }
        }
    }

    /**
     * @param outlookDao
     *            the outlookDao to set
     */
    public void setOutlookDao(OutlookDao outlookDao)
    {
        this.outlookDao = outlookDao;
    }

    /**
     * @param containerEntityDao
     *            the containerEntityDao to set
     */
    public void setContainerEntityDao(AcmContainerDao containerEntityDao)
    {
        this.containerEntityDao = containerEntityDao;
    }

    /**
     * @param auditPropertyEntityAdapter
     *            the auditPropertyEntityAdapter to set
     */
    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    /**
     * @param closedStates
     *            the closedStates to set
     */
    public void setClosedStates(String closedStates)
    {
        this.closedStates = Arrays.asList(closedStates.split(","));
    }

    /**
     * @param entityType
     *            the entityType to set
     */
    public void setEntityType(String entityType)
    {
        this.entityType = entityType;
    }

    /**
     * @param entityTypeForQuery
     *            the entityTypeForQuery to set
     */
    public void setEntityTypeForQuery(String entityTypeForQuery)
    {
        this.entityTypeForQuery = entityTypeForQuery;
    }

    /**
     * @param entityIdForQuery
     *            the entityIdForQuery to set
     */
    public void setEntityIdForQuery(String entityIdForQuery)
    {
        this.entityIdForQuery = entityIdForQuery;
    }

}