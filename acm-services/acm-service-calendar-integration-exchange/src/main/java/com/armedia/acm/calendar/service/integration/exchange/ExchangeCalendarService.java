package com.armedia.acm.calendar.service.integration.exchange;

import com.armedia.acm.calendar.service.AcmCalendar;
import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarEvent.Priority;
import com.armedia.acm.calendar.service.AcmCalendarInfo;
import com.armedia.acm.calendar.service.Attendee;
import com.armedia.acm.calendar.service.CalendarExceptionMapper;
import com.armedia.acm.calendar.service.CalendarService;
import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.calendar.service.RecurrenceDetails;
import com.armedia.acm.calendar.service.RecurrenceDetails.Daily;
import com.armedia.acm.calendar.service.RecurrenceDetails.Monthly;
import com.armedia.acm.calendar.service.RecurrenceDetails.WeekOfMonth;
import com.armedia.acm.calendar.service.RecurrenceDetails.Weekly;
import com.armedia.acm.calendar.service.RecurrenceDetails.Yearly;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookDTO;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.Importance;
import microsoft.exchange.webservices.data.core.enumeration.property.Sensitivity;
import microsoft.exchange.webservices.data.core.enumeration.property.time.DayOfTheWeek;
import microsoft.exchange.webservices.data.core.enumeration.property.time.DayOfTheWeekIndex;
import microsoft.exchange.webservices.data.core.enumeration.property.time.Month;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.DailyPattern;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.MonthlyPattern;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.RelativeMonthlyPattern;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.RelativeYearlyPattern;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.WeeklyPattern;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.YearlyPattern;
import microsoft.exchange.webservices.data.property.complex.time.OlsonTimeZoneDefinition;

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

    private Map<String, EntityHandler> entityHandlers;

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
        EntityHandler handler = Optional.ofNullable(entityHandlers.get(objectType)).orElseThrow(() -> new CalendarServiceException(""));
        handler.checkPermission(user, auth, objectId);
        return Optional.of(new ExchangeCalendar(handler, objectType, objectId));
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
        EntityHandler handler = Optional.ofNullable(entityHandlers.get(objectType)).orElseThrow(() -> new CalendarServiceException(""));
        return handler.listCalendars(user, auth, objectType, sort, sortDirection, start, maxItems);
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
        EntityHandler handler = Optional.ofNullable(entityHandlers.get(calendarEvent.getObjectType()))
                .orElseThrow(() -> new CalendarServiceException(""));
        handler.checkPermission(user, auth, calendarEvent.getObjectId());
        AcmOutlookUser outlookUser = getOutlookUser(user, auth);
        ExchangeService exchangeService = outlookDao.connect(outlookUser);
        // TODO: getFolderId should also consider if the event is associated with a restricted object or not.
        Folder folder = handler.getFolder(exchangeService, calendarEvent);
        try
        {
            Appointment appointment = new Appointment(exchangeService);
            setAppointmentProperties(appointment, calendarEvent, attachments);
            appointment.save(folder.getId());
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
            Appointment appointment = Appointment.bind(exchangeService, new ItemId(calendarEvent.getEventId()));
            Folder objectFolder = Folder.bind(exchangeService, appointment.getParentFolderId());
            String folderDisplayName = objectFolder.getDisplayName();
            String objectType = parseObjectType(folderDisplayName);
            String objectId = parseObjectId(folderDisplayName);
            EntityHandler handler = Optional.ofNullable(entityHandlers.get(objectType)).orElseThrow(() -> new CalendarServiceException(""));
            handler.checkPermission(user, auth, objectId);

            setAppointmentProperties(appointment, calendarEvent, attachments);

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
    public void deleteCalendarEvent(AcmUser user, Authentication auth, String calendarEventId) throws CalendarServiceException
    {
        AcmOutlookUser outlookUser = getOutlookUser(user, auth);
        ExchangeService exchangeService = outlookDao.connect(outlookUser);
        try
        {
            Appointment appointment = Appointment.bind(exchangeService, new ItemId(calendarEventId));
            Folder objectFolder = Folder.bind(exchangeService, appointment.getParentFolderId());
            String folderDisplayName = objectFolder.getDisplayName();
            String objectType = parseObjectType(folderDisplayName);
            String objectId = parseObjectId(folderDisplayName);
            EntityHandler handler = Optional.ofNullable(entityHandlers.get(objectType)).orElseThrow(() -> new CalendarServiceException(""));
            handler.checkPermission(user, auth, objectId);
            outlookDao.deleteAppointmentItem(exchangeService, calendarEventId, appointment.getIsRecurring(), DeleteMode.HardDelete);
        } catch (Exception e)
        {
            throw new CalendarServiceException(e);
        }
    }

    /**
     * @param folderDisplayName
     * @return
     */
    private String parseObjectId(String folderDisplayName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param folderDisplayName
     * @return
     */
    private String parseObjectType(String folderDisplayName)
    {
        // TODO Auto-generated method stub
        return null;
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
     * @param appointment
     * @param exchange
     * @param folder
     * @param calendarEvent
     * @throws Exception
     */
    private void setAppointmentProperties(Appointment appointment, AcmCalendarEvent calendarEvent, MultipartFile[] attachments)
            throws Exception
    {
        appointment.setSubject(calendarEvent.getSubject());
        appointment.setLocation(calendarEvent.getLocation());
        OlsonTimeZoneDefinition startTimeZoneDefinition = new OlsonTimeZoneDefinition(
                TimeZone.getTimeZone(calendarEvent.getStart().getZone().toString()));
        appointment.setStartTimeZone(startTimeZoneDefinition);
        Date startDate = Date.from(calendarEvent.getStart().toInstant());
        appointment.setStart(startDate);
        OlsonTimeZoneDefinition endTimeZoneDefinition = new OlsonTimeZoneDefinition(
                TimeZone.getTimeZone(calendarEvent.getEnd().getZone().toString()));
        appointment.setEndTimeZone(endTimeZoneDefinition);
        appointment.setEnd(Date.from(calendarEvent.getEnd().toInstant()));
        appointment.setIsAllDayEvent(calendarEvent.isAllDayEvent());
        RecurrenceDetails rc = calendarEvent.getRecurrenceDetails();
        Recurrence recurrence = null;
        if (rc != null)
        {
            switch (rc.getRecurrenceType())
            {
            case ONLY_ONCE:

                break;
            case DAILY:
                Daily daily = (Daily) rc;
                if (daily.getEveryWeekDay() != null && daily.getEveryWeekDay())
                {
                    recurrence = new WeeklyPattern(startDate, 1, DayOfTheWeek.Monday, DayOfTheWeek.Tuesday, DayOfTheWeek.Wednesday,
                            DayOfTheWeek.Thursday, DayOfTheWeek.Friday);
                } else
                {
                    recurrence = new DailyPattern(startDate, daily.getInterval());
                }
                break;
            case WEEKLY:
                Weekly weekly = (Weekly) rc;
                recurrence = new WeeklyPattern(startDate, weekly.getInterval(), convertDaysOfWeek(weekly));
                break;
            case MONTHLY:
                Monthly monthly = (Monthly) rc;
                if (monthly.getDay() != null)
                {
                    recurrence = new MonthlyPattern(startDate, monthly.getInterval(), monthly.getDay());
                } else
                {
                    recurrence = new RelativeMonthlyPattern(startDate, monthly.getInterval(), convertDayOfWeek(monthly.getDayOfWeek()),
                            convertWeekOfMonth(monthly.getWeekOfMonth()));
                }
                break;
            case YEARLY:
                Yearly yearly = (Yearly) rc;
                if (yearly.getDayOfMonth() != null)
                {
                    recurrence = new YearlyPattern(startDate, convertMonth(yearly.getMonth()), yearly.getDayOfMonth());
                } else
                {
                    recurrence = new RelativeYearlyPattern(startDate, convertMonth(yearly.getMonth()),
                            convertDayOfWeek(yearly.getDayOfWeek()), convertWeekOfMonth(yearly.getWeekOfMonth()));
                }
                break;
            }
            appointment.setRecurrence(recurrence);
        }
        appointment.setBody(MessageBody.getMessageBodyFromText(calendarEvent.getDetails()));
        if (calendarEvent.getRemindIn() != -1)
        {
            appointment.setIsReminderSet(true);
            appointment.setReminderMinutesBeforeStart(calendarEvent.getRemindIn());
            // appointment.setReminderDueBy(Date.from(calendarEvent.getEnd().minusMinutes(calendarEvent.getRemindIn()).toInstant()));
        } else
        {
            appointment.setIsReminderSet(false);
        }
        appointment.setSensitivity(convertSensitivity(calendarEvent.getSensitivity()));
        appointment.setImportance(convertImportance(calendarEvent.getPriority()));

        if (calendarEvent.getAttendees() != null)
        {
            for (Attendee attendee : calendarEvent.getAttendees())
            {
                switch (attendee.getType())
                {
                case REQUIRED:
                    appointment.getRequiredAttendees().add(attendee.getEmail());
                case OPTIONAL:
                    appointment.getOptionalAttendees().add(attendee.getEmail());
                case RESOURCE:
                    appointment.getResources().add(attendee.getEmail());
                }
            }
        }

        if (attachments != null)
        {
            for (MultipartFile attachment : attachments)
            {
                appointment.getAttachments().addFileAttachment(attachment.getName(), attachment.getInputStream());
            }
        }

    }

    /**
     * @param rc
     * @return
     */
    private DayOfTheWeek[] convertDaysOfWeek(Weekly rc)
    {
        List<DayOfTheWeek> daysOfWeek = new ArrayList<>();
        for (DayOfWeek dof : rc.getDays())
        {
            daysOfWeek.add(convertDayOfWeek(dof));
        }
        return daysOfWeek.toArray(new DayOfTheWeek[daysOfWeek.size()]);
    }

    private DayOfTheWeek convertDayOfWeek(DayOfWeek dof)
    {
        switch (dof)
        {
        case MONDAY:
            return DayOfTheWeek.Monday;
        case TUESDAY:
            return DayOfTheWeek.Tuesday;
        case WEDNESDAY:
            return DayOfTheWeek.Wednesday;
        case THURSDAY:
            return DayOfTheWeek.Thursday;
        case FRIDAY:
            return DayOfTheWeek.Friday;
        case SATURDAY:
            return DayOfTheWeek.Saturday;
        case SUNDAY:
            return DayOfTheWeek.Sunday;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param weekOfMonth
     * @return
     */
    private DayOfTheWeekIndex convertWeekOfMonth(WeekOfMonth weekOfMonth)
    {
        switch (weekOfMonth)
        {
        case FIRST:
            return DayOfTheWeekIndex.First;
        case SECOND:
            return DayOfTheWeekIndex.Second;
        case THIRD:
            return DayOfTheWeekIndex.Third;
        case FOURTH:
            return DayOfTheWeekIndex.Fourth;
        case LAST:
            return DayOfTheWeekIndex.Last;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param month
     * @return
     */
    private Month convertMonth(java.time.Month month)
    {
        switch (month)
        {
        case JANUARY:
            return Month.January;
        case FEBRUARY:
            return Month.February;
        case MARCH:
            return Month.March;
        case APRIL:
            return Month.April;
        case MAY:
            return Month.May;
        case JUNE:
            return Month.June;
        case JULY:
            return Month.July;
        case AUGUST:
            return Month.August;
        case SEPTEMBER:
            return Month.September;
        case OCTOBER:
            return Month.October;
        case NOVEMBER:
            return Month.November;
        case DECEMBER:
            return Month.December;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param priority
     * @return
     */
    private Importance convertImportance(Priority priority)
    {
        switch (priority)
        {
        case LOW:
            return Importance.Low;
        case NORMAL:
            return Importance.Normal;
        case HIGH:
            return Importance.High;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param sensitivity
     * @return
     */
    private Sensitivity convertSensitivity(com.armedia.acm.calendar.service.AcmCalendarEvent.Sensitivity sensitivity)
    {
        switch (sensitivity)
        {
        case CONFIDENTIAL:
            return Sensitivity.Confidential;
        case PRIVATE:
            return Sensitivity.Private;
        case PERSONAL:
            return Sensitivity.Personal;
        case NORMAL:
            return Sensitivity.Normal;
        default:
            throw new IllegalArgumentException();
        }
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
    public void setEntityHandlers(Map<String, EntityHandler> entityHandlers)
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
