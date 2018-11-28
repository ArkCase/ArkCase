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

import static com.armedia.acm.calendar.DateTimeAdjuster.guessTimeZone;

import com.armedia.acm.calendar.service.AcmCalendarEvent;
import com.armedia.acm.calendar.service.AcmCalendarEvent.Priority;
import com.armedia.acm.calendar.service.AcmCalendarEventAttachment;
import com.armedia.acm.calendar.service.Attendee;
import com.armedia.acm.calendar.service.Attendee.AttendeeType;
import com.armedia.acm.calendar.service.Attendee.ResponseStatus;
import com.armedia.acm.calendar.service.RecurrenceDetails;
import com.armedia.acm.calendar.service.RecurrenceDetails.Daily;
import com.armedia.acm.calendar.service.RecurrenceDetails.Monthly;
import com.armedia.acm.calendar.service.RecurrenceDetails.WeekOfMonth;
import com.armedia.acm.calendar.service.RecurrenceDetails.Weekly;
import com.armedia.acm.calendar.service.RecurrenceDetails.Yearly;
import com.armedia.acm.calendar.service.integration.exchange.exception.CalendarRecurrenceTypeException;
import com.armedia.acm.calendar.service.integration.exchange.exception.RecurrenceDetailsException;

import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.TimeZone;

import microsoft.exchange.webservices.data.core.XmlElementNames;
import microsoft.exchange.webservices.data.core.enumeration.property.Importance;
import microsoft.exchange.webservices.data.core.enumeration.property.MeetingResponseType;
import microsoft.exchange.webservices.data.core.enumeration.property.Sensitivity;
import microsoft.exchange.webservices.data.core.enumeration.property.time.DayOfTheWeek;
import microsoft.exchange.webservices.data.core.enumeration.property.time.DayOfTheWeekIndex;
import microsoft.exchange.webservices.data.core.enumeration.property.time.Month;
import microsoft.exchange.webservices.data.core.exception.misc.ArgumentOutOfRangeException;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceValidationException;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.complex.recurrence.DayOfTheWeekCollection;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.DailyPattern;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.MonthlyPattern;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.RelativeMonthlyPattern;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.RelativeYearlyPattern;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.WeeklyPattern;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.YearlyPattern;
import microsoft.exchange.webservices.data.property.complex.time.OlsonTimeZoneDefinition;
import microsoft.exchange.webservices.data.property.complex.time.TimeZoneDefinition;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 26, 2017
 *
 */
public class ExchangeTypesConverter
{

