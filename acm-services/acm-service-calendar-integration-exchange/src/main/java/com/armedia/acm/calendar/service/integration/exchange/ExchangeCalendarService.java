package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.calendar.config.service.CalendarAdminService;
import com.armedia.acm.calendar.config.service.CalendarConfiguration;
import com.armedia.acm.calendar.config.service.CalendarConfigurationException;
import com.armedia.acm.calendar.config.service.CalendarConfigurationsByObjectType;
import com.armedia.acm.calendar.service.AcmCalendar;
import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarInfo;
import com.armedia.acm.calendar.service.Attendee.AttendeeType;
import com.armedia.acm.calendar.service.CalendarExceptionMapper;
import com.armedia.acm.calendar.service.CalendarService;
import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandler.PermissionType;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookDTO;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.property.complex.Attachment;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 12, 2017
 *
 */
public class ExchangeCalendarService implements CalendarService, ApplicationListener<AbstractConfigurationFileEvent>
{

    /**
     * The scheduler task name.
     */
    private static final String CONFIGUTATION_FILENAME = "calendarService.properties";

    /**
     * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 12, 2017
     *
     */
    public class ExchangeCalendarExcpetionMapper<CSE extends CalendarServiceException> implements CalendarExceptionMapper<CSE>
    {

        /*
         * (non-Javadoc)
         *
         * @see com.armedia.acm.calendar.service.CalendarExceptionMapper#mapException(com.armedia.acm.calendar.service.
         * CalendarServiceException)
         */
        @Override
        public Object mapException(CSE ce)
        {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see com.armedia.acm.calendar.service.CalendarExceptionMapper#getStatusCode()
         */
        @Override
        public HttpStatus getStatusCode()
        {
            // TODO Auto-generated method stub
            return null;
        }

    }

    private CalendarAdminService calendarAdminService;

    private Map<String, CalendarEntityHandler> entityHandlers;

    private OutlookService outlookService;

    private OutlookDao outlookDao;

    private Map<String, CalendarConfiguration> configurationsByType;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent event)
    {
        // TODO: TECHNICAL DEBT, should not depend on an implementation of configuration service that writes to file
        // system, as there might be different implementations of configuration service. Maybe it would be better if the
        // configuration service emits an event that would trigger action?
        if (isConfigurationFileChange(event))
        {
            try
            {
                CalendarConfigurationsByObjectType calendarConfiguration = calendarAdminService.readConfiguration(true);
                configurationsByType = calendarConfiguration.getConfigurationsByType();
            } catch (CalendarConfigurationException e)
            {
                // TODO add logging
                // log.error("Could not load scheduler configuration from file {}, error was: {}.",
                // configFile.getAbsoluteFile(), e);
            }
        }

    }

    /**
     * Checks if the event was triggered by a change of the scheduler configuration file.
     *
     * @param abstractConfigurationFileEvent
     *            the event encapsulating a reference to the modified file.
     * @return <code>true</code> if the event was triggered by the calendar configuration <code>false</code> otherwise.
     */
    private boolean isConfigurationFileChange(AbstractConfigurationFileEvent abstractConfigurationFileEvent)
    {
        return (abstractConfigurationFileEvent instanceof ConfigurationFileAddedEvent
                || abstractConfigurationFileEvent instanceof ConfigurationFileChangedEvent)
                && abstractConfigurationFileEvent.getConfigFile().getName().equals(CONFIGUTATION_FILENAME);
    }

