/**
 *
 */
package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.calendar.config.service.CalendarConfiguration.PurgeOptions;
import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarEventInfo;
import com.armedia.acm.calendar.service.AcmCalendarInfo;
import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
public class CalendarEntityHandler implements ApplicationListener<AbstractConfigurationFileEvent>
{

    public static enum PermissionType
    {
        READ, WRITE, DELETE;
    }

    private static final Object CALENDAR_PURGERS_CONFIGUTATION_FILENAME = "calendarPurgersSettings.properties";

    @PersistenceContext
    private EntityManager em;

    private Logger log = LoggerFactory.getLogger(getClass());

    private OutlookDao outlookDao;

    protected Map<String, PropertyDefinition> sortFields;

    private List<String> closedStates;

    private String entityType;

    private String entityTypeForQuery;

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

    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent event)
    {

        if (isConfigurationFileChange(event))
        {
            File configFile = event.getConfigFile();
            Properties properties = new Properties();
            try
            {
                properties.load(new FileInputStream(configFile));
                processProperties(properties);
            } catch (IOException e)
            {
                log.error("Could not read properties from {} file.", configFile.getName());
            }
        }
    }

    /**
     * @param properties
     */
    private void processProperties(Properties properties)
    {
        String closedStateKey = String.format("%s.CLOSED_STATES", entityType);
        if (properties.containsKey(closedStateKey))
        {
            closedStates = Arrays.asList(properties.getProperty(closedStateKey).split(","));
        } else
        {
            closedStates = Arrays.asList("CLOSED");
        }
    }

    private boolean isConfigurationFileChange(AbstractConfigurationFileEvent abstractConfigurationFileEvent)
    {
        return (abstractConfigurationFileEvent instanceof ConfigurationFileAddedEvent
                || abstractConfigurationFileEvent instanceof ConfigurationFileChangedEvent)
                && abstractConfigurationFileEvent.getConfigFile().getName().equals(CALENDAR_PURGERS_CONFIGUTATION_FILENAME);
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

    public List<AcmCalendarInfo> listCalendars(ExchangeService service, AcmUser user, Authentication auth, String sort,
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
            query = getEm().createQuery(String
                    .format("SELECT ot FROM %s ot WHERE ot.complaintId = :objectId AND ot.restricted = :restricted", entityTypeForQuery),
                    AcmContainerEntity.class);
            query.setParameter("restricted", true);
        } else
        {
            query = getEm().createQuery(String.format("SELECT ot FROM %s ot WHERE ot.complaintId = :objectId", entityTypeForQuery),
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

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandler#purgeCalendar(com.armedia.acm.
     * calendar.config.service.CalendarConfiguration.PurgeOptions)
     */
    public void purgeCalendars(ExchangeService service, PurgeOptions purgeOptions, Integer daysClosed)
    {
        switch (purgeOptions)
        {
        case RETAIN_INDEFINITELY:
            return;
        case CLOSED:
        case CLOSED_X_DAYS:
            System.currentTimeMillis();
            List<AcmContainerEntity> purgeCandidates = getEntities(daysClosed);
            purgeCalendars(service, purgeCandidates);
            break;
        }

    }

    private List<AcmContainerEntity> getEntities(Integer daysClosed)
    {
        TypedQuery<AcmContainerEntity> query;
        if (daysClosed == null)
        {
            query = getEm().createQuery(
                    String.format("SELECT obj FROM %s obj WHERE obj.status IN :statuses AND obj.container.calendarFolderId IS NOT NULL",
                            entityTypeForQuery),
                    AcmContainerEntity.class);
        } else
        {
            query = getEm().createQuery(String.format(
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
    private void purgeCalendars(ExchangeService service, List<AcmContainerEntity> purgeCandidates)
    {
        for (AcmContainerEntity entity : purgeCandidates)
        {
            try
            {
                // The start date and end date are chosen on the assumption that no events would be created prior to the
                // creation of the object, and that possible events that are 1 year after the event creation are part of
                // recurring events.
                Date startDate = entity.getContainer().getCreated();
                Date endDate = Date.from(LocalDate.now().plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
                CalendarView calendarView = new CalendarView(startDate, endDate);
                CalendarFolder calendar = CalendarFolder.bind(service, new FolderId(entity.getContainer().getCalendarFolderId()));

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
                // update the container with removing the outlook folder id.
                // also, update the queries to include only containers that have outlook folder id set.
                entity.getContainer().setCalendarFolderId(null);
                getEm().merge(entity);

            } catch (Exception e)
            {
                log.warn("Error while trying to purge calendar events for calendar folder with id: {}",
                        entity.getContainer().getCalendarFolderId(), e);
            }
        }
    }

    /**
     * @return the em
     */
    protected EntityManager getEm()
    {
        return em;
    }

    /**
     * @param outlookDao the outlookDao to set
     */
    public void setOutlookDao(OutlookDao outlookDao)
    {
        this.outlookDao = outlookDao;
    }

    /**
     * @param entityType the entityType to set
     */
    public void setEntityType(String entityType)
    {
        this.entityType = entityType;
    }

    /**
     * @param entityTypeForQuery the entityTypeForQuery to set
     */
    public void setEntityTypeForQuery(String entityTypeForQuery)
    {
        this.entityTypeForQuery = entityTypeForQuery;
    }

}