    /**
     *
     * @param appointment
     * @param calendarEvent
     * @param attachments
     * @param updateRecurrence
     * @throws Exception
     */
    static void setAppointmentProperties(Appointment appointment, AcmCalendarEvent calendarEvent, MultipartFile[] attachments,
            boolean updateRecurrence) throws Exception
    {
        appointment.setSubject(calendarEvent.getSubject());
        appointment.setLocation(calendarEvent.getLocation());
        if (calendarEvent.getStart() == null)
        {
            calendarEvent.setStart(ZonedDateTime.now());
        }
        OlsonTimeZoneDefinition startTimeZoneDefinition = new OlsonTimeZoneDefinition(
                TimeZone.getTimeZone(calendarEvent.getStart().getZone()));
        appointment.setStartTimeZone(startTimeZoneDefinition);
        Date startDate = calendarEvent.isAllDayEvent()
                ? Date.from(calendarEvent.getStart().toLocalDate().atStartOfDay(calendarEvent.getStart().getZone()).toInstant())
                : Date.from(calendarEvent.getStart().toInstant());
        appointment.setStart(startDate);
        if (calendarEvent.getEnd() == null)
        {
            calendarEvent.setEnd(ZonedDateTime.now().plusHours(1));
        }
        OlsonTimeZoneDefinition endTimeZoneDefinition = new OlsonTimeZoneDefinition(TimeZone.getTimeZone(calendarEvent.getEnd().getZone()));
        appointment.setEndTimeZone(endTimeZoneDefinition);
        Date endDate = calendarEvent.isAllDayEvent()
                ? Date.from(calendarEvent.getStart().toLocalDate().atStartOfDay(calendarEvent.getStart().getZone()).plusDays(1).toInstant())
                : Date.from(calendarEvent.getEnd().toInstant());
        appointment.setEnd(endDate);
        appointment.setIsAllDayEvent(calendarEvent.isAllDayEvent());

        processRecurrence(appointment, calendarEvent, updateRecurrence);

        appointment.setBody(MessageBody.getMessageBodyFromText(calendarEvent.getDetails()));
        if (calendarEvent.getRemindIn() != -1)
        {
            appointment.setIsReminderSet(true);
            appointment.setReminderMinutesBeforeStart(calendarEvent.getRemindIn());
            // appointment.setReminderDueBy(Date.from(calendarEvent.getEnd().minusMinutes(calendarEvent.getRemindIn()).toInstant()));
        }
        else
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
                appointment.getAttachments().addFileAttachment(attachment.getOriginalFilename(), attachment.getInputStream());
            }
        }

    }

    /**
     * @param appointment
     * @param calendarEvent
     * @param updateRecurrence
     * @throws ArgumentOutOfRangeException
     * @throws Exception
     */
    private static void processRecurrence(Appointment appointment, AcmCalendarEvent calendarEvent, boolean updateRecurrence)
            throws ArgumentOutOfRangeException, Exception
    {
        RecurrenceDetails rc = calendarEvent.getRecurrenceDetails();
        Recurrence recurrence = null;
        if (updateRecurrence && rc != null)
        {

            ZonedDateTime startAt = rc.getStartAt();
            if (startAt == null)
            {
                startAt = calendarEvent.getStart();
            }
            Date recurrenceStartAt = Date.from(startAt.toInstant());
            switch (rc.getRecurrenceType())
            {
            case ONLY_ONCE:

                break;
            case DAILY:
                Daily daily = (Daily) rc;
                if (daily.getEveryWeekDay() != null && daily.getEveryWeekDay())
                {
                    recurrence = new DailyPattern(recurrenceStartAt, 1);
                }
                else
                {
                    recurrence = new DailyPattern(recurrenceStartAt, daily.getInterval());
                }
                break;
            case WEEKLY:
                Weekly weekly = (Weekly) rc;
                recurrence = new WeeklyPattern(recurrenceStartAt, weekly.getInterval(), convertDaysOfWeek(weekly));
                break;
            case MONTHLY:
                Monthly monthly = (Monthly) rc;
                if (monthly.getDay() != null)
                {
                    recurrence = new MonthlyPattern(recurrenceStartAt, monthly.getInterval(), monthly.getDay());
                }
                else
                {
                    recurrence = new RelativeMonthlyPattern(recurrenceStartAt, monthly.getInterval(),
                            convertDayOfWeek(monthly.getDayOfWeek()), convertWeekOfMonth(monthly.getWeekOfMonth()));
                }
                break;
            case YEARLY:
                Yearly yearly = (Yearly) rc;
                if (yearly.getDayOfMonth() != null)
                {
                    recurrence = new YearlyPattern(recurrenceStartAt, convertMonth(yearly.getMonth()), yearly.getDayOfMonth());
                }
                else
                {
                    recurrence = new RelativeYearlyPattern(recurrenceStartAt, convertMonth(yearly.getMonth()),
                            convertDayOfWeek(yearly.getDayOfWeek()), convertWeekOfMonth(yearly.getWeekOfMonth()));
                }
                break;
            default:
                throw new CalendarRecurrenceTypeException(String.format("Calendar recurrence type unknown: %s", rc.getRecurrenceType()));
            }
            Integer endAfterOccurrances = rc.getEndAfterOccurrances();
            if (recurrence != null)
            {
                if (endAfterOccurrances != null)
                {
                    recurrence.setNumberOfOccurrences(endAfterOccurrances);
                }
                ZonedDateTime endBy = rc.getEndBy();
                if (endBy != null)
                {
                    recurrence.setEndDate(Date.from(endBy.toInstant()));
                }
                appointment.setRecurrence(recurrence);
            }
        }
    }

    /**
     * @param rc
     * @return
     */
    private static DayOfTheWeek[] convertDaysOfWeek(Weekly rc)
    {
        List<DayOfTheWeek> daysOfWeek = new ArrayList<>();
        for (DayOfWeek dof : rc.getDays())
        {
            daysOfWeek.add(convertDayOfWeek(dof));
        }
        return daysOfWeek.toArray(new DayOfTheWeek[daysOfWeek.size()]);
    }

    private static DayOfTheWeek convertDayOfWeek(DayOfWeek dof)
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
    private static DayOfTheWeekIndex convertWeekOfMonth(WeekOfMonth weekOfMonth)
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
    private static Month convertMonth(java.time.Month month)
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
    private static Importance convertImportance(Priority priority)
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
    private static Sensitivity convertSensitivity(AcmCalendarEvent.Sensitivity sensitivity)
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
     * @param event
     * @param appointment
     * @throws Exception
     */
    static void setEventProperties(AcmCalendarEvent event, Appointment appointment) throws Exception
    {
        // TODO set organizer
        // event.setCreatorId(appointment.getOrganizer().getAddress());
        event.setEventId(appointment.getId().getUniqueId());
        event.setObjectType("CASE_FILE");
        event.setSubject(appointment.getSubject());
        event.setLocation(appointment.getLocation());

        TimeZoneDefinition startTimeZoneDefinititon = appointment.getStartTimeZone();
        ZoneId startTimeZone = ZoneId.of(guessTimeZone(startTimeZoneDefinititon.getId()));
        event.setStart(ZonedDateTime.ofInstant(appointment.getStart().toInstant(), startTimeZone));

        TimeZoneDefinition endTimeZoneDefinition = appointment.getEndTimeZone();
        ZoneId endTimeZone = ZoneId.of(guessTimeZone(endTimeZoneDefinition.getId()));
        event.setEnd(ZonedDateTime.ofInstant(appointment.getEnd().toInstant(), endTimeZone));

        event.setAllDayEvent(appointment.getIsAllDayEvent());

        processRecurrence(event, appointment, startTimeZone, endTimeZone);

        event.setDetails(MessageBody.getStringFromMessageBody(appointment.getBody()));

        if (appointment.getIsReminderSet())
        {
            event.setRemindIn(appointment.getReminderMinutesBeforeStart());
        }

        event.setSensitivity(convertSensitivity(appointment.getSensitivity()));

        event.setPriority(convertImportance(appointment.getImportance()));

        List<Attendee> attendees = new ArrayList<>();
        appointment.getRequiredAttendees().forEach(a -> {
            Attendee attendee = processAttendee(a, AttendeeType.REQUIRED);
            attendees.add(attendee);
        });
        appointment.getOptionalAttendees().forEach(a -> {
            Attendee attendee = processAttendee(a, AttendeeType.OPTIONAL);
            attendees.add(attendee);
        });
        appointment.getResources().forEach(a -> {
            Attendee attendee = processAttendee(a, AttendeeType.RESOURCE);
            attendees.add(attendee);
        });
        event.setAttendees(attendees);

        List<AcmCalendarEventAttachment> files = new ArrayList<>();
        String appointmentId = appointment.getId().getUniqueId();
        appointment.getAttachments().forEach(att -> {
            files.add(new AcmCalendarEventAttachment(att.getName(), att.getId(), appointmentId));
        });
        event.setFiles(files);
    }

    private static Attendee processAttendee(microsoft.exchange.webservices.data.property.complex.Attendee a, AttendeeType attendeeType)
    {
        Attendee attendee = new Attendee();
        attendee.setEmail(a.getAddress());
        attendee.setType(attendeeType);
        attendee.setStatus(convertResponseType(a.getResponseType()));
        return attendee;
    }

    /**
     * @param event
     * @param appointment
     * @param startTimeZone
     * @param endTimeZone
     * @throws ServiceLocalException
     * @throws ServiceValidationException
     */
    private static void processRecurrence(AcmCalendarEvent event, Appointment appointment, ZoneId startTimeZone, ZoneId endTimeZone)
            throws ServiceLocalException, ServiceValidationException
    {
        Recurrence recurrence = appointment.getRecurrence();
        RecurrenceDetails recurrenceDetails = null;
        if (recurrence != null)
        {
            switch (recurrence.getXmlElementName())
            {
            case XmlElementNames.DailyRecurrence:
                DailyPattern dp = (DailyPattern) recurrence;
                Daily daily = new Daily();
                daily.setInterval(dp.getInterval());
                recurrenceDetails = daily;
                break;
            case XmlElementNames.WeeklyRecurrence:
                WeeklyPattern wp = (WeeklyPattern) recurrence;
                Weekly weekly = new Weekly();
                weekly.setInterval(wp.getInterval());
                weekly.setDays(convertDaysOfWeek(wp.getDaysOfTheWeek()));
                recurrenceDetails = weekly;
                break;
            case XmlElementNames.AbsoluteMonthlyRecurrence:
                MonthlyPattern mp = (MonthlyPattern) recurrence;
                Monthly monthly = new Monthly();
                monthly.setInterval(mp.getInterval());
                monthly.setDay(mp.getDayOfMonth());
                recurrenceDetails = monthly;
                break;
            case XmlElementNames.RelativeMonthlyRecurrence:
                RelativeMonthlyPattern rmp = (RelativeMonthlyPattern) recurrence;
                Monthly relativeMonthly = new Monthly();
                relativeMonthly.setInterval(rmp.getInterval());
                relativeMonthly.setDayOfWeek(convertDayOfWeek(rmp.getDayOfTheWeek()));
                relativeMonthly.setWeekOfMonth(convertWeekOfTheMonth(rmp.getDayOfTheWeekIndex()));
                recurrenceDetails = relativeMonthly;
                break;
            case XmlElementNames.AbsoluteYearlyRecurrence:
                YearlyPattern yp = (YearlyPattern) recurrence;
                Yearly yearly = new Yearly();
                yearly.setMonth(convertMonth(yp.getMonth()));
                yearly.setDayOfMonth(yp.getDayOfMonth());
                recurrenceDetails = yearly;
                break;
            case XmlElementNames.RelativeYearlyRecurrence:
                RelativeYearlyPattern ryp = (RelativeYearlyPattern) recurrence;
                Yearly relativeYearly = new Yearly();
                relativeYearly.setMonth(convertMonth(ryp.getMonth()));
                relativeYearly.setDayOfWeek(convertDayOfWeek(ryp.getDayOfTheWeek()));
                relativeYearly.setWeekOfMonth(convertWeekOfTheMonth(ryp.getDayOfTheWeekIndex()));
                recurrenceDetails = relativeYearly;
                break;
            default:
                throw new RecurrenceDetailsException("Recurrence details unknown");
            }
            if (recurrence.getNumberOfOccurrences() != null)
            {
                recurrenceDetails.setEndAfterOccurrances(recurrence.getNumberOfOccurrences());
            }
            if (recurrence.getStartDate() != null)
            {
                recurrenceDetails.setStartAt(ZonedDateTime.ofInstant(recurrence.getStartDate().toInstant(), startTimeZone));
            }
            if (recurrence.getEndDate() != null)
            {
                recurrenceDetails.setEndBy(ZonedDateTime.ofInstant(recurrence.getEndDate().toInstant(), endTimeZone));
            }
            event.setRecurrenceDetails(recurrenceDetails);

        }
    }

    /**
     * @param daysOfTheWeek
     * @return
     */
    private static EnumSet<DayOfWeek> convertDaysOfWeek(DayOfTheWeekCollection daysOfTheWeek)
    {
        List<DayOfWeek> dow = new ArrayList<>();
        daysOfTheWeek.forEach(day -> dow.add(convertDayOfWeek(day)));
        EnumSet<DayOfWeek> daysOfWeek = EnumSet.copyOf(dow);
        return daysOfWeek;
    }

    /**
     * @param day
     * @return
     */
    private static DayOfWeek convertDayOfWeek(DayOfTheWeek day)
    {
        switch (day)
        {
        case Monday:
            return DayOfWeek.MONDAY;
        case Tuesday:
            return DayOfWeek.TUESDAY;
        case Wednesday:
            return DayOfWeek.WEDNESDAY;
        case Thursday:
            return DayOfWeek.THURSDAY;
        case Friday:
            return DayOfWeek.FRIDAY;
        case Saturday:
            return DayOfWeek.SATURDAY;
        case Sunday:
            return DayOfWeek.SUNDAY;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param dayOfTheWeekIndex
     * @return
     */
    private static WeekOfMonth convertWeekOfTheMonth(DayOfTheWeekIndex dayOfTheWeekIndex)
    {
        switch (dayOfTheWeekIndex)
        {
        case First:
            return WeekOfMonth.FIRST;
        case Second:
            return WeekOfMonth.SECOND;
        case Third:
            return WeekOfMonth.THIRD;
        case Fourth:
            return WeekOfMonth.FOURTH;
        case Last:
            return WeekOfMonth.LAST;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param month
     * @return
     */
    private static java.time.Month convertMonth(Month month)
    {
        switch (month)
        {
        case January:
            return java.time.Month.JANUARY;
        case February:
            return java.time.Month.FEBRUARY;
        case March:
            return java.time.Month.MARCH;
        case April:
            return java.time.Month.APRIL;
        case May:
            return java.time.Month.MAY;
        case June:
            return java.time.Month.JUNE;
        case July:
            return java.time.Month.JULY;
        case August:
            return java.time.Month.AUGUST;
        case September:
            return java.time.Month.SEPTEMBER;
        case October:
            return java.time.Month.OCTOBER;
        case November:
            return java.time.Month.NOVEMBER;
        case December:
            return java.time.Month.DECEMBER;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param sensitivity
     * @return
     */
    private static AcmCalendarEvent.Sensitivity convertSensitivity(Sensitivity sensitivity)
    {
        switch (sensitivity)
        {
        case Normal:
            return AcmCalendarEvent.Sensitivity.NORMAL;
        case Personal:
            return AcmCalendarEvent.Sensitivity.PERSONAL;
        case Private:
            return AcmCalendarEvent.Sensitivity.PRIVATE;
        case Confidential:
            return AcmCalendarEvent.Sensitivity.CONFIDENTIAL;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param importance
     * @return
     */
    private static Priority convertImportance(Importance importance)
    {
        switch (importance)
        {
        case Low:
            return Priority.LOW;
        case Normal:
            return Priority.NORMAL;
        case High:
            return Priority.HIGH;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     *
     * @param responseType
     * @return
     */
    private static ResponseStatus convertResponseType(MeetingResponseType responseType)
    {
        switch (responseType)
        {
        case Unknown:
        case NoResponseReceived:
            return ResponseStatus.NONE;
        case Tentative:
            return ResponseStatus.TENTATIVE;
        case Accept:
            return ResponseStatus.ACCEPTED;
        case Decline:
            return ResponseStatus.DECLINED;
        case Organizer:
            return ResponseStatus.ORGANIZER;
        default:
            throw new IllegalArgumentException(String.format("Non valid event responsetype [%s].", responseType.name()));
        }
    }

}