    /*
     * (non-Javadoc)
     *
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
            return Optional.ofNullable(null);
        }
        CalendarEntityHandler handler = Optional.ofNullable(entityHandlers.get(objectType))
                .orElseThrow(() -> new CalendarServiceException(""));
        boolean restricted = handler.isRestricted(objectId);
        AcmOutlookUser outlookUser = restricted ? getRestrictedOutlookUser(auth, objectType) : getPublicOutlookUser(user, auth);
        ExchangeService exchangeService = outlookDao.connect(outlookUser);
        if (restricted && !handler.checkPermission(exchangeService, user, auth, objectId, PermissionType.READ))
        {
            // TODO: add logging and proper exception message.
            throw new CalendarServiceException("");
        }
        return Optional.of(new ExchangeCalendar(exchangeService, handler, objectType, objectId, restricted));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.service.CalendarService#listCalendars(com.armedia.acm.services.users.model.AcmUser,
     * org.springframework.security.core.Authentication, java.lang.String, java.lang.String, java.lang.String, int, int)
     */
    @Override
    public List<AcmCalendarInfo> listCalendars(AcmUser user, Authentication auth, String objectType, String sort, String sortDirection,
            int start, int maxItems) throws CalendarServiceException
    {
        List<AcmCalendarInfo> result = new ArrayList<>();
        AcmOutlookUser outlookUser = getPublicOutlookUser(user, auth);
        ExchangeService exchangeService = outlookDao.connect(outlookUser);
        if (objectType != null)
        {
            if (configurationsByType.containsKey(objectType) && configurationsByType.get(objectType).isIntegrationEnabled())
            {
                CalendarEntityHandler handler = Optional.ofNullable(entityHandlers.get(objectType))
                        .orElseThrow(() -> new CalendarServiceException(""));
                result.addAll(handler.listCalendars(exchangeService, user, auth, sort, sortDirection, start, maxItems));
            }
        } else
        {
            for (Entry<String, CalendarEntityHandler> handlerEntry : entityHandlers.entrySet())
            {
                if (!configurationsByType.containsKey(handlerEntry.getKey())
                        && !configurationsByType.get(handlerEntry.getKey()).isIntegrationEnabled())
                {
                    continue;
                }
                result.addAll(handlerEntry.getValue().listCalendars(exchangeService, user, auth, sort, sortDirection, start, maxItems));
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.calendar.service.CalendarService#addCalendarEvent(com.armedia.acm.services.users.model.AcmUser,
     * org.springframework.security.core.Authentication, java.lang.String,
     * com.armedia.acm.calendar.service.AcmCalendarEvent, org.springframework.web.multipart.MultipartFile[])
     */
    @Override
    public void addCalendarEvent(AcmUser user, Authentication auth, String calendarId, AcmCalendarEvent calendarEvent,
            MultipartFile[] attachments) throws CalendarServiceException
    {
        if (calendarId == null && calendarEvent.getCalendarId() == null
                || calendarId != null && calendarEvent.getCalendarId() != null && !calendarId.equals(calendarEvent.getCalendarId()))
        {
            // TODO: add logging and proper exception message here.
            throw new CalendarServiceException("");
        }
        if (!configurationsByType.containsKey(calendarEvent.getObjectType())
                || !configurationsByType.get(calendarEvent.getObjectType()).isIntegrationEnabled())
        {
            // TODO: add logging and proper exception message here.
            throw new CalendarServiceException("");
        }
        calendarId = calendarId != null ? calendarId : calendarEvent.getCalendarId();
        CalendarEntityHandler handler = Optional.ofNullable(entityHandlers.get(calendarEvent.getObjectType()))
                .orElseThrow(() -> new CalendarServiceException(""));
        boolean restricted = handler.isRestricted(calendarEvent.getObjectId());
        AcmOutlookUser outlookUser = restricted ? getRestrictedOutlookUser(auth, calendarEvent.getObjectType())
                : getPublicOutlookUser(user, auth);
        ExchangeService exchangeService = outlookDao.connect(outlookUser);
        if (restricted && !handler.checkPermission(exchangeService, user, auth, calendarEvent.getObjectId(), PermissionType.WRITE))
        {
            // TODO: add logging and proper exception message.
            throw new CalendarServiceException("");
        }
        try
        {
            Appointment appointment = new Appointment(exchangeService);
            ExchangeTypesConverter.setAppointmentProperties(appointment, calendarEvent, attachments, true);
            appointment.save(new FolderId(calendarId));
        } catch (Exception e)
        {
            throw new CalendarServiceException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.service.CalendarService#updateCalendarEvent(com.armedia.acm.services.users.model.
     * AcmUser, org.springframework.security.core.Authentication, com.armedia.acm.calendar.service.AcmCalendarEvent,
     * org.springframework.web.multipart.MultipartFile[])
     */
    @Override
    public void updateCalendarEvent(AcmUser user, Authentication auth, boolean updateMaster, AcmCalendarEvent calendarEvent,
            MultipartFile[] attachments) throws CalendarServiceException
    {
        if (!configurationsByType.containsKey(calendarEvent.getObjectType())
                || !configurationsByType.get(calendarEvent.getObjectType()).isIntegrationEnabled())
        {
            // TODO: add logging and proper exception message here.
            throw new CalendarServiceException(
                    String.format("Calendar integration is not enablef for %s object type.", calendarEvent.getObjectType()));
        }

        try
        {
            CalendarEntityHandler handler = Optional.ofNullable(entityHandlers.get(calendarEvent.getObjectType()))
                    .orElseThrow(() -> new CalendarServiceException(""));

            boolean restricted = handler.isRestricted(calendarEvent.getObjectId());
            AcmOutlookUser outlookUser = restricted ? getRestrictedOutlookUser(auth, calendarEvent.getObjectType())
                    : getPublicOutlookUser(user, auth);
            ExchangeService exchangeService = outlookDao.connect(outlookUser);

            if (restricted && !handler.checkPermission(exchangeService, user, auth, calendarEvent.getObjectId(), PermissionType.WRITE))
            {
                // TODO: add logging and proper exception message.
                throw new CalendarServiceException("");
            }
            Appointment appointment = Appointment.bind(exchangeService, new ItemId(calendarEvent.getEventId()));
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

            appointment.update(ConflictResolutionMode.AlwaysOverwrite);

        } catch (Exception e)
        {
            throw new CalendarServiceException(e);
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
        List<String> attachmentsFileNames = calendarEvent.getFileNames();
        if (attachmentsFileNames != null && !attachmentsFileNames.isEmpty())
        {
            List<Attachment> attachmentsToRemove = appointment.getAttachments().getItems().stream()
                    .filter(attachment -> attachmentsFileNames.contains(attachment.getName())).collect(Collectors.toList());
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
     *
     * @see com.armedia.acm.calendar.service.CalendarService#deleteCalendarEvent(com.armedia.acm.services.users.model.
     * AcmUser, org.springframework.security.core.Authentication, java.lang.String)
     */
    @Override
    public void deleteCalendarEvent(AcmUser user, Authentication auth, String objectType, String objectId, String calendarEventId,
            boolean deleteRecurring) throws CalendarServiceException
    {
        if (!configurationsByType.containsKey(objectType) || !configurationsByType.get(objectType).isIntegrationEnabled())
        {
            // TODO: add logging and proper exception message here.
            throw new CalendarServiceException(String.format("Calendar integration is not enablef for %s object type.", objectType));
        }

        try
        {
            CalendarEntityHandler handler = Optional.ofNullable(entityHandlers.get(objectType))
                    .orElseThrow(() -> new CalendarServiceException(""));

            boolean restricted = handler.isRestricted(objectId);
            AcmOutlookUser outlookUser = restricted ? getRestrictedOutlookUser(auth, objectType) : getPublicOutlookUser(user, auth);
            ExchangeService exchangeService = outlookDao.connect(outlookUser);

            if (restricted && !handler.checkPermission(exchangeService, user, auth, objectId, PermissionType.DELETE))
            {
                // TODO: add logging and proper exception message.
                throw new CalendarServiceException("");
            }
            Appointment appointment = Appointment.bind(exchangeService, new ItemId(calendarEventId));
            if (deleteRecurring && appointment.getIsRecurring())
            {
                appointment = Appointment.bindToRecurringMaster(exchangeService, new ItemId(calendarEventId));
                outlookDao.deleteAppointmentItem(exchangeService, appointment.getId().getUniqueId(), true, DeleteMode.MoveToDeletedItems);
            } else
            {
                outlookDao.deleteAppointmentItem(exchangeService, calendarEventId, false, DeleteMode.MoveToDeletedItems);
            }
        } catch (Exception e)
        {
            throw new CalendarServiceException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
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
     * @param objectType
     * @return
     */
    private AcmOutlookUser getRestrictedOutlookUser(Authentication auth, String objectType)
    {
        String userId = auth.getName();
        CalendarConfiguration configuration = configurationsByType.get(objectType);
        return new AcmOutlookUser(userId, configuration.getSystemEmail(), configuration.getPassword());
    }

    /**
     * @param user
     * @param handler
     * @param calendarEvent
     * @return
     * @throws CalendarServiceException
     */
    private AcmOutlookUser getPublicOutlookUser(AcmUser user, Authentication auth) throws CalendarServiceException
    {
        try
        {
            String userId = auth.getName();
            OutlookDTO outlookDTO = outlookService.retrieveOutlookPassword(auth);
            return new AcmOutlookUser(userId, user.getMail(), outlookDTO.getOutlookPassword());
        } catch (AcmEncryptionException e)
        {
            // TODO: Add logging here.
            throw new CalendarServiceException(e);
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
     * @param entityHandlers
     *            the entityHandlers to set
     */
    public void setEntityHandlers(Map<String, CalendarEntityHandler> entityHandlers)
    {
        this.entityHandlers = entityHandlers;
    }

    /**
     * @param outlookService
     *            the outlookService to set
     */
    public void setOutlookService(OutlookService outlookService)
    {
        this.outlookService = outlookService;
    }

    /**
     * @param outlookDao
     *            the outlookDao to set
     */
    public void setOutlookDao(OutlookDao outlookDao)
    {
        this.outlookDao = outlookDao;
    }

}
