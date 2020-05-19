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

import static com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandler.PermissionType.DELETE;
import static com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandler.PermissionType.READ;
import static com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandler.PermissionType.WRITE;

import com.armedia.acm.calendar.config.service.CalendarAdminService;
import com.armedia.acm.calendar.config.service.CalendarConfiguration;
import com.armedia.acm.calendar.config.service.CalendarConfigurationEvent;
import com.armedia.acm.calendar.config.service.CalendarConfigurationException;
import com.armedia.acm.calendar.config.service.CalendarConfigurationsByObjectType;
import com.armedia.acm.calendar.config.service.EmailCredentials;
import com.armedia.acm.calendar.config.service.EmailCredentialsVerifierService;
import com.armedia.acm.calendar.service.AcmCalendar;
import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarInfo;
import com.armedia.acm.calendar.service.Attendee.AttendeeType;
import com.armedia.acm.calendar.service.CalendarExceptionMapper;
import com.armedia.acm.calendar.service.CalendarService;
import com.armedia.acm.calendar.service.CalendarServiceConfigurationException;
import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandler.ServiceConnector;
import com.armedia.acm.core.exceptions.AcmOutlookConnectionFailedException;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDaoException;
import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.service.OutlookFolderRecreator;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.PersistenceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.property.complex.Attachment;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 12, 2017
 */
