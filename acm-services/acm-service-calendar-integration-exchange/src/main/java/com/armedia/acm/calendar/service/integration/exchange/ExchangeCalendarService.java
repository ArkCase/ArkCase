package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.calendar.service.AcmCalendar;
import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarInfo;
import com.armedia.acm.calendar.service.CalendarExceptionMapper;
import com.armedia.acm.calendar.service.CalendarService;
import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandler.PermissionType;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookDTO;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.property.complex.Attachment;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 12, 2017
 *
 */
public class ExchangeCalendarService implements CalendarService
{

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

    private Map<String, CalendarEntityHandler> entityHandlers;

    private OutlookService outlookService;

    private OutlookDao outlookDao;

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
        CalendarEntityHandler handler = Optional.ofNullable(entityHandlers.get(objectType))
                .orElseThrow(() -> new CalendarServiceException(""));
        AcmOutlookUser outlookUser = getOutlookUser(user, auth);
        ExchangeService exchangeService = outlookDao.connect(outlookUser);
        if (!handler.checkPermission(exchangeService, user, auth, objectId, PermissionType.READ))
        {
            // TODO: add logging and proper exception message.
            throw new CalendarServiceException("");
        }
        return Optional.of(new ExchangeCalendar(exchangeService, handler, objectType, objectId));
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
        AcmOutlookUser outlookUser = getOutlookUser(user, auth);
        ExchangeService exchangeService = outlookDao.connect(outlookUser);
        if (objectType != null)
        {
            CalendarEntityHandler handler = Optional.ofNullable(entityHandlers.get(objectType))
                    .orElseThrow(() -> new CalendarServiceException(""));
            result.addAll(handler.listCalendars(exchangeService, user, auth, sort, sortDirection, start, maxItems));
        } else
        {
            for (CalendarEntityHandler handler : entityHandlers.values())
            {
                result.addAll(handler.listCalendars(exchangeService, user, auth, sort, sortDirection, start, maxItems));
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
        calendarId = calendarId != null ? calendarId : calendarEvent.getCalendarId();
        CalendarEntityHandler handler = Optional.ofNullable(entityHandlers.get(calendarEvent.getObjectType()))
                .orElseThrow(() -> new CalendarServiceException(""));
        AcmOutlookUser outlookUser = getOutlookUser(user, auth);
        ExchangeService exchangeService = outlookDao.connect(outlookUser);
        if (!handler.checkPermission(exchangeService, user, auth, calendarEvent.getObjectId(), PermissionType.WRITE))
        {
            // TODO: add logging and proper exception message.
            throw new CalendarServiceException("");
        }
        try
        {
            Appointment appointment = new Appointment(exchangeService);
            ExchangeTypesConverter.setAppointmentProperties(appointment, calendarEvent, attachments);
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
    public void updateCalendarEvent(AcmUser user, Authentication auth, AcmCalendarEvent calendarEvent, MultipartFile[] attachments)
            throws CalendarServiceException
    {
        AcmOutlookUser outlookUser = getOutlookUser(user, auth);
        ExchangeService exchangeService = outlookDao.connect(outlookUser);

        try
        {
            CalendarEntityHandler handler = Optional.ofNullable(entityHandlers.get(calendarEvent.getObjectType()))
                    .orElseThrow(() -> new CalendarServiceException(""));
            if (!handler.checkPermission(exchangeService, user, auth, calendarEvent.getObjectId(), PermissionType.WRITE))
            {
                // TODO: add logging and proper exception message.
                throw new CalendarServiceException("");
            }
            Appointment appointment = Appointment.bind(exchangeService, new ItemId(calendarEvent.getEventId()));
            ExchangeTypesConverter.setAppointmentProperties(appointment, calendarEvent, attachments);
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

            appointment.update(ConflictResolutionMode.AlwaysOverwrite);

        } catch (Exception e)
        {
            throw new CalendarServiceException(e);
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
        AcmOutlookUser outlookUser = getOutlookUser(user, auth);
        ExchangeService exchangeService = outlookDao.connect(outlookUser);
        try
        {
            CalendarEntityHandler handler = Optional.ofNullable(entityHandlers.get(objectType))
                    .orElseThrow(() -> new CalendarServiceException(""));
            if (!handler.checkPermission(exchangeService, user, auth, objectId, PermissionType.DELETE))
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
        // TODO Auto-generated method stub
        return new ExchangeCalendarExcpetionMapper<>();
    }

    /**
     * @param user
     * @param handler
     * @param calendarEvent
     * @return
     * @throws CalendarServiceException
     */
    private AcmOutlookUser getOutlookUser(AcmUser user, Authentication auth) throws CalendarServiceException
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