public class ExchangeCalendarService
        implements CalendarService, EmailCredentialsVerifierService, ApplicationListener<CalendarConfigurationEvent>, InitializingBean
{

    static final String PROCESS_USER = "CALENDAR_SERVICE_PURGER";
    private Logger log = LogManager.getLogger(getClass());
    private CalendarAdminService calendarAdminService;
    private OutlookDao outlookDao;
    private AcmOutlookFolderCreatorDao folderCreatorDao;
    private Map<String, CalendarConfiguration> configurationsByType;
    private OutlookFolderRecreator folderRecreator;
    private SpringContextHolder springContextHolder;
    private ExchangeEventPublisher exchangeEventPublisher;

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(CalendarConfigurationEvent event)
    {
        loadConfiguration();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        loadConfiguration();
    }

    private void loadConfiguration()
    {
        try
        {
            CalendarConfigurationsByObjectType calendarConfiguration = calendarAdminService.readConfiguration(true);
            configurationsByType = calendarConfiguration.getConfigurationsByType();
        }
        catch (CalendarConfigurationException e)
        {
            log.error("Could not load calendar configuration.", e);
        }
    }

    protected CalendarEntityHandler findEntityHandlerByObjectType(String objectType)
    {
        return getSpringContextHolder().getAllBeansOfType(CalendarEntityHandler.class)
            .values()
            .stream()
            .filter(eh -> objectType.equals(eh.getEntityType())).findFirst().orElse(null);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.calendar.service.CalendarService#retrieveCalendar(com.armedia.acm.services.users.model.AcmUser,
     * org.springframework.security.core.Authentication, java.lang.String, java.lang.String)
     */
    @Override
    public Optional<AcmCalendar> retrieveCalendar(AcmUser user, Authentication auth, String objectType, String objectId)
            throws CalendarServiceException
    {
        if (!configurationsByType.containsKey(objectType) || !configurationsByType.get(objectType).isIntegrationEnabled())
        {
            log.warn("Calendar integration is not enabled for [{}] object type.", objectType);
            return Optional.empty();
        }

        CalendarEntityHandler handler = Optional.ofNullable(findEntityHandlerByObjectType(objectType))
                .orElseThrow(() -> new CalendarServiceConfigurationException(
                        String.format("No CalendarEntityHandler registered for [%s] object type.", objectType)));

        AcmOutlookUser outlookUser = getOutlookUserForObject(auth, Long.valueOf(objectId), objectType, handler);

        if (!handler.checkPermission(auth, objectType, objectId, READ))
        {
            log.warn("User [{}] does not have READ permission to access object with [{}] id of [{}] type.", user.getFullName(), objectId,
                    objectType);
            throw new CalendarServiceAccessDeniedException(
                    String.format("User [%s] does not have READ permission to access object with [%s] id of [%s] type.", user.getFullName(),
                            objectId, objectType));
        }

        ExchangeService exchangeService = outlookDao.connect(outlookUser);
        return Optional.of(new ExchangeCalendar(exchangeService, handler, objectType, objectId));
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.calendar.service.CalendarService#listCalendars(com.armedia.acm.services.users.model.AcmUser,
     * org.springframework.security.core.Authentication, java.lang.String, java.lang.String, java.lang.String, int, int)
     */
    @Override
    public List<AcmCalendarInfo> listCalendars(AcmUser user, Authentication auth, String objectType, String sort, String sortDirection,
            int start, int maxItems) throws CalendarServiceException
    {
        List<AcmCalendarInfo> result = new ArrayList<>();
        if (objectType != null)
        {
            if (configurationsByType.containsKey(objectType) && configurationsByType.get(objectType).isIntegrationEnabled())
            {
                CalendarEntityHandler handler = Optional.ofNullable(findEntityHandlerByObjectType(objectType))
                        .orElseThrow(() -> new CalendarServiceConfigurationException(
                                String.format("No CalendarEntityHandler registered for [%s] object type.", objectType)));
                ServiceConnector connector = getConnector(auth.getName(), objectType, handler);
                result.addAll(handler.listCalendars(connector, user, auth, sort, sortDirection, start, maxItems));
            }
        }
        else
        {
            for (CalendarEntityHandler handler : getSpringContextHolder().getAllBeansOfType(CalendarEntityHandler.class).values())
            {
                if (!configurationsByType.containsKey(handler.getEntityType())
                        && !configurationsByType.get(handler.getEntityType()).isIntegrationEnabled())
                {
                    continue;
                }
                ServiceConnector connector = getConnector(auth.getName(), objectType, handler);
                result.addAll(handler.listCalendars(connector, user, auth, sort, sortDirection, start, maxItems));
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.calendar.service.CalendarService#addCalendarEvent(com.armedia.acm.services.users.model.AcmUser,
     * org.springframework.security.core.Authentication, java.lang.String,
     * com.armedia.acm.calendar.service.AcmCalendarEvent, org.springframework.web.multipart.MultipartFile[])
     */
    @Override
    public void addCalendarEvent(AcmUser user, Authentication auth, AcmCalendarEvent calendarEvent, MultipartFile[] attachments)
            throws CalendarServiceException
    {
        log.debug("Adding calendar event for object with id: [{}] of [{}] type.", calendarEvent.getObjectId(),
                calendarEvent.getObjectType());

        if (!configurationsByType.containsKey(calendarEvent.getObjectType())
                || !configurationsByType.get(calendarEvent.getObjectType()).isIntegrationEnabled())
        {
            log.warn("Calendar integration is not enabled for [{}] object type.", calendarEvent.getObjectType());
            throw new CalendarServiceConfigurationException(
                    String.format("Calendar integration is not enabled for [%s] object type.", calendarEvent.getObjectType()));
        }

        CalendarEntityHandler handler = Optional.ofNullable(findEntityHandlerByObjectType(calendarEvent.getObjectType()))
                .orElseThrow(() -> new CalendarServiceConfigurationException(
                        String.format("No CalendarEntityHandler registered for [%s] object type.", calendarEvent.getObjectType())));

        AcmOutlookUser outlookUser = getOutlookUserForObject(auth, Long.valueOf(calendarEvent.getObjectId()), calendarEvent.getObjectType(),
                handler);

        if (!handler.checkPermission(auth, calendarEvent.getObjectType(), calendarEvent.getObjectId(), WRITE))
        {
            log.warn("User [{}] does not have WRITE permission to access object with [{}] id of [{}] type.", user.getFullName(),
                    calendarEvent.getObjectId(), calendarEvent.getObjectType());
            throw new CalendarServiceAccessDeniedException(
                    String.format("User [%s] does not have WRITE permission to access object with [%s] id of [%s] type.",
                            user.getFullName(), calendarEvent.getObjectId(), calendarEvent.getObjectType()));
        }

        Appointment appointment;
        FolderId folderId;
        try (ExchangeService exchangeService = outlookDao.connect(outlookUser))
        {
            appointment = new Appointment(exchangeService);
            ExchangeTypesConverter.setAppointmentProperties(appointment, calendarEvent, attachments, true);
            String calendarId = handler.getCalendarId(calendarEvent.getObjectId())
                    .orElseThrow(() -> new Exception(String.format("No outlook folder associated with object of type [%s] with id [%s].",
                            calendarEvent.getObjectType(), calendarEvent.getObjectId())));
            folderId = new FolderId(calendarId);

            // issue with ews-java-api library. Workaround solution for creating calendar event with attachment.
            // adding attachment successfully requires an update, by changing the subject, it will force an update
            appointment.save(folderId);

            appointment.setSubject(calendarEvent.getSubject());
            appointment.update(ConflictResolutionMode.AlwaysOverwrite);
            exchangeEventPublisher.publishCalendarEventAdded(appointment, auth.getName(), Long.valueOf(calendarEvent.getObjectId()),
                    calendarEvent.getObjectType());
        }
        catch (Exception e)
        {
            log.warn("Error while trying to create event for object with id: [{}] of [{}] type.", calendarEvent.getObjectId(),
                    calendarEvent.getObjectType(), e);
            throw new CalendarServiceException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.calendar.service.CalendarService#updateCalendarEvent(com.armedia.acm.services.users.model.
     * AcmUser, org.springframework.security.core.Authentication, com.armedia.acm.calendar.service.AcmCalendarEvent,
     * org.springframework.web.multipart.MultipartFile[])
     */
    @Override
    public void updateCalendarEvent(AcmUser user, Authentication auth, boolean updateMaster, AcmCalendarEvent calendarEvent,
            MultipartFile[] attachments) throws CalendarServiceException
    {

        log.debug("Updating calendar event for object with id: [{}] of [{}] type.", calendarEvent.getObjectId(),
                calendarEvent.getObjectType());

        if (!configurationsByType.containsKey(calendarEvent.getObjectType())
                || !configurationsByType.get(calendarEvent.getObjectType()).isIntegrationEnabled())
        {
            log.warn("Calendar integration is not enabled for [{}] object type.", calendarEvent.getObjectType());
            throw new CalendarServiceConfigurationException(
                    String.format("Calendar integration is not enabled for [%s] object type.", calendarEvent.getObjectType()));
        }

        try
        {
            CalendarEntityHandler handler = Optional.ofNullable(findEntityHandlerByObjectType(calendarEvent.getObjectType()))
                    .orElseThrow(() -> new CalendarServiceConfigurationException(
                            String.format("No CalendarEntityHandler registered for [%s] object type.", calendarEvent.getObjectType())));

            AcmOutlookUser outlookUser = getOutlookUserForObject(auth, Long.valueOf(calendarEvent.getObjectId()),
                    calendarEvent.getObjectType(), handler);

            if (!handler.checkPermission(auth, calendarEvent.getObjectType(), calendarEvent.getObjectId(), WRITE))
            {
                log.warn("User [{}] does not have WRITE permission to access object with [{}] id of [{}] type.", user.getFullName(),
                        calendarEvent.getObjectId(), calendarEvent.getObjectType());
                throw new CalendarServiceAccessDeniedException(
                        String.format("User [%s] does not have WRITE permission to access object with [%s] id of [%s] type.",
                                user.getFullName(), calendarEvent.getObjectId(), calendarEvent.getObjectType()));
            }

            try ( ExchangeService exchangeService = outlookDao.connect(outlookUser) )
            {
                Appointment appointment = bindToAppointment(calendarEvent.getEventId(), exchangeService);
                boolean updateRecurrence = false;
                if (updateMaster && appointment.getIsRecurring())
                {
                    appointment = Appointment.bindToRecurringMaster(exchangeService, new ItemId(calendarEvent.getEventId()));
                    updateRecurrence = true;
                }

                PropertySet allProperties = new PropertySet();
                allProperties.addRange(PropertyDefinitionHolder.standardProperties);
                appointment.load(allProperties);
                ExchangeTypesConverter.setAppointmentProperties(appointment, calendarEvent, attachments, updateRecurrence);

                processAttachmentsForRemoval(calendarEvent, appointment);
                processAttendeesForRemoval(calendarEvent, appointment);

                // workaround solution for updating calendar event with attachment
                appointment.update(ConflictResolutionMode.AlwaysOverwrite);
                appointment.setSubject(calendarEvent.getSubject());
                appointment.update(ConflictResolutionMode.AlwaysOverwrite);
            }

        }
        catch (CalendarServiceBindToRemoteException e)
        {
            // Just re-throw here. The extra catch block is needed to prevent it being wrapped in the more general type.
            throw e;
        }
        catch (Exception e)
        {
            log.debug("Error while trying to update object with id: [{}] of [{}] type.", calendarEvent.getObjectId(),
                    calendarEvent.getObjectType(), e);
            throw new CalendarServiceException(e);
        }

    }

    /**
     * @param calendarEvent
     * @param exchangeService
     * @return
     * @throws Exception
     */
    private Appointment bindToAppointment(String calendarEventId, ExchangeService exchangeService)
            throws CalendarServiceBindToRemoteException
    {
        try
        {
            return Appointment.bind(exchangeService, new ItemId(calendarEventId));
        }
        catch (Exception e)
        {
            log.warn("Error binding to remote service for event with id [{}].", calendarEventId, e);
            throw new CalendarServiceBindToRemoteException(e);
        }
    }

    /**
     * @param calendarEvent
     * @param appointment
     * @throws ServiceLocalException
     * @throws Exception
     */
    private void processAttachmentsForRemoval(AcmCalendarEvent calendarEvent, Appointment appointment)
            throws ServiceLocalException, Exception
    {
        List<String> attachmentsIds = calendarEvent.getFiles().stream().map(att -> att.getAttachmentId()).collect(Collectors.toList());
        if (!attachmentsIds.isEmpty())
        {
            List<Attachment> attachmentsToRemove = appointment.getAttachments().getItems().stream()
                    .filter(attachment -> attachmentsIds.contains(attachment.getId())).collect(Collectors.toList());
            for (Attachment attachment : attachmentsToRemove)
            {
                appointment.getAttachments().remove(attachment);
            }
        }
    }

    /**
     * @param calendarEvent
     * @param appointment
     * @throws ServiceLocalException
     * @throws Exception
     */
    private void processAttendeesForRemoval(AcmCalendarEvent calendarEvent, Appointment appointment) throws ServiceLocalException, Exception
    {
        List<String> required = calendarEvent.getAttendees().stream().filter(att -> AttendeeType.REQUIRED.equals(att.getType()))
                .map(att -> att.getEmail()).collect(Collectors.toList());
        List<microsoft.exchange.webservices.data.property.complex.Attendee> requiredForRemoval = appointment.getRequiredAttendees()
                .getItems().stream().filter(att -> !required.contains(att.getAddress())).collect(Collectors.toList());
        for (microsoft.exchange.webservices.data.property.complex.Attendee attendee : requiredForRemoval)
        {
            appointment.getRequiredAttendees().remove(attendee);
        }

        List<String> optional = calendarEvent.getAttendees().stream().filter(att -> AttendeeType.OPTIONAL.equals(att.getType()))
                .map(att -> att.getEmail()).collect(Collectors.toList());
        List<microsoft.exchange.webservices.data.property.complex.Attendee> optionalForRemoval = appointment.getOptionalAttendees()
                .getItems().stream().filter(att -> !optional.contains(att.getAddress())).collect(Collectors.toList());
        for (microsoft.exchange.webservices.data.property.complex.Attendee attendee : optionalForRemoval)
        {
            appointment.getOptionalAttendees().remove(attendee);
        }

        List<String> resource = calendarEvent.getAttendees().stream().filter(att -> AttendeeType.RESOURCE.equals(att.getType()))
                .map(att -> att.getEmail()).collect(Collectors.toList());
        List<microsoft.exchange.webservices.data.property.complex.Attendee> resourcedForRemoval = appointment.getResources().getItems()
                .stream().filter(att -> !resource.contains(att.getAddress())).collect(Collectors.toList());
        for (microsoft.exchange.webservices.data.property.complex.Attendee attendee : resourcedForRemoval)
        {
            appointment.getResources().remove(attendee);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.calendar.service.CalendarService#deleteCalendarEvent(com.armedia.acm.services.users.model.
     * AcmUser, org.springframework.security.core.Authentication, java.lang.String)
     */
    @Override
    public void deleteCalendarEvent(AcmUser user, Authentication auth, String objectType, String objectId, String calendarEventId,
            boolean deleteRecurring) throws CalendarServiceException
    {
        if (!configurationsByType.containsKey(objectType) || !configurationsByType.get(objectType).isIntegrationEnabled())
        {
            log.warn("Calendar integration is not enabled for [{}] object type.", objectType);
            throw new CalendarServiceConfigurationException(
                    String.format("Calendar integration is not enabled for [%s] object type.", objectType));
        }

        try
        {
            CalendarEntityHandler handler = Optional.ofNullable(findEntityHandlerByObjectType(objectType))
                    .orElseThrow(() -> new CalendarServiceConfigurationException(
                            String.format("No CalendarEntityHandler registered for [%s] object type.", objectType)));

            AcmOutlookUser outlookUser = getOutlookUserForObject(auth, Long.valueOf(objectId), objectType, handler);


            if (!handler.checkPermission(auth, objectType, objectId, DELETE))
            {
                log.warn("User [{}] does not have DELETE permission to access object with [{}] id of [{}] type.", user.getFullName(),
                        objectId, objectType);
                throw new CalendarServiceAccessDeniedException(
                        String.format("User [%s] does not have DELETE permission to access object with [%s] id of [%s] type.",
                                user.getFullName(), objectId, objectType));
            }
            try (ExchangeService exchangeService = outlookDao.connect(outlookUser))
            {
                Appointment appointment = bindToAppointment(calendarEventId, exchangeService);
                if (deleteRecurring && appointment.getIsRecurring())
                {
                    appointment = Appointment.bindToRecurringMaster(exchangeService, new ItemId(calendarEventId));
                    outlookDao.deleteAppointmentItem(exchangeService, appointment.getId().getUniqueId(), true, DeleteMode.MoveToDeletedItems);
                }
                else
                {
                    outlookDao.deleteAppointmentItem(exchangeService, calendarEventId, false, DeleteMode.MoveToDeletedItems);
                }
            }
        }
        catch (CalendarServiceBindToRemoteException e)
        {
            // Just re-throw here. The extra catch block is needed to prevent it being wrapped in the more general type.
            throw e;
        }
        catch (Exception e)
        {
            log.warn("Error while trying to delete object with id: [{}] of [{}] type.", objectId, objectType, e);
            throw new CalendarServiceException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.calendar.service.CalendarService#purgeEvents(java.lang.String,
     * com.armedia.acm.calendar.config.service.CalendarConfiguration)
     */
    @Override
    public void purgeEvents(String objectType, CalendarConfiguration config) throws CalendarServiceException
    {
        if (!config.isIntegrationEnabled())
        {
            return;
        }

        CalendarEntityHandler handler = Optional.ofNullable(findEntityHandlerByObjectType(objectType))
                .orElseThrow(() -> new CalendarServiceConfigurationException(
                        String.format("No CalendarEntityHandler registered for [%s] object type.", objectType)));

        ServiceConnector connector = getConnector(PROCESS_USER, objectType, handler);
        handler.purgeCalendars(connector, config.getPurgeOptions(), config.getDaysClosed());
    }

    private ServiceConnector getConnector(String userId, String objectType, CalendarEntityHandler handler)
    {
        return (objectId) -> {
            ExchangeService service = null;
            try
            {
                AcmOutlookUser outlookUser = getOutlookUserForObject(userId, objectId, objectType, handler);
                service = outlookDao.connect(outlookUser);
            }
            catch (AcmOutlookConnectionFailedException | CalendarServiceException e)
            {
                log.warn("Could not retrieve 'AcmOutlookUser' for object with [{}} id of type [{}]", objectId, objectType);
            }
            return Optional.ofNullable(service);
        };
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.calendar.service.CalendarService#getExceptionMapper(com.armedia.acm.calendar.service.
     * CalendarServiceException)
     */
    @Override
    public <CSE extends CalendarServiceException> CalendarExceptionMapper<CSE> getExceptionMapper(CalendarServiceException e)
    {
        return new ExchangeCalendarExcpetionMapper<>();
    }

    /**
     * @param auth
     * @param objectId
     *            id of the object that outlook user is retrieved
     * @param objectType
     * @param handler
     *            TODO
     * @return
     * @throws CalendarServiceException
     */
    private AcmOutlookUser getOutlookUserForObject(Authentication auth, Long objectId, String objectType, CalendarEntityHandler handler)
            throws CalendarServiceException
    {
        return getOutlookUserForObject(auth.getName(), objectId, objectType, handler);
    }

    /**
     * @param userId
     * @param objectId
     *            id of the object that outlook user is retrieved
     * @param objectType
     * @param handler
     *            TODO
     * @return
     * @throws CalendarServiceException
     */
    private AcmOutlookUser getOutlookUserForObject(String userId, Long objectId, String objectType, CalendarEntityHandler handler)
            throws CalendarServiceException
    {
        try
        {
            AcmOutlookFolderCreator folderCreator = folderCreatorDao.getFolderCreatorForObject(objectId, objectType);
            return new AcmOutlookUser(userId, folderCreator.getSystemEmailAddress(), folderCreator.getSystemPassword());
        }
        catch (AcmOutlookFolderCreatorDaoException e)
        {
            return handleFolderCreatorException(userId, objectId, objectType, handler, e);
        }
    }

    private AcmOutlookUser handleFolderCreatorException(String userId, Long objectId, String objectType, CalendarEntityHandler handler,
            AcmOutlookFolderCreatorDaoException e) throws CalendarServiceException
    {
        if (e.getCause() instanceof PersistenceException)
        {
            return handleMissingFolderCreator(userId, objectId, objectType, handler, e);
        }
        else
        {
            throw new CalendarServiceException(e);
        }
    }

    private AcmOutlookUser handleMissingFolderCreator(String userId, Long objectId, String objectType, CalendarEntityHandler handler,
            AcmOutlookFolderCreatorDaoException fce) throws CalendarServiceException
    {

        CalendarConfiguration configuration = configurationsByType.get(objectType);
        AcmOutlookUser user;
        if (configuration != null && configuration.isIntegrationEnabled())
        {
            user = new AcmOutlookUser(userId, configuration.getSystemEmail(), configuration.getPassword());
        }
        else
        {
            throw new CalendarServiceException(fce);
        }

        try (ExchangeService service = outlookDao.connect(user))
        {
            String calendarId = handler.getCalendarId(objectId.toString()).orElseThrow(() -> new Exception(
                    String.format("No outlook folder associated with object of type [%s] with id [%s].", objectType, objectId)));

            CalendarFolder.bind(service, new FolderId(calendarId));

            AcmOutlookFolderCreator folderCreator = folderCreatorDao.getFolderCreator(configuration.getSystemEmail(),
                    configuration.getPassword());
            folderCreatorDao.recordFolderCreator(folderCreator, objectId, objectType);

            return user;

        }
        catch (Exception e)
        {
            return tryToRecreateFolder(objectId, objectType, user, e);
        }
    }

    private AcmOutlookUser tryToRecreateFolder(Long objectId, String objectType, AcmOutlookUser outlookUser, Exception be)
            throws CalendarServiceException
    {
        try
        {
            CalendarEntityHandler handler = findEntityHandlerByObjectType(objectType);
            if ( handler != null )
            {
                if (handler.isObjectClosed(objectId))
                {
                    throw new CalendarObjectClosedException(String.format("Object of [%s] type and [%s] id is closed.", objectType, objectId));
                }
                folderRecreator.recreateFolder(objectType, objectId, outlookUser);
            }
            return outlookUser;
        }
        catch (CalendarServiceException cse)
        {
            CalendarServiceBindToRemoteException recreateException = new CalendarServiceBindToRemoteException(cse);
            recreateException.addSuppressed(be);
            throw recreateException;
        }

    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.calendar.config.service.CalendarAdminService#verifyEmailCredentials(com.armedia.acm.calendar.
     * config.service.EmailCredentials)
     */
    @Override
    public boolean verifyEmailCredentials(String userId, EmailCredentials emailCredentials)
    {
        AcmOutlookUser user = new AcmOutlookUser(userId, emailCredentials.getEmail(), emailCredentials.getPassword());
        log.debug("Verifying connection to Exchange as [{}]", user.getEmailAddress());
        try (ExchangeService service = outlookDao.connect(user))
        {
            log.debug("Binding to inbox as [{}]", user.getEmailAddress());
            Folder.bind(service, WellKnownFolderName.Inbox);
            return true;

        }
        catch (Exception e)
        {
            log.error("Could not verify connection to Exchange as [{}]", user.getEmailAddress(), e);
            return false;
        }
    }

    /**
     * @param calendarAdminService
     *            the calendarAdminService to set
     */
    public void setCalendarAdminService(CalendarAdminService calendarAdminService)
    {
        this.calendarAdminService = calendarAdminService;
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
     * @param folderCreatorDao
     *            the folderCreatorDao to set
     */
    public void setFolderCreatorDao(AcmOutlookFolderCreatorDao folderCreatorDao)
    {
        this.folderCreatorDao = folderCreatorDao;
    }

    public void setFolderRecreator(OutlookFolderRecreator folderRecreator)
    {
        this.folderRecreator = folderRecreator;
    }

    /**
     * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 12, 2017
     */
    public class ExchangeCalendarExcpetionMapper<CSE extends CalendarServiceException> implements CalendarExceptionMapper<CSE>
    {

        private CSE exception;

        /*
         * (non-Javadoc)
         * @see com.armedia.acm.calendar.service.CalendarExceptionMapper#mapException(com.armedia.acm.calendar.service.
         * CalendarServiceException)
         */
        @Override
        public Object mapException(CSE ce)
        {
            exception = ce;
            Map<String, Object> errorDetails = new HashMap<>();
            if (exception instanceof CalendarServiceAccessDeniedException)
            {
                errorDetails.put("error_cause", "ACCESS_DENIED");
            }
            else if (exception instanceof CalendarServiceConfigurationException)
            {
                errorDetails.put("error_cause", "SERVICE_CONFIGURATION");
            }
            else if (exception instanceof CalendarServiceBindToRemoteException)
            {
                errorDetails.put("error_cause", "INVALID_BIND_TO_SERVICE_CREDENTIALS");
            }
            else if (exception instanceof CalendarObjectClosedException)
            {
                errorDetails.put("error_cause", "OBJECT_CLOSED");
            }
            else
            {
                errorDetails.put("error_cause", "INTERNAL_SERVER_ERROR");
            }
            errorDetails.put("error_message", ce.getMessage());
            return errorDetails;
        }

        /*
         * (non-Javadoc)
         * @see com.armedia.acm.calendar.service.CalendarExceptionMapper#getStatusCode()
         */
        @Override
        public HttpStatus getStatusCode()
        {
            if (exception instanceof CalendarServiceAccessDeniedException || exception instanceof CalendarServiceBindToRemoteException)
            {
                return HttpStatus.FORBIDDEN;
            }
            else
            {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public void setExchangeEventPublisher(ExchangeEventPublisher exchangeEventPublisher)
    {
        this.exchangeEventPublisher = exchangeEventPublisher;
    }
}